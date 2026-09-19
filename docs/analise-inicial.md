# Análise inicial: Missão Marte Unifor (`src/exercicio10`)

Aluno: Matheus Correia
Data: 19/09/2026

## 1. Testes do código original

Compilei e executei o código original e testei os fluxos abaixo. Todos funcionaram.

| Requisito | Resultado |
| --- | --- |
| Iniciar nova missão pelo menu | OK |
| Escolher piloto, dificuldade e tamanho do mapa | OK |
| Movimentar a nave e embarcar passageiros | OK |
| Detectar colisões e encerramento da missão | OK |
| Exibir estatísticas ao final | OK |
| Consultar o ranking | OK |
| Resetar o ranking | OK |
| Persistir pontuações | OK |
| Compilar e executar sem erros | OK |

Comandos usados:

```bash
mkdir -p out && javac -encoding UTF-8 -d out src/exercicio10/*.java
java -cp out exercicio10.Main
```

## 2. Responsabilidades concentradas no `Main`

O `Main.java` tem 557 linhas e faz muitas coisas diferentes. Cada linha da tabela é um motivo diferente para a classe mudar.

| # | Responsabilidade | Linhas | Para onde vai |
| --- | --- | --- | --- |
| 1 | Menu e fluxo da aplicação | 28-77 | `Main` e `JogoService` |
| 2 | Leitura e validação da entrada | 216-230, 382-390 | `JogoService` |
| 3 | Regras da partida (pontos, movimento, colisão, vitória, derrota) | 79-198 | `JogoService` |
| 4 | Configuração por dificuldade | 232-256 | `JogoService` |
| 5 | Criação da missão (sorteio de posições e passageiros) | 240-314 | `JogoService` |
| 6 | Desenho do mapa, estatísticas e ranking | 62-77, 200-214, 316-380, 392-404 | `MapaRenderer` |
| 7 | Regra do Top 5 | 175-190, 422-427 | `JogoService` |
| 8 | Persistência do ranking (JSON manual, ler, gravar, apagar) | 406-556 | `RankingRepository` e `RankingService` |
| 9 | Classe `RankingEntry` dentro do `Main` | 540-556 | `repository` |

Por que isso é um problema: se eu quiser mudar o formato do arquivo de ranking, preciso mexer no mesmo arquivo que desenha o mapa e controla a partida. Uma alteração pequena pode quebrar outra parte sem querer, e não dá para testar a regra do jogo sem passar pelo console e pelo arquivo.

## 3. Violações encontradas

```text
Local: Main.criarPassageiroPolimorfico (linha 292) e Main.desenharMapa (linhas 339-345)
Princípio relacionado: OCP
Observação: o tipo do passageiro é escolhido com switch e o símbolo no mapa com instanceof.
Impacto: para criar um novo tipo de passageiro é preciso editar dois lugares do Main.
Proposta: cada passageiro informa o próprio símbolo, sem instanceof no renderizador.
Prioridade: alta
```

```text
Local: Main.loadRanking, saveRanking e resetarRanking (linhas 406-469)
Princípio relacionado: DIP
Observação: a regra do jogo depende direto de Files, Path e do formato do arquivo.
Impacto: trocar por banco de dados ou memória exige alterar o Main, e não dá para testar sem disco.
Proposta: criar a interface RankingRepository e fazer o serviço depender só dela.
Prioridade: alta
```

```text
Local: Main.criarNovaMissao (linhas 259-287)
Princípio relacionado: SRP
Observação: três laços quase iguais sorteiam posições para passageiros, asteroides e inimigos.
Impacto: código duplicado; qualquer ajuste no sorteio precisa ser feito três vezes.
Proposta: um único método de sorteio de posição livre reaproveitado pelos três.
Prioridade: média
```

```text
Local: Passageiro, Professor, Engenheiro, Astronauta
Princípio relacionado: LSP
Observação: cada subclasse só sobrescreve getPontuacao() e passa o tipo pelo construtor como String.
Impacto: as subclasses podem substituir a base sem problema, mas o tipo como texto permite valores incoerentes.
Proposta: tornar Passageiro abstrata e deixar cada subclasse definir seu tipo e pontuação.
Prioridade: baixa
```

## 4. Comportamentos que preciso preservar

- O ranking é recarregado do arquivo depois de cada partida.
- O reset apaga o arquivo e deixa o ranking vazio.
- A vitória exige todos os passageiros embarcados e a nave em (0,0).
- Cada movimento custa 1 ponto, e a partida acaba se os pontos chegarem a 0.

## 5. Diferenças entre o original e o tutorial

Ao comparar o original com o `tutorialSolid.md`, encontrei diferenças que preciso decidir:

- **Pontuação dos passageiros:** no original, Professor vale 10, Engenheiro 15 e Astronauta 20. O tutorial diz 15, 20 e 10. Vou manter os valores do original para preservar o comportamento, e registrar isso na revisão.
- **Formato do ranking:** o original grava JSON. O código de referência do tutorial grava linhas separadas por `|`. Ainda preciso decidir qual usar.
- **Sorteio e capacidade:** o tutorial pede posições em todo o mapa, nave com capacidade para todos os passageiros e contagem a bordo, restantes e total. Vou registrar como mudanças intencionais.

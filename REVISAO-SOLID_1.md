# Revisão final da atividade

Nome: André Paixão
Data: 24/09/2026

## Como validei a solução

- [x] compilação do código inicial;
- [x] compilação da versão refatorada;
- [x] início de uma missão;
- [x] movimentação, embarque e conclusão da missão;
- [x] consulta e reset do ranking;
- [x] outro teste: checagem estática do pacote `model` (`grep` por
      `instanceof`, `Scanner` e `java.io`/`java.nio`) — nenhuma ocorrência, o
      que confirma que o domínio não conhece entrada de dados nem arquivo.

Os testes do código original (menu, dificuldade, movimentação, colisão,
ranking e persistência) estão detalhados em `docs/analise-inicial.md`. A
compilação por etapa segue os comandos da seção 7 do `tutorialSolid.md`:

```powershell
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path out | Out-Null
javac -d out (Get-ChildItem -Recurse -Filter *.java -Path src/solidexercicio10 | ForEach-Object FullName)
java -cp out solidexercicio10.Main
```

## Achados da revisão

```text
Local: solidexercicio10.model (Nave, Missao, Passageiro e subclasses)
Princípio relacionado: SRP
Observação: cada classe do domínio tem um único motivo para mudar — Nave
cuida de posição/vidas/capacidade, Missao cuida de embarque e colisão, e cada
subclasse de Passageiro cuida só da própria pontuação e símbolo.
Impacto: alterar a regra de pontuação de um tipo de passageiro não exige
tocar em Nave, Missao nem nas demais subclasses.
Proposta: manter assim; se Missao continuar recebendo regras novas (ex.:
combustível, clima), avaliar extrair uma classe de regras de colisão.
Prioridade: baixa
```

```text
Local: solidexercicio10.presentation.MapaRenderer / solidexercicio10.model.Passageiro
Princípio relacionado: OCP
Observação: MapaRenderer lê o símbolo com p.getSimbolo(), sem instanceof.
Um novo tipo de passageiro só precisa herdar de Passageiro e definir seu
símbolo e pontuação.
Impacto: extensão sem editar o renderer nem o fluxo do JogoService,
diferente do Main.desenharMapa original (instanceof em cada tipo).
Proposta: manter o contrato; documentar no README que novos passageiros só
entram no domínio, nunca na apresentação.
Prioridade: baixa
```

```text
Local: solidexercicio10.model.Passageiro e subclasses (Professor, Engenheiro, Astronauta)
Princípio relacionado: LSP
Observação: as três subclasses substituem Passageiro em List<Passageiro> sem
overrides inesperados; getPontuacao() e getSimbolo() sempre devolvem valores
coerentes com o tipo.
Impacto: JogoService.embarcarPassageiroNaPosicao trata qualquer subclasse do
mesmo jeito, sem checagens de tipo.
Proposta: nenhuma ação corretiva; sugerido apenas cobrir esse comportamento
com um teste que embarca um de cada tipo e confere a pontuação somada.
Prioridade: baixa
```

```text
Local: solidexercicio10.model.Posicionavel e Movel
Princípio relacionado: ISP
Observação: Posicionavel só declara coordenadas; Movel só declara
movimento. EntidadeMapa implementa Posicionavel, e só Nave/Inimigo
implementam Movel — Passageiro fica parado e não é forçado a ter mover().
Impacto: nenhuma classe precisa implementar método que não usa.
Proposta: manter as duas interfaces separadas; não fundir em uma só mesmo
que pareça "mais simples" perderia a distinção entre objeto parado e
móvel.
Prioridade: baixa
```

```text
Local: solidexercicio10.service.JogoService / solidexercicio10.repository.RankingRepository
Princípio relacionado: DIP
Observação: JogoService recebe RankingRepository no construtor (interface),
não RankingService (implementação em arquivo). Quem escolhe a
implementação concreta é o Main.
Impacto: dá para trocar a persistência por memória ou banco sem alterar
JogoService, e dá para testar o fluxo do jogo com um repositório falso em
memória.
Proposta: ao escrever testes automatizados da equipe, criar um
RankingRepositoryEmMemoria só para teste, sem tocar em arquivo.
Prioridade: média
```

## Decisões com as quais concordo

Concordo com a decisão de o `JogoService` depender de `RankingRepository`
em vez de `RankingService` (DIP). No código original, `Main` chamava
`Files`/`Path` direto no meio da regra da partida — qualquer teste da lógica
do jogo exigia ler e apagar arquivo do disco. Com a interface, o benefício é
duplo: a persistência fica substituível (arquivo, memória ou banco) e a
regra do jogo passa a ser testável isoladamente. O custo uma interface a
mais é pequeno perto do ganho de testabilidade.

## Decisões com as quais não concordo

Não concordo em deixar toda a criação da missão (sorteio de posições,
passageiros e perigos) dentro de `JogoService.jogar`. Como o próprio
`docs/analise-inicial.md` já registrou, o código original tinha três laços
quase iguais para sortear posição livre; a versão refatorada resolve a
duplicação com o método `livre()`, mas ainda mistura "criar a missão" com
"rodar o loop do jogo" na mesma classe. Para um projeto deste tamanho o
custo de separar agora não compensa, mas se a criação da missão ganhar mais
regras (ex.: dificuldades customizadas, eventos aleatórios), eu extrairia
uma `MissaoFactory` o próprio tutorial sugere isso na reflexão da etapa 4.

## Melhoria implementada (opcional)

Proposta não implementada nesta entrega, registrada para prioridade futura:
extrair o cálculo de pontuação inicial por dificuldade
(`dificuldade == Dificuldade.FACIL ? 30 : ...` em `JogoService.jogar`) para
um método `Dificuldade.pontuacaoInicial()`. Hoje a regra de pontos por
dificuldade está no enum (tabela da seção 6) mas o valor inicial é decidido
no `service`. Mover o valor para o enum reúne toda regra de pontuação da
dificuldade em um único lugar (SRP) e evita que um novo nível de
dificuldade exija editar `JogoService`.

## Prioridade das melhorias

| Prioridade | Melhoria |
| --- | --- |
| Alta | Nenhuma pendência de alta prioridade nesta revisão as violações de alta prioridade do código original (OCP no switch/instanceof de passageiro e DIP na persistência) já foram resolvidas na refatoração. |
| Média | Repositório de ranking em memória para testes automatizados da equipe. |
| Baixa | Mover a pontuação inicial por dificuldade para `Dificuldade.pontuacaoInicial()`; considerar `MissaoFactory` se a criação da missão crescer. |

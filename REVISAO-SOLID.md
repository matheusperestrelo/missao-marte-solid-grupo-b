# Revisão final da atividade

**Nome:** João Gabriel Rocha Cavalcante
**Data:** 24/09/2026

## Como validei a solução

Foram realizados testes na versão inicial e na versão refatorada do projeto, verificando a compilação e o funcionamento do jogo.

* [x] compilação do código inicial;
* [x] compilação da versão refatorada;
* [x] início de uma missão;
* [x] movimentação, embarque e conclusão da missão;
* [x] consulta e reset do ranking;
* [x] teste do menu principal e encerramento do jogo.

### Comandos utilizados

Para compilar a versão refatorada:

```powershell
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path out | Out-Null
javac -d out (Get-ChildItem -Recurse -Filter *.java -Path src/solidexercicio10 | ForEach-Object FullName)
```

Para executar:

```powershell
java -cp out solidexercicio10.Main
```

Durante os testes, foram verificadas as principais funcionalidades da aplicação, incluindo o início de uma missão, movimentação da nave, embarque dos passageiros, conclusão da missão e funcionamento do ranking.

Também foi identificado e corrigido um problema na representação visual da movimentação vertical. O problema estava na ordem de impressão das linhas do mapa no `MapaRenderer`, fazendo com que os comandos `w` e `s` parecessem invertidos. A renderização foi ajustada para apresentar o eixo Y de forma compatível com a movimentação esperada.

---

## Achados da revisão

### Ponto 1

```text
Local: JogoService, MapaRenderer e RankingRepository
Princípio relacionado: Single Responsibility Principle (SRP)
Observação: As responsabilidades do sistema foram separadas entre classes diferentes. O JogoService concentra o fluxo da partida, o MapaRenderer é responsável pela apresentação do mapa e o RankingRepository trata do armazenamento e consulta do ranking.
Impacto: A separação facilita a manutenção e evita que uma única classe fique responsável por muitas tarefas diferentes.
Proposta: Manter a separação atual e, futuramente, separar também o tratamento dos comandos de entrada do jogador.
Prioridade: Média
```

### Ponto 2

```text
Local: Passageiro, Professor, Engenheiro e Astronauta
Princípio relacionado: Open/Closed Principle (OCP)
Observação: A classe Passageiro funciona como uma abstração para os diferentes tipos de passageiros. Novos tipos podem ser adicionados criando novas subclasses sem precisar alterar toda a estrutura existente.
Impacto: A estrutura fica mais preparada para receber novos tipos de passageiros e novas regras.
Proposta: Manter a utilização da classe abstrata Passageiro para representar os diferentes tipos.
Prioridade: Média
```

### Ponto 3

```text
Local: Professor, Engenheiro e Astronauta
Princípio relacionado: Liskov Substitution Principle (LSP)
Observação: As subclasses de Passageiro possuem as características esperadas de um Passageiro e podem ser utilizadas onde um Passageiro é esperado.
Impacto: O código pode trabalhar com uma lista de Passageiro sem precisar tratar cada tipo separadamente em todas as situações.
Proposta: Manter a estrutura de herança utilizada na refatoração.
Prioridade: Baixa
```

### Ponto 4

```text
Local: Posicionavel e Movel
Princípio relacionado: Interface Segregation Principle (ISP)
Observação: Foram criadas interfaces pequenas e específicas. Posicionavel representa objetos que possuem posição e Movel representa objetos que podem se movimentar.
Impacto: As classes não precisam implementar métodos que não utilizam, reduzindo o acoplamento.
Proposta: Continuar utilizando interfaces específicas quando novas funcionalidades forem adicionadas.
Prioridade: Baixa
```

### Ponto 5

```text
Local: JogoService e RankingRepository
Princípio relacionado: Dependency Inversion Principle (DIP)
Observação: O JogoService recebe um RankingRepository em vez de depender diretamente da implementação concreta RankingService.
Impacto: Isso permite trocar a implementação do ranking sem precisar alterar a lógica principal do jogo.
Proposta: Aplicar o mesmo princípio futuramente em outras partes que possam precisar de diferentes implementações.
Prioridade: Média
```

---

## Decisões com as quais concordo

Concordo com a decisão de separar a lógica do jogo da parte responsável pela apresentação.

A utilização do `MapaRenderer` para cuidar da exibição do mapa evita que a lógica principal da missão fique misturada com comandos de impressão no console.

Essa separação facilita futuras alterações na apresentação do sistema. Por exemplo, seria possível criar uma nova forma de apresentação, como uma interface gráfica, sem precisar modificar todas as regras da missão.

Também facilita a manutenção, pois cada parte do sistema possui uma responsabilidade mais específica.

---

## Decisões com as quais não concordo

Uma alteração que eu faria seria separar o tratamento dos comandos de entrada do jogador do `JogoService`.

Atualmente, o `JogoService` participa do fluxo de leitura e tratamento dos comandos do usuário. Para uma aplicação maior, seria interessante criar uma classe específica para receber e interpretar os comandos.

Isso deixaria o `JogoService` mais concentrado nas regras e no fluxo da missão, facilitando testes automatizados e futuras mudanças na forma de entrada.

Por exemplo, futuramente os comandos poderiam vir de uma interface gráfica em vez do teclado, sem precisar modificar diretamente a lógica da missão.

---

## Melhoria implementada

Durante os testes foi identificado um problema na movimentação vertical da nave.

Os comandos `w` e `s` estavam funcionando de acordo com a alteração da coordenada Y, porém a forma como o `MapaRenderer` imprimia as linhas fazia com que a movimentação parecesse visualmente invertida no terminal.

A alteração realizada foi na ordem de impressão do eixo Y.

Antes:

```java
for (int y = minY; y <= maxY; y++) {
```

Depois:

```java
for (int y = maxY; y >= minY; y--) {
```

Com isso, a representação do mapa passou a ficar compatível com a movimentação esperada pelo jogador.

Essa alteração não modificou a regra de movimentação da nave. Ela corrigiu apenas a forma como a posição era apresentada no terminal, mantendo a lógica do jogo separada da apresentação.

---

## Conclusão

A refatoração melhorou a organização do projeto ao separar responsabilidades e aplicar os princípios SOLID.

A estrutura atual facilita a manutenção do código e permite futuras alterações, principalmente na apresentação, no ranking e na inclusão de novos tipos de entidades.

Como melhorias futuras, seria interessante implementar testes automatizados e separar o tratamento da entrada do usuário da lógica principal do jogo.

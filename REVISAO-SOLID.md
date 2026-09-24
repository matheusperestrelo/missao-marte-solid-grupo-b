# Revisão SOLID — Missão Marte

## 1. Observação sobre cada princípio SOLID

### S — Single Responsibility Principle (SRP)
A refatoração separou responsabilidades que antes estavam concentradas em poucas classes. O `JogoService` ficou responsável pela lógica do jogo, enquanto o `MapaRenderer` ficou responsável pela apresentação e o `RankingRepository` pela persistência do ranking. Isso facilita a manutenção, pois uma alteração em uma responsabilidade tende a afetar menos partes do sistema.

### O — Open/Closed Principle (OCP)
A utilização de abstrações e interfaces facilita a inclusão de novos comportamentos sem precisar modificar diretamente todas as classes existentes. Por exemplo, novos tipos de passageiros podem ser criados a partir de `Passageiro`, mantendo a estrutura principal do jogo.

### L — Liskov Substitution Principle (LSP)
As subclasses de `Passageiro`, como `Professor`, `Engenheiro` e `Astronauta`, podem ser utilizadas onde um `Passageiro` é esperado, mantendo o comportamento definido pela abstração. Isso permite que a missão trabalhe com diferentes tipos de passageiros por meio da mesma referência.

### I — Interface Segregation Principle (ISP)
As interfaces `Posicionavel` e `Movel` possuem responsabilidades pequenas e específicas. Uma classe não precisa implementar métodos que não estejam relacionados à sua função. Isso deixa as interfaces mais simples e reduz dependências desnecessárias.

### D — Dependency Inversion Principle (DIP)
O `JogoService` recebe uma dependência do tipo `RankingRepository`, em vez de depender diretamente da implementação `RankingService`. Dessa forma, a lógica do jogo depende de uma abstração, permitindo trocar a forma de armazenamento do ranking com menos alterações no restante do sistema.

---

## 2. Melhoria adicional

Uma melhoria adicional seria separar ainda mais a lógica de entrada do usuário da classe `JogoService`.

Atualmente, o `JogoService` coordena a execução do jogo e utiliza o `Scanner` recebido pelo `Main`. Em uma próxima evolução, poderia ser criada uma classe ou interface específica para entrada do usuário. Assim, seria possível trocar o `Scanner` por outra forma de entrada, como uma interface gráfica, sem alterar a lógica principal da missão.

Outra melhoria futura seria criar testes automatizados para as regras principais, como embarque de passageiros, perda de vidas, colisões e cálculo da pontuação.

---

## 3. Decisão do tutorial com a qual a equipe concorda

A equipe concorda com a separação entre a lógica do jogo e a apresentação.

A criação do `MapaRenderer` como responsável por desenhar o mapa deixa o `JogoService` concentrado na coordenação das regras da missão. Essa separação facilita uma futura mudança da apresentação do console para uma interface gráfica, pois a lógica principal do jogo não precisa ser completamente reescrita.

---

## 4. Decisão do tutorial com a qual a equipe discorda

A equipe discorda da ideia de que a estrutura apresentada no tutorial seja necessariamente a solução definitiva para o projeto.

A divisão em `model`, `repository`, `presentation` e `service` melhora bastante a organização, mas ainda existe espaço para reduzir o acoplamento e separar melhor algumas responsabilidades. Por exemplo, em uma próxima iteração, a entrada do usuário poderia ficar em uma camada própria, evitando que a lógica do jogo fique responsável por coordenar tanto regras quanto interação com o usuário.

A discordância, portanto, não é sobre a separação proposta ser inadequada, mas sobre considerá-la como a estrutura final. Para a equipe, ela é uma boa base para novas melhorias.

---

## 5. Testes realizados e resultados

Foram realizados os seguintes testes após a refatoração:

| Teste | Resultado |
|---|---|
| Compilação dos arquivos Java do pacote `solidexercicio10` | OK |
| Execução da classe `solidexercicio10.Main` | OK |
| Abertura e navegação pelo menu | OK |
| Execução de uma missão | OK |
| Movimentação durante a missão | OK |
| Embarque de passageiros | OK |
| Funcionamento das regras da missão | OK |
| Ranking | OK |
| Reset do ranking | OK |
| Encerramento do programa | OK |

Com os testes realizados, o projeto foi executado normalmente após a refatoração.

---

## 6. Prioridade das melhorias

As melhorias foram organizadas da seguinte forma:

1. **Alta prioridade — Criar testes automatizados**
      - Permitir verificar automaticamente as principais regras do jogo.
      - Reduzir o risco de alterações futuras quebrarem funcionalidades existentes.

2. **Média prioridade — Separar a entrada do usuário**
      - Criar uma abstração para entrada de dados.
      - Facilitar uma futura troca do console por interface gráfica.

3. **Média prioridade — Reduzir ainda mais o acoplamento do renderer**
      - Fazer o `MapaRenderer` receber somente os dados necessários para desenhar.
      - Evitar que a apresentação tenha acesso desnecessário à lógica da missão.

4. **Baixa prioridade — Evoluir a persistência do ranking**
      - Permitir futuramente trocar o armazenamento em arquivo por banco de dados ou outro mecanismo sem alterar a lógica principal do jogo.

---

## Conclusão

A refatoração tornou o projeto mais organizado ao separar responsabilidades entre modelo, persistência, apresentação e serviço. Os princípios SOLID ajudaram a reduzir o acoplamento e facilitaram futuras alterações.

A equipe considera que a estrutura atual atende ao objetivo da atividade, mas reconhece que ainda existem melhorias possíveis, principalmente na criação de testes automatizados e na separação da entrada do usuário.

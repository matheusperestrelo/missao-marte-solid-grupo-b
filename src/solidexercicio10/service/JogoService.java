package solidexercicio10.service;
import java.util.*;
import solidexercicio10.model.*;
import solidexercicio10.presentation.MapaRenderer;
import solidexercicio10.repository.*;

public class JogoService {
    private final RankingRepository ranking; private final MapaRenderer renderer = new MapaRenderer(); private final Random random = new Random();
    public JogoService(RankingRepository ranking) { this.ranking = ranking; }
    public void executarLoop(Scanner s) {
        boolean ativo = true;
        while (ativo) {
            System.out.println("\n1. Nova missao\n2. Ranking\n3. Limpar ranking\n4. Sair");
            String op = ler(s, "Opcao: ", "4");
            switch (op) {
                case "1" -> jogar(s); case "2" -> exibirRanking();
                case "3" -> { ranking.limpar(); System.out.println("Ranking limpo."); }
                case "4" -> ativo = false; default -> System.out.println("Opcao invalida.");
            }
        }
    }
    private void jogar(Scanner s) {
        String nome = ler(s, "Piloto: ", "Piloto");
        Dificuldade dificuldade = Dificuldade.deString(ler(s, "Dificuldade: ", "medio"));
        int tamanho;
        try { tamanho = Integer.parseInt(ler(s, "Tamanho: ", "5")); } catch (NumberFormatException e) { tamanho = 5; }
        tamanho = Math.max(1, tamanho);
        int min = -tamanho, max = tamanho, total = dificuldade == Dificuldade.FACIL ? 4 : 5;
        if (dificuldade == Dificuldade.DIFICIL) total = 6;
        Nave nave = new Nave("A-1", 0, 0, total); Missao missao = new Missao(nave);
        colocar(missao, total, min, max, true, nave); colocar(missao, dificuldade == Dificuldade.DIFICIL ? 3 : 2, min, max, false, nave);
        colocarPerigos(missao, dificuldade == Dificuldade.DIFICIL ? 3 : 2, min, max, nave);
        int pontos = dificuldade == Dificuldade.FACIL ? 30 : dificuldade == Dificuldade.DIFICIL ? 15 : 20;
        int movimentos = 0; long inicio = System.currentTimeMillis(); ler(s, "Enter para iniciar", "");
        while (true) {
            renderer.desenhar(missao, pontos, nome, min, max, min, max);
            System.out.printf("A bordo: %d/%d | Restantes: %d | Total: %d%n", nave.getPassageiros().size(), nave.getCapacidade(), missao.getPassageiros().size(), nave.getPassageiros().size() + missao.getPassageiros().size());
            char c = ler(s, "Comando: ", "q").toLowerCase().charAt(0);
            if (c == 'q') return;
            if (c == 'c') { Passageiro p = missao.passagemNaPosicao(); if (p != null && missao.embarcarPassageiroNaPosicao()) pontos += p.getPontuacao(); }
            else if ("wsad".indexOf(c) >= 0) { nave.moverComLimites(c, min, max, min, max); pontos--; movimentos++; }
            missao.moverInimigos(random, min, max, min, max);
            if (missao.verificaColisao()) nave.perderVida();
            if (pontos <= 0 || nave.getVidas() == 0) return;
            if (missao.todosEmbarcados() && nave.getX() == 0 && nave.getY() == 0) {
                long tempo = (System.currentTimeMillis() - inicio) / 1000;
                System.out.printf("Missao concluida. Pontos: %d, movimentos: %d, tempo: %ds%n", pontos, movimentos, tempo);
                ranking.salvar(nome, pontos, dificuldade, nave.getPassageiros().size(), tempo); return;
            }
        }
    }
    private void colocar(Missao m, int quantidade, int min, int max, boolean passageiro, Nave n) {
        for (int i = 0; i < quantidade; i++) { int[] p = livre(m, min, max, n); if (passageiro) { if (i % 3 == 0) m.adicionarPassageiro(new Professor("Professor", p[0], p[1])); else if (i % 3 == 1) m.adicionarPassageiro(new Engenheiro("Engenheiro", p[0], p[1])); else m.adicionarPassageiro(new Astronauta("Astronauta", p[0], p[1])); } }
    }
    private void colocarPerigos(Missao m, int quantidade, int min, int max, Nave n) { for (int i = 0; i < quantidade; i++) { int[] p = livre(m, min, max, n); m.adicionarInimigo(new Inimigo(p[0], p[1])); } }
    private int[] livre(Missao m, int min, int max, Nave n) { int x, y; do { x = random.nextInt(max - min + 1) + min; y = random.nextInt(max - min + 1) + min; } while ((x == 0 && y == 0) || ocupado(m, x, y)); return new int[] { x, y }; }
    private boolean ocupado(Missao m, int x, int y) { for (Passageiro p : m.getPassageiros()) if (p.getX() == x && p.getY() == y) return true; for (Inimigo i : m.getInimigos()) if (i.getX() == x && i.getY() == y) return true; return false; }
    private void exibirRanking() { for (RankingEntry e : ranking.listar()) System.out.printf("%s: %d%n", e.name, e.score); }
    private String ler(Scanner s, String mensagem, String padrao) { System.out.print(mensagem); if (!s.hasNextLine()) return padrao; String valor = s.nextLine(); return valor.isBlank() ? padrao : valor; }
}
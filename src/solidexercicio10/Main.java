package solidexercicio10;
import java.util.Scanner;
import solidexercicio10.repository.RankingRepository;
import solidexercicio10.repository.RankingService;
import solidexercicio10.service.JogoService;

public class Main {
    public static void main(String[] args) {
        RankingRepository repository = new RankingService("ranking-solid-exercicio10.json");
        JogoService jogo = new JogoService(repository);
        try (Scanner scanner = new Scanner(System.in)) { jogo.executarLoop(scanner); }
    }
}
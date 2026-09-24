package solidexercicio10.repository;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import solidexercicio10.model.Dificuldade;

public class RankingService implements RankingRepository {
    private final Path arquivo;
    public RankingService(String nome) { arquivo = Paths.get(nome); }
    public void salvar(String nome, int pontos) { salvar(nome, pontos, Dificuldade.MEDIO, 0, 0); }
    public void salvar(String nome, int pontos, Dificuldade dificuldade, int passageiros, long tempo) {
        List<String> linhas = new ArrayList<>();
        if (Files.exists(arquivo)) try { linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8); } catch (IOException ignored) { }
        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        linhas.add(nome + "|" + pontos + "|" + dificuldade + "|" + passageiros + "|" + data + "|" + tempo);
        try {
            Path parent = arquivo.getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.write(arquivo, linhas, StandardCharsets.UTF_8);
        } catch (IOException e) { throw new IllegalStateException("Falha ao salvar ranking", e); }
    }
    public List<RankingEntry> listar() {
        List<RankingEntry> ranking = new ArrayList<>();
        if (!Files.exists(arquivo)) return ranking;
        try {
            for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
                String[] p = linha.split("\\|", -1);
                if (p.length < 6) continue;
                try { ranking.add(new RankingEntry(p[0], Integer.parseInt(p[1]), Dificuldade.deString(p[2]), Integer.parseInt(p[3]), p[4], Long.parseLong(p[5]))); }
                catch (NumberFormatException ignored) { }
            }
        } catch (IOException ignored) { return ranking; }
        ranking.sort(Comparator.comparingInt((RankingEntry e) -> e.score).reversed());
        return ranking;
    }
    public void limpar() { try { Files.deleteIfExists(arquivo); } catch (IOException e) { throw new IllegalStateException(e); } }
}
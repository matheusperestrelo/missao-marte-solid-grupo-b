package solidexercicio10.model;

public enum Dificuldade {
    FACIL, MEDIO, DIFICIL;

    public static Dificuldade deString(String valor) {
        if (valor == null) return MEDIO;
        return switch (valor.trim().toLowerCase()) {
            case "facil", "fácil" -> FACIL;
            case "dificil", "difícil" -> DIFICIL;
            default -> MEDIO;
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case FACIL -> "Fácil";
            case DIFICIL -> "Difícil";
            case MEDIO -> "Médio";
        };
    }
}

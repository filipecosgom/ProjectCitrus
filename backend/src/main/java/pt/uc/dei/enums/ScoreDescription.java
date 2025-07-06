package pt.uc.dei.enums;

public enum ScoreDescription {
    NO_SCORE(0),
    LOW(1),
    PARTIAL(2),
    EXPECTED(3),
    EXCEEDED(4);

    private final int value;

    ScoreDescription(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getDescription(Language lang) {
        switch (this) {
            case NO_SCORE:
                return lang == Language.PORTUGUESE ? "Nenhuma pontuação atribuída" : "No score given";
            case LOW:
                return lang == Language.PORTUGUESE ? "Contribuição baixa" : "Low contribution";
            case PARTIAL:
                return lang == Language.PORTUGUESE ? "Contribuição parcial" : "Partial contribution";
            case EXPECTED:
                return lang == Language.PORTUGUESE ? "Contribuição conforme o esperado" : "Expected contribution";
            case EXCEEDED:
                return lang == Language.PORTUGUESE ? "Contribuição excedida" : "Exceeded contribution";
            default:
                return "";
        }
    }

    public static ScoreDescription fromScore(Integer score) {
        if (score == null) return NO_SCORE;
        switch (score) {
            case 1: return LOW;
            case 2: return PARTIAL;
            case 3: return EXPECTED;
            case 4: return EXCEEDED;
            default: return NO_SCORE;
        }
    }
}

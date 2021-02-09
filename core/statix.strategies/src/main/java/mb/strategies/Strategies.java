package mb.strategies;

public final class Strategies {
    private Strategies() {}

    public static <CTX, I, M, O> Strategy<CTX, I, O> glc(
        Strategy<CTX, I, M> condition,
        Strategy<CTX, M, O> onSuccess,
        Strategy<CTX, I, O> onFailure
    ) {
        return new IfStrategy<CTX, I, M, O>().apply(condition, onSuccess, onFailure);
    }
}

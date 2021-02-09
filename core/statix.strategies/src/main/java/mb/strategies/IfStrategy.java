package mb.strategies;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class IfStrategy<CTX, I, M, O> implements Strategy3<CTX, Strategy<CTX, I, M>, Strategy<CTX, M, O>, Strategy<CTX, I, O>, I, O>{

//    @Override
//    public O eval(
//        CTX ctx,
//        Strategy<CTX, I, M> condition,
//        Strategy<CTX, M, O> onSuccess,
//        Strategy<CTX, I, O> onFailure,
//        I input
//    ) throws InterruptedException {
//        return this.apply(condition, onSuccess, onFailure).eval(ctx, input);
//    }

    @Override
    public O eval(
        CTX ctx,
        Strategy<CTX, I, M> condition,
        Strategy<CTX, M, O> onSuccess,
        Strategy<CTX, I, O> onFailure,
        I input
    ) throws InterruptedException {
        @Nullable final M value = condition.eval(ctx, input);
        if (value != null) {
            return onSuccess.eval(ctx, value);
        } else {
            return onFailure.eval(ctx, input);
        }
    }

//    @Override
//    public Strategy<CTX, I, O> apply(
//        Strategy<CTX, I, M> condition,
//        Strategy<CTX, M, O> onSuccess,
//        Strategy<CTX, I, O> onFailure
//    ) {
//        return new S(condition, onSuccess, onFailure);
//    }
//
//    private static final class S<CTX, I, M, O> implements Strategy<CTX, I, O> {
//
//        private final Strategy<CTX, I, M> condition;
//        private final Strategy<CTX, M, O> onSuccess;
//        private final Strategy<CTX, I, O> onFailure;
//
//        public S(
//            Strategy<CTX, I, M> condition,
//            Strategy<CTX, M, O> onSuccess,
//            Strategy<CTX, I, O> onFailure
//        ) {
//            this.condition = condition;
//            this.onSuccess = onSuccess;
//            this.onFailure = onFailure;
//        }
//
//        @Override
//        public O eval(CTX ctx, I input) throws InterruptedException {
//            @Nullable final M value = this.condition.eval(ctx, input);
//            if (value != null) {
//                return this.onSuccess.eval(ctx, value);
//            } else {
//                return this.onFailure.eval(ctx, input);
//            }
//        }
//    }

}

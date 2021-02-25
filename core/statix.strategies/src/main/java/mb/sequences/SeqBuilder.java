package mb.sequences;


@SuppressWarnings("all")
public interface SeqBuilder {

    static <T> void yieldAll(Iterable<T> iterable, Continuation<T> cont) {

    }

    static void doSomethingSuspend(String _item, Continuation cont) {
        class StateMachine implements Continuation<Object> {
            // State
            int state = 0;
            // Input arguments
            String item;
            // Result
            Object result;

            @Override
            public void resume(Object value) {
                this.result = value;
                doSomethingSuspend(null, this);
            }
        }
        final StateMachine sm = cont instanceof StateMachine ? (StateMachine)cont : new StateMachine();
        switch (sm.state) {
            case 0:
                sm.item = _item;
                sm.state = 1;
                requestTokenSuspend(sm);
                break;
            case 1: {
                final String item = sm.item;
                final String token = (String)sm.result;
                sm.state = 2;
                createPostSuspend(token, item, sm);
                break;
            }
            case 2: {
                final String item = sm.item;
                final int post = (Integer)sm.result;
                processPost(post);
                break;
            }
            default:
                throw new IllegalStateException();
        }
    }


    static void doSomething(String item) {
        // 0:
        final String token = requestToken();
        // 1:
        final int post = createPost(token, item);
        // 2:
        processPost(post);
    }

    static void requestTokenSuspend(Continuation<? super String> cont) { cont.resume("T");}
    static void createPostSuspend(String token, String item, Continuation<? super Integer> cont) { cont.resume(token.length() + item.length()); }

    static String requestToken() { return "T";}
    static int createPost(String token, String item) { return token.length() + item.length(); }
    static void processPost(int post) { return; }

//    static class CoroutineImpl {
//        int label;
//    }
}

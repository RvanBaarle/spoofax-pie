package mb.strategies;

import mb.sequences.Seq;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DebugEventHandler implements StrategyEventHandler, Closeable {


    private final Yaml yaml;
    private final Writer writer;
    //private final Map<String, Object> root = new LinkedHashMap<>();
    private final Deque<StrategyData> stack = new ArrayDeque<>();

    public DebugEventHandler(Path path, DebugRepresenter debugRepresenter) throws IOException {
        this(Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE), debugRepresenter);
    }

    public DebugEventHandler(OutputStream outputStream, DebugRepresenter debugRepresenter) {
        this(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), debugRepresenter);
    }

    public DebugEventHandler(Writer writer, DebugRepresenter debugRepresenter) {
        if (writer == null) throw new NullPointerException();
        this.writer = writer;
        this.yaml = createYaml(debugRepresenter);
        // Push the root map
        this.stack.push(new StrategyData(null, new ArrayList<>()));
    }

    private static Yaml createYaml(DebugRepresenter debugRepresenter) {
        return new Yaml(debugRepresenter);
    }

    public static class DebugRepresenter extends Representer {
        public DebugRepresenter() {
            super();
            setRepr(null, this.representers.get(String.class)); // Set default
//            addMultiRepr(Seq.class, new RepresentSeq());
        }

        public void setRepr(@Nullable Class<?> clz, Represent represent) {
            if (represent == null) throw new NullPointerException("represent");
            this.representers.put(clz, represent);
        }

        public void setMultiRepr(@Nullable Class<?> clz, Represent represent) {
            if (represent == null) throw new NullPointerException("represent");
            this.multiRepresenters.put(clz, represent);
        }

        protected class RepresentSeq implements Represent {
            @SuppressWarnings("unchecked")
            public Node representData(Object data) {
                return representSequence(getTag(data.getClass(), Tag.SEQ), ((Seq<Object>) data).asIterable(), DumperOptions.FlowStyle.AUTO);
            }
        }

//        protected class RepresentSeq implements Represent {
//            @SuppressWarnings("unchecked")
//            public Node representData(Object data) {
//                return representSequence(getTag(data.getClass(), Tag.SEQ), ((Seq<Object>) data).asIterable(), DumperOptions.FlowStyle.AUTO);
//            }
//        }
    }

    private void innerEnter(StrategyDecl strategy, Object ctx, List<Object> args, Object input) {
        if (!isDebugged(ctx, strategy)) return;
        final List<Object> strategyList = new ArrayList<>();
//        final Map<String, Object> argsMap = new LinkedHashMap<>();
        for(int i = 0; i < strategy.getArity(); i++) {
            final String paramName = strategy.getParamName(i);
            final Object arg = args.get(i);
            strategyList.add(newMap("@" + paramName, represent(ctx, arg)));
//            argsMap.put(paramName, represent(ctx, arg));
        }
//        final List<Object> strategyList = new ArrayList<>();
//        if(!argsMap.isEmpty()) strategyList.add("@args", argsMap);
//        strategyList.put("@input", represent(ctx, input));
        strategyList.add(newMap("@input", represent(ctx, input)));
        final StrategyData sData = stack.peek();
        final List<Object> parentList = sData.list;
        parentList.add(newMap(strategy.getName(), strategyList));
        stack.push(new StrategyData(strategy, strategyList));
    }

    private void innerLeave(StrategyDecl strategy, Object ctx, Seq<Object> output) {
        if (!isDebugged(ctx, strategy)) return;
        final StrategyData sData = stack.pop();
        if (strategy != sData.strategy) {
            throw new UnsupportedOperationException("Trying to leave strategy '" + strategy + "', expected leaving '" + sData.strategy + "'.");
        }
        final List<Object> strategyList = sData.list;
        Object result;
        try {
            List<Object> results = output.map(o -> represent(ctx, o)).toList().eval();
            if(results.isEmpty()) {
                result = "FAILED";
            } else {
                result = results;
            }
        } catch(InterruptedException ex) {
            // Cannot happen
            result = "ERROR";
        }
        strategyList.add(newMap("@output", result));
    }

    private Map<String, Object> newMap(String key, Object value) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put(key, value);
        return map;
    }

    protected boolean isDebugged(Object ctx, StrategyDecl obj) {
        return !(obj instanceof AppliedStrategies.ApplStrategy1To0)
            && !(obj instanceof AppliedStrategies.ApplStrategy2To0)
            && !(obj instanceof AppliedStrategies.ApplStrategy3To0)
            && !(obj instanceof SeqStrategy)
            && !(obj instanceof DebugStrategy)
            && !obj.getName().equals("seq")
            && !obj.getName().equals("debugState")
            && !obj.getName().equals("debugCUser")
            && !obj.getName().equals("debugCResolveQuery")
            && !obj.getName().equals("printSolverState");
    }

    protected Object represent(Object ctx, Object obj) {
        return obj;
    }

    @Override
    public <CTX, I, O> I enter(Strategy<CTX, I, O> strategy, CTX ctx, I input) {
        innerEnter(strategy, ctx, Collections.emptyList(), input);
        return input;
    }

    @Override
    public <CTX, A1, I, O> I enter(Strategy1<CTX, A1, I, O> strategy, CTX ctx, A1 arg1, I input) {
        innerEnter(strategy, ctx, Collections.singletonList(arg1), input);
        return input;
    }

    @Override
    public <CTX, A1, A2, I, O> I enter(Strategy2<CTX, A1, A2, I, O> strategy, CTX ctx, A1 arg1, A2 arg2, I input) {
        innerEnter(strategy, ctx, Arrays.asList(arg1, arg2), input);
        return input;
    }

    @Override
    public <CTX, A1, A2, A3, I, O> I enter(Strategy3<CTX, A1, A2, A3, I, O> strategy, CTX ctx, A1 arg1, A2 arg2, A3 arg3, I input) {
        innerEnter(strategy, ctx, Arrays.asList(arg1, arg2, arg3), input);
        return input;
    }

    @Override
    public <CTX, I, O> Seq<O> leave(Strategy<CTX, I, O> strategy, CTX ctx, Seq<O> output) {
        final Seq<O> buffered = output.buffer();
        //noinspection unchecked
        innerLeave(strategy, ctx, (Seq<Object>)buffered);
        return buffered;
    }

    @Override
    public <CTX, A1, I, O> Seq<O> leave(Strategy1<CTX, A1, I, O> strategy, CTX ctx, Seq<O> output) {
        final Seq<O> buffered = output.buffer();
        //noinspection unchecked
        innerLeave(strategy, ctx, (Seq<Object>)buffered);
        return buffered;
    }

    @Override
    public <CTX, A1, A2, I, O> Seq<O> leave(Strategy2<CTX, A1, A2, I, O> strategy, CTX ctx, Seq<O> output) {
        final Seq<O> buffered = output.buffer();
        //noinspection unchecked
        innerLeave(strategy, ctx, (Seq<Object>)buffered);
        return buffered;
    }

    @Override
    public <CTX, A1, A2, A3, I, O> Seq<O> leave(Strategy3<CTX, A1, A2, A3, I, O> strategy, CTX ctx, Seq<O> output) {
        final Seq<O> buffered = output.buffer();
        //noinspection unchecked
        innerLeave(strategy, ctx, (Seq<Object>)buffered);
        return buffered;
    }

    @Override
    public void close() throws IOException {
        List<Object> list = stack.pop().list;
        yaml.dumpAll(list.iterator(), writer);
    }

    private static class StrategyData {
        public final StrategyDecl strategy;
        public final List<Object> list;
        public StrategyData(StrategyDecl strategy, List<Object> list) {
            this.strategy = strategy;
            this.list = list;
        }
    }
}

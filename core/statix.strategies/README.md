



## Lazy/Maybe/Option
A `Lazy<T>` is a lazy computation of a result `T`. It has the following core functions and methods:

- `of(value: T): Lazy<T>` - Unit operation that creates a new computation with the given value.
- `Lazy<T>.flatMap(transform: T -> Lazy<R>): Lazy<R>` - Binding operation that maps the specified operation over this operation and flattens the result.
- `Lazy<T>.forEach(action: T -> ()): Boolean` - Terminal operation that evaluates the computation and maybe invokes the action.

From these we can define others:
- `Lazy<T>.get(): T = { val x: T; if(!this.forEach({ it -> x = it })) throw Exception() else return x }`
- `Lazy<T>.tryGet(): T? = { val x: T? = null; this.forEach({ it -> x = it }); return x; }`
- `compose(f: A -> Lazy<B>, g: B -> Lazy<C>): A -> Lazy<C> = { a -> f(a).flatMap(g) }`
- `Lazy<A>.map(transform: A -> B): Lazy<B> = this.flatMap { a -> of(transform(a)) }`

## Strategy
A strategy combines (lazy) computations.

- `id(f: A -> Option<B>): A -> Option<B> = f`
- `fail(f: A -> Option<B>): A -> Option<B> = { _ -> Option.fail() }`
- `compose(f: A -> Option<B>, g: B -> Option<C>): A -> Option<C> = { a -> f(a).flatMap(g) }`

```
if(c: A -> Option<B>, t: B -> Option<C>, e: A -> Option<C>): A -> Option<C> = { a ->
  val mb = c(a)
  if (mb.any())
    return mb.flatMap(t)
  else
    return of(a).flatMap(e)
}
```

vs.

```
if(): (A -> Option<B>, B -> Option<C>, A -> Option<C>) A -> Option<C> = { (c, t, e) a ->
  val mb = c(a)
  if (mb.any())
    return mb.flatMap(t)
  else
    return of(a).flatMap(e)
}
```

Because all strategies are singleton objects, we need some way to bind arguments to them. For example:

```
// Build the computation
val s = if.apply(c, t, e)
// Evaluate the computation
val r = s.apply(a).eval()
```

Let's see `flatMap` on strategies, as a strategy.

```
val seq: (A -> Option<B>, B -> Option<C>, A) -> Option<C> =
  { s1, s2, a -> s1(a).flatMap(s2) }
```


```
solve(): SolverState -> Option<SolverState> = { s ->
  val s' = solver.solve(s)
  return Option.of(s')
}
```

----

## Sequence
A sequence computes its values lazily, for every value which is required from it. If a sequence is iterated twice, its values are computed twice. A sequence indicates failure when iteration produces no values.

    Sequence<T>.iter(ctx: CTX): Iterator<T>

The most basic operation to build a sequence is `emit`:

    Sequence.emit(generator: () -> T?): Sequence<T>

The most basic operation to terminate a sequence is `collect`:

    Sequence.collect(collector: T -> ())

Sequences have initial operations, which build the sequence:

    Sequence.empty(): Sequence<T>
    Sequence.of(vararg values: R): Sequence<T>
    Sequence.from(iterable: Iterable<T>): Sequence<T>

Sequences have intermediate operations, which do not evaluate the sequence:

    Sequence<T>.drop(n: Int): Sequence<T>
    Sequence<T>.dropWhile(p: T -> Boolean): Sequence<T>
    Sequence<T>.take(n: Int): Sequence<T>
    Sequence<T>.takeWhile(p: T -> Boolean): Sequence<T>
    Sequence<T>.distinct(): Sequence<T>
    Sequence<T>.distinctBy(selector: T -> K): Sequence<T>

Sequences don't have many terminal operations. Operations which would otherwise be terminal
return a computation instead.

    Sequence<T>.count(): Computation<Int>
    Sequence<T>.count(p: T -> Boolean): Computation<Int>
    Sequence<T>.all(p: T -> Boolean): Computation<Boolean>
    Sequence<T>.any(): Computation<Boolean>
    Sequence<T>.any(p: T -> Boolean): Computation<Boolean>
    Sequence<T>.none(): Computation<Boolean>
    Sequence<T>.none(p: T -> Boolean): Computation<Boolean>
    Sequence<T>.first(p: T -> Boolean): Computation<T>
    Sequence<T>.last(p: T -> Boolean): Computation<T>
    Sequence<T>.associateBy(keySelector: T -> K, valueTransform: T -> V): Computation<Map<K, V>>
    Sequence<T>.contains(element: T): Computation<Boolean>
    Sequence<T>.elementAt(index: Int): Computation<T>


The following are special intermediate operations:

    // Ensure the sequence can only be iterated once.
    Sequence<T>.constrainOnce(): Sequence<T>
    // Buffers the results of the sequence.
    Sequence<T>.buffer(): Sequence<T>


## Computation
A computation computes its value every time it is evaluated. It is a sequence with only zero or one result. A computation indicates failure when evaluation produces `null`.

    Computation<R>.eval(ctx: CTX): R?

    Computation<R>: Sequence<R>



## Buffering
Since both sequences and computations recompute their values upon iteration/evaluation respectively, it can be a performance benefit to buffer the computed results at the cost of some memory. For computations, this is called a _thunk_. However, there are no special classes for cached sequences and computations, since the buffering effect is transparent to the user.

    Sequence<R>.buffer(): Sequence<R>
    Computation<R>.buffer(): Computation<R>

> *Note*: As an optimization, buffering an already buffered sequence or computation does not buffer it twice.


## Strategies
A strategy is used to compose (lazy) computations. A strategy takes zero or more input arguments and produces a computation or sequence.

    id(value: T): T = { value }
    fail(value: T): T = {}

### Conditional
There is a special conditional strategy called `if(c, t, e)`, which takes a condition `c`, a then-value `t` and an else value `e`. The condition is evaluated or iterated if necessary. If it indicates failure (`null` or an empty sequence), the strategy returns `e`. Otherwise, it returns `t`.

Writing the `c < t + e` Stratego strategy in this way looks like this:

    glc: (T -> C, C -> R, T -> R) T -> R
    glc.apply(sc: T -> C, st: C -> R, se: T -> R, input: T): Seq<R> -> {
        val c: Seq<C> = sc.apply(input);
        val t: Seq<R> = st.apply(c);
        val e: Seq<R> = se.apply(input);
        return TODO();
    }

    T.isFail(): Boolean {
        if (this is Seq) return this.any()
        else return this != null
    }


--------

# Strategies
A strategy represents a lazy computation. A strategy takes zero or more input arguments and produces a lazy computation. Evaluating the computation will produce the actual results of the strategy.

The original intended use for these strategies is to perform search space exploration in Statix solutions, which is why strategies return a lazy sequence of results instead of a lazy computation.

The signature of a typical strategy with one input and a lazy sequence of results, is:

    strategy(input: T): Sequence<R>

A lazy computation can be evaluated in a context:

    Computation<R>.eval(ctx: CTX): R

A sequence is a special kind of lazy computation that yields its values one-by-one:

    Sequence<R>.eval(ctx: CTX)

The biggest issue with lazy computations and strategy compositions is conditional evaluation. In the case of an `if(c, t, e)` strategy, to determine whether to return the `t` or `e` computation, we have to evaluate the `c` computation (at least partially).

    if(c, t, e) = <?{_}> c


## Thunks
A thunk is either a computation or its result. Initially, the thunk represents only the computation, but after the first evaluation the thunk represents its result.

    // Creates a thunk from a computation
    Computation<T>.thunk(): Thunk<T>

    // Returns the value, computing it if necessary
    Thunk<T>.unthunk(): T

## Sequence
A (possibly infinite) sequence of lazy computations.

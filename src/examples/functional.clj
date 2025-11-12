(ns examples.functional)

;; Functional programming concepts

;; Pure functions

;; A mystery function.
;;
;; If it is a _pure function_, then `data-1` and `data-2` **must be**
;; immutable. (If not, calling `mystery` with the same input at
;; different times would return different results.)
(def data-1 :foo)
(def data-2 :bar)
(defn mystery [input]
  (if input data-1 data-2))

;; Persistent data structures

;; Clojure data structures are **persistent**. This idea is **different**
;; from persistence using a database - but it is similar. A persistent data
;; structure is a data structure that
;;
;; - One can change
;; - Without, generally, copying the entire structure
;; - Such that, after the change both the original **and** the copy
;;   both exist in memory
;;
;; Or,
;;
;; > In this context, persistent means that the data structures preserve
;; > old copies of themselves by efficiently _sharing structure_ between
;; > the older and newer versions.

;; For example,
(def a '(1 2))
(def b (cons 0 a))
b
a

;; Laziness and recursion

;; A recursion occurs when a function calls itself - directly or
;; indirectly. Laziness results in Clojure delaying evaluating an
;; expression **until it is actually needed**.

;; In Clojure, neither functions nor expressions are lazy; however,
;; sequences **are** lazy. This choice allows one to gain much of the
;; benefit of a fully lazy language.

;; Remember, lazy evaluation "plays nicely" with pure functions.

;; Referential transparency

;; Functions which can be replace by their result are called
;; _referentially transparent_. These kinds of functions benefit from
;; the following:
;;
;; - _Memoization_ - automatic caching of results
;; - Automatic _parallelization_ - moving function evaluation to another
;;   processor or machine

;; Benefits of FP

;; We "promised" that functional programming will make your code
;; easier to:
;;
;; - Write - Because all the information we need to understand the
;;   function is **inside** the function itself
;; - Read - Same as writing
;; - Test - Greatly simplifies the setup of tests; that is, one need
;;   only supply the correct arguments
;; - Reuse - To reuse code, you must understand it and be able to
;;   easily compose it with **other** code.
;; - Compose - Pure functions **are** encapsulated

;; Guidelines for use

;; The following guidelines help your initial steps toward FP mastery
;; in Clojure:
;;
;; 1. Avoid direct recursion (not optimizable by the JVM)
;; 2. Use `recur` when you're producing
;;    - Scalar values
;;    - Small, fixed sequences
;;
;;    Remember that Clojure **will** optimize calls that use an
;;    explicit `recur`.
;; 3. When producing large or variable sized sequences, **always**
;;    be lazy. (**Do not recur**.) This approach allows callers to
;;    consume only the part of the sequences what they actually need.
;; 4. Be careful **not to realize** more of a lazy sequence that
;;    you need.
;; 5. Know the sequence library. This knowledge often allows you to
;;    write code **without** using `recur` or the lazy APIs at all.
;; 6. Subdivide. Divide even simple-seeming problems into smaller
;;    pieces often lead to using solutions from the sequence library.

;; How to be lazy

;; Functional programs make great use of _recursive definitions_. A
;; recursize definitions has two parts:
;;
;; - A _basis_ (explicity enumerates some members of the sequence)
;; - An _induction_ (rules for combining members of the sequence to
;;   produce additional members).

;; Working code may involve:
;;
;; - A simple recursion
;; - A tail recursion - a function which only calls itself as the
;;   (absolute) last step
;; - A lazy sequence that eliminates actual recursion (and calculates
;;   a value later when it is needed)

;; Remember, writing a recursive function in Java is not easy (see
;; "no tail call optimization"); consequently, often the best approach
;; in Clojure is **to be lazy**.

;; We'll explore all these approaches by implementing a function to
;; calculate the Fibonacci numbers.

;; Here is an implementation using simple recursion.

;; A bad idea
(defn stack-consuming-fibo [n]
  (cond
   ;; The next two lines are the _basis__
   (= n 0) 0
   (= n 1) 1
   ;; The `:else` part is the _induction_
   :else (+ (stack-consuming-fibo (- n 1))
            (stack-consuming-fibo (- n 2)))))

;; This implementation works for "small" values of `n`.
(stack-consuming-fibo 34)

;; However, this implementation **fails** for "large" values of `n`.
;; (stack-consuming-fibo 1000000)

;; The function, `stack-consuming-fibo`, creates a number of stack frames
;; proportional to `n`. For "large" values of `n`, this implementation
;; exhausts the JVM stack.
;;
;; Additionally, when the implementation **does not** overflow the
;; stack, the total number of stack frame created is **exponential**
;; in `n`. This exponential creation of stack frames results in
;; terrible performance even if it does not overflow.

;; Generally, in Clojure, you should almost always avoid
;; _stack-consuming_ recursion like `stack-consuming-fibo`.

;; Tail recursion

;; Functional programs can solve the stack-usage problem with
;; _tail recursion_. In a tail-recursive function, the recursion must
;; be the last operation performed by the function; that is, the
;; expression returned by the function. A language can then perform
;; _tail-call opimization_ (TCO) and convert the recursion into an
;; iteration that **odes not** consume the stack.

;; To convert `stack-consuming-fibo` to a tail recursive function, one
;; must create a function whose arguments hawe enough information to
;; move the recursion forward without accruing an "after work" (like
;; an addition between two Fibonacci numbers).
;;
;; These ideas suggest that a function whose arguments
;;
;; - Incude the two Fibonacci numbers
;; - An ordinal value, `n`, to count down

(defn tail-fibo [n]
  (letfn [(fib [current next n]
            (if (zero? n)
              current
              (fib next (+ current next) (dec n))))]
    (fib 0N 1N n)))

;; This definition uses `letfn`. `leftn` is like `let` but is
;; dedicated to creating local functions. Uniquely, any function
;; defined in the `letfn` can call either itself or any other
;; function defined in the `letfn` block.

;; `tail-fibo` works for small values of `n`.
(tail-fibo 9)

;; However, even though it is tail recursive, it fails for large
;; values of `n` because Java **does not** perform tail call
;; optimization. Other functional languages, like Haskell or Scheme
;; could call a language-specific function like `fib` without "blowing"
;; the stack.

;; Clojure provides several pragmatic workarounds:
;;
;; - Explicit self-recursion with `recur`
;; - Lazy sequences
;; - Explicit multual recursion with `trampoline
;;
;; We'll discuss the first two techniques here. We'll defer the
;; discussion of `trampoline` for later in this chapter.

;; Self-recursion with `recur`

;; The function, `tail-fibo`, is an example of self-recursion that can
;; be optimized away on the JVM.
;;
;; In Clojure, we can convert a function that perferms a tail-recursive
;; call into self-recursion with `recur`.

;; Better, but still not great
(defn recur-fibo [n]
  (letfn [(fib [current next n]
            (if (zero? n)
              current
              (recur next (+ current next) (dec n))))]
    (fib 0N 1N n)))

;; Unlike other previous implementations of Fibonacci numbers,
;; `recur-fib` will **not** consume stack as it calculates.

(recur-fibo 9)

;; Remember, calculating the millionth Fibonacci number does take some
;; time.
(recur-fibo 1000000)

;; Although `recur-fibo` can calculate a **single** Fibonacci number,
;; it does not effectively support multiple calls because each call
;; starts completely frush. Since the function itself has insufficient
;; information to effectively cache values.

;; Ideally, you'd define sequonces with an API that makes no reference
;; to the specific range the client cares about. This implementation
;; would then allow clients to find the range they want using `take`
;; and `drop`.

;; Lazy sequences

;; A `lazy-seq` invokes its body only when needed; that is, when `seq`
;; is called either directly or indirectly. Additionally, `lazy-seq`
;; then caches the result for subsequent calls.

;; Here is a lazy Fibanacci sequence defined using `lazy-seq`

(defn lazy-seq-fibo
  ;; Callers typically call the zero-argument version
  ([]
   (concat [0 1] (lazy-seq-fibo 0N 1N)))
  ;; Essentially, the two-argument implementation is "private".
  ([a b]
   (let [n (+ a b)]
     (lazy-seq
      (cons n (lazy-seq-fibo b n))))))

;; `lazy-seq-fibo` works for small values
(take 10 (lazy-seq-fibo))

;; However, `lazy-seq-fibo` also works for **large values**.
;;
;; Notice the call to `rem` which effectively extracts the
;; last three digits of this very lange value.
(rem (nth (lazy-seq-fibo) 1000000) 1000)

;; The `lazy-seq-fibo` approach follows guideline #3 by using laziness
;; to implement an infinite sequence. This approach works (much better
;; than our previous approaches); however, we can accomplish this task
;; with less hand-written code using guideline #5 and using existing
;; sequence library functions.

(take 5 (iterate (fn [[a b]] [b (+ a b)])
                 [0 1]))

;; This function starts with the first pair of Fibonacci numbers,
;; `[0 1]`. From that pair, it calculates the next pair during the
;; next iteration (invoking the Fibonacci generator function) and
;; appends that next pair **lazily** to the sequence. And so on.

;; This approach suggests our "final" implementation of the
;; Fibonacci sequence.

(defn fibo []
  (map first (iterate (fn [[a b]] [b (+ a b)])
                      [0N 1N])))
(take 5 (fibo))
(take 10 (fibo))
(take 34 (fibo))
(rem (nth (fibo) 1000000) 1000)

;; Notice that `fibo` returns a lazy sequence because it builds on
;; `map` and `iterate` both of which return **lazy sequences**.
;; Additionally, `fibo` is **simple** and short. It is understandable
;; because the code reflects the "definition" of the Fibonacci
;; sequence **but does not** obfuscate that definition with code.

;; However, learning to think recursively, lazily, and within the
;; JVM's limitations on recursion - all at the same time, requires
;; work, creativity and experience.
;;
;; Remember, guideline #3 correctly predicts that the right approach
;; is to use a lazy sequence, and guideline #5 lets **existing**
;; sequences do most of the work.

;; Be cautious, though. Lazy sequences consume **some** stack and heap.
;; But they **do not** consume resources proportional to the size of
;; the entire - possibly infinite - sequence that they generate. By
;; generating these sequences lazily, you, the programmer, choose
;; how what resources, for example, time, you will consume to traverse
;; the sequence. For example, if you need the one millionth Fibonacci
;; number, you can use `fibo` to generate it - **without** consuming
;; stack or heap space for **all** those previous values. Remember,
;; there is no such thing as a free lunch.

;; In general, when writing Clojure programs, you should prefer lazy
;; sequences over loop/recur for **any** sequence that varies in size
;; **and** for any "large" sequence.

;; Coming to Realization

;; Remember, lazy sequences only consume resources when they are
;; **realized**; that is, when a portion of the sequences is
;; instantiated in memory.

;; For example, `take` returns a lazy sequence and does no realization
;; at all. For example, the following expression holds the first billion
;; (Yes. That is with a `B`!) Fibonacci numbers
(def lots-of-fibs (take 1000000000 (fibo)))

;; Notice tha creating `lots-of-fibs` returns almost immediately.
;; This, seemingly inexplicable action, does _almost nothing_. If you
;; ever call a function that **actually uses values** in `lots-of-fibs`,
;; Clojure will calculate them **at that time**. For example, the
;; following call will return the 100th Fibonacci number from
;; `lots-of-fibs` but **without** calculating the billion other
;; numbers that `lots-of-fibs` "promises" to provide.

(nth lots-of-fibs 100)

;; Most sequence functions return lazy sequences. If you are
;; uncertain, the function documentation will typically tell you the
;; answer.

;; Remember, though, the REPL is **not** lazy. It will try to print
;; all billion numbers in the sequence `(take 1000000000 (fibo))`.
;; You may not wish to wait.
;;
;; As a convenience for working with lazy sequences, you can configure
;; how many items will be printed in the REPL by setting the vale of
;; `*print-length*`. For example,

(set! *print-length* 10)
(take 1000000000 (fibo))
(fibo)
(set! *print-length* nil)  ;; Resets `*print-length*` to its default

;; Losing your head

;; We must consider one last aspect of working with lazy sequences.
;; Although we can create large (possibly infinite) sequences and
;; then only work with a part of that large sequence, our efforts
;; will fail if we (unintentionally) hald a reference to the part
;; of the sequence that **we no longer care about**.
;;
;; The most common way for this situation to arise is when one
;; accidentally holds on the head (the first item) of a sequence.
;;
;; For example, we could define a sequence as a top-level var:

(def head-fibo (lazy-cat [0N 1N] (map + head-fibo (rest head-fibo))))

;; This definition is a very pretty definition; however, it retains
;; the head of an infinite sequences. This retention ensures that,
;; when one uses a part of this sequence, the **entire sequence**,
;; at least up to the last item used, **remains in memory**.
;;
;; This definition works very well for small Fibonacci numbers
(take 10 head-fibo)

;; But fails with an `OutOfMemoryError` fo large Fibonacci numbers.
;; `(nth head-fibo 1000000)`

;; Unless you actually want to cache a sequences as you traverse it,
;; one **must be careful** to **not** keep a reference to the head of
;; the (large) sequence.

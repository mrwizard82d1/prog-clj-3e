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

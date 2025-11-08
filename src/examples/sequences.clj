(ns examples.sequences
  (:require [clojure.string :refer [join]]))

;; Sequences

;; THe interface for all sequences is found in `clojure.lang.ISeq`. This
;; interface contains three functions:
;;
;; - `first` - returns the first item in a sequence if any;
;;   otherwise, `nil`
;; - `rest` - returns a sequence of all items after the first.
;; - `cons` - Constructs a new sequence from an element and an existing
;;   sequence.

(first '(1 2 3))
(first '())

(rest '(1 2 3))
(rest '())

(cons 0 '(1 2 3))
(cons \a '())

;; FYI, from the Clojure Cheat Sheet for the function, `empty?`:
;;
;; > To check the emptiness of a seq, please use the idiom, `(seq x)`
;; > rather than `(not (empty? x))`.

;; The function, `seq`, returns `nil` it the argument is empty or is
;; `nil`. The `next` function returns the `seq` of items **after**
;; the first; that is, `(next aseq)` is the equivalent of
;; `(seq (rest aseq))`.

(next '(1 2 3))
(next '())

;; In addition to working on Clojure lists, the `seq` function works
;; on all other Clojure data structures as well. For example,

(first [1 2 3])
(rest [1 2 3])
(cons 0 [1 2 3])

;; Remember that the functions, `rest` and `cons`, return a **`seq`**;
;; they **do not** return an instance of the same type as the original
;; collection.

;; One can check if the returned type is a `seq` using the predicate,
;; `seq?`

(seq? (first '(1 2 3)))
(seq? (rest [1 2 3]))
(seq? (cons 0 [1 2 3]))

;; Remember, although seqs are very general, we sometimes want to work
;; with a specific implementation type.

;; One can treat maps as seqs by treating a map as a sequence of
;; key-value pairs.

(first {:fname "Lawrence" :lname "Jones"})
(rest {:fname "Lawrence" :lname "Jones"})
(cons [:mname "Allan"] {:fname "Lawrence" :lname "Jones"})

;; Finally, one can treat **sets** as seqs.

(first #{:the :quick :brown :fox})
(rest #{:the :quick :brown :fox})
(cons :jumped #{:the :quick :brown :fox})

;; Remember that maps and sets have a stable traversal order, but the
;; actual order is implementation dependent and **should not** be
;; relied on.
;;
;; If you want a **reliable order**, you can use the "sorted"
;; variations.

(sorted-set :the :quick :brown :fox)
(sorted-map :c 3, :b 2, :a 1,)

;; In addition to the core capabalities of `seq`, you will find two
;; additional capabilities immediately interesting, `conj` and `into`

;; For lists, `conj` and `into` add items to the **front** of the `seq`.
(conj '(1 2 3) :a)
(into '(1 2 3) '(:a :b :c))

;; For vectors, `conj` and `into` add items to the **back** of the `seq`.
(conj [1 2 3] :a)
(into [1 2 3] [:a :b :c])

;; Because `conj` (and related functions) do the **efficient thing**
;; for the underlying data structure, one can often write code that is
;; both efficient and completely decoupled from the specific and
;; concrete implementation.

;; Additionally, most Clojure sequence are **lazy**. Critically,
;; Clojure sequences are **immutable**. For people, immutability means
;; that it is easier to reason about Clojure sequences. For our code,
;; immutablitiy means that Clojure sequences are safe for
;; **concurrent access**.
;;
;; All is not "sunshine and roses", however. English-language (and
;; other human languages) more easily express changes using the concept
;; of **mutability**.

;; Using the sequence library

;; Functions in the sequence "library" can be grouped into four broad
;; categories:
;;
;; - Creating sequences
;; - Filter sequences
;; - Sequence predicates
;; - Transform sequences

;; Creating sequences

;; The function, `range` produces a a sequence from `start` to `end`
;; incrementing by `step` each time.
(range 10)
(range 10 20)
(range 1 25 2)
(range 0 -1 -0.25)
(range 1/2 4 1)

;; The function, `(repeat n x)` function repeats a value, `x`,
;; `n` times.
(repeat 5 1)
(repeat 10 "x")

;; `iterate` begins with a value, `x` and repeatedly (infinitely)
;; applies a function, `f` to generate the next value in the sequence.
;;
;; Because `iterate` returns an infinite sequence of values, we use
;; `take` to select the first 10 values.
(take 10 (iterate inc 1))

;; We can use `iterate` to generate **the entire sequence** of
;; positive integers
(def whole-numbers (iterate inc 1))

;; When called with a single argument, `repeat` returns a lazy,
;; infinitely long sequence of the single argument passed to
;; `repeat`.
(take 20 (repeat 1))

;; The `cycle` function takes a collection and repeatedly returns
;; each value in the collection.
(take 10 (cycle (range 3)))

;; The `interleave` function accepts **multiple collections**
;; producing a new collection that interleaves values from each of
;; these collections.
(interleave whole-numbers ["A" "B" "C" "D" "E"])

;; Closely related to `interleave` is `interpose` which returns a new
;; collection consisting of all the values in the original collection
;; argument but with the `separator` argument between each element.
(interpose "," ["apples", "bananas", "grapes"])

;; The `interpose` function works nicely with `str` to produce an
;; output string.
(apply str (interpose "," ["apples", "bananas", "grapes"]))

;; This usage of `interpose` with `str` is common enough that the Clojure
;; standard library has the function `join` that provides this function
;; "out of the box".
(join \, ["apples" "bananas" "grapes"])

;; Creating populated Clojure collections
;;
;; - `(list & elements)`
;; - `(vector & elements)`
;; - `(hash-set & elements)`
;; - `(hash-map key-1 val-1 ...)`

;; The `set` function works differently from `hash-set`. It accepts a
;; collection and adds all elements of that collection to the
;; returned value.
(set [1 2 3])

;; `hash-set` takes a variable number of arguments and add all these
;; arguments to the returned set.
(hash-set 1 2 3)

;; The functions, `vector` and `vec`, have a similar relationship.
;; The `vec` function takes a single argument while `vector` accepts
;; multiple arguments.
(vec (range 3))
(vector 0 1 2)

;; Filtering sequences

;; The most basic function for filtering a sequence is, unsurprisingly,
;; `filter`. It accepts a predicate and a collection and returns a
;; sequence containing **only** those elements of `coll` for which the
;; predicate function returns `true`.
(take 10 (filter even? whole-numbers))
(take 10 (filter odd? whole-numbers))

;; Similarly, `take-while`, takes elements from a collection until the
;; predicate function returns false.

;; Remember, a `set`, when invoked as a function, returns `true` if and
;; only if its argument is a member of the set.
(def vowel? #{\a \e \i \o \u})

;; Similarly, `complement`, accepts a predicate but returns the Boolean
;; negation of the result of the predicate function.
;;
;; For example, the following definition creates a function that
;; negates the result of applying `vowel?` to its argument.
(def consonant? (complement vowel?))

(take-while consonant? "the-quick-brown-fox")

;; The opposite of `take-while` is `drop-while`. This function skips
;; all elements of a collection for which the predicate returns `true`.
(drop-while consonant? "the-quick-brown-fox")

;; The functions, `split-at` and `split-with` split a collection
;; into two.
(split-at 5 (range 10))
(split-with #(<= % 10) (range 0 20 2))

;; Sequence predicates

;; Where filter functions take a predicate and return a sequence,
;; a sequence predicate applies the predicate function to each
;; member of the sequence.

;; For example, `every?` returns `true` if and only if invoking
;; the predicate on **every** member of the collection returns
;; `true`.
(every? odd? [1 3 5])
(every? odd? [1 3 5 8])

;; The `some` function returns `true` if at least one member of the
;; collection results in the `predicate` returning a true value.
(some even? [1 2 3])
(some odd? [1 2 3])
(some even? [1 3 5])

;; Even though our examples returned either `true` or `nil`, this
;; behavior is a consequence of using a predicate. If we use a function
;; that **does not** return a boolean value, `some` will return the
;; first item for which our function returns a `truthy` value.
(some #(rem % 5) [1 3 5])

;; As another example, we can create a similar expression using the
;; `identity` function to find the first logically true value in a
;; sequence.
(some identity [nil false 1 nil 2])

;; A common use of `some` is to perform a linear seach of a sequence to
;; determine if this sequence contains a **matchine** element. For example,
(some #{3} (range 20))

;; We also have "complementary" tests available to us.
(not-every? even? whole-numbers)
(not-any? even? whole-numbers)

;; Transforming sequence

;; The simplest transformation is `map`.
(map #(format "<p>%s</p" %)
     ["the" "quick" "brown" "fox"])

;; Using `map`  with a mapping function of **multiple** arguments.
(map #(format "<%s>%s</%s>" %1 %2 %1)
     ["h1" "h2" "h3" "h1"]
     ["the" "quick" "brown" "fox"])

;; Another common transformation is `reduce`.
(reduce + (range 1 11))
(reduce * (range 1 11))

;; The functions, `sort` and `sort-by`, can be used to sort a collection.
(sort [42 1 7 11])
(sort-by #(.toString %) [42 1 7 11])

;; Seq comprehensions

;; A basic example
(for [word ["the" "quick" "brown" "fox"]]
  (format "<p>%s</p>" word))

;; Emulate filters using a `:when` clause
(take 10
      (for [n whole-numbers :when (even? n)] n))

;; A `:while` clause continues evaluation only while its expression
;; is `true`
(for [n whole-numbers :while (even? n)] n)

;; Seq comprehensions support **multiple** binding expressions. This
;; example generates all possible squares on a chess board.
(for [file "ABCDEFGH"
      rank (range 1 9)]
  (format "%c%d" file rank))

;; The previous example iterates over `rank` faster than over `file`
;; because `rank` is listed "to the right" of `file` in the bindings.
;; We can iterate faster over the `file` binding by moving it to
;; the "rightmost" position (later in the bindings).
(for [rank (range 1 9)
      file "abcdefgh"]
  (format "%c%d" file rank))

;; Although many languages perform transformations, filters, and
;; comprehensions greedily, **do not** assume this behavior for
;; Clojure. Most sequence functions do not traverse elements until
;; they are actually needed.

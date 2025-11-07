(ns examples.sequences)

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

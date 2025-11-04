(ns examples.exploring)

;; Numbers
(+ 2 3)

;; Add many numbers
(+ 1 2 3 4)

;; Or add **no** numbers
(+)

;; Other arithmetic operators
(- 10 5)
(* 3 10 11)
(> 5 2)
(> 5 2 1)
(>= 5 5)
(< 5 2)
(< 1 2 3)
(= 5 2)

;; Divison may suprise you. (Integral division returns a **fraction**.)
(/ 22 7)

;; But a floating point argument produces a more typical result
(/ (float 22) 7)
(/ 22.0 7)

;; Integer only division utilizes the `quot` and `rem` functions
(quot 22 7)
(rem 22 7)

;; Append a `M` to perform arbitrary precision, floating-point math.
;; The following expression evalutes to 1.0 (because the large
;; floating-point number causes the division to underflow).
(+ 1 (/ 0.00001 1000000000000000000))

;; However, we can create a `BigDecimal` literal using `M` to perform
;; arbitrary precision, floating-point division.
(+ 1 (/ 0.00001M 1000000000000000000))

;; Similarly, one can use the `N` suffix to perform arbitrary precision
;; integer arithmetic.
;; This expression raises an `ArithmeticException`
;; (* 1000 1000 1000 1000 1000 1000 1000)

;; But this similar expression returns the correct value
(* 1000N 1000 1000 1000 1000 1000 1000)

;; Symbols

;; Functions
str
concat

;; Operators
+
-

;; Java classes
java.lang.String
java.util.Random

;; Clojure namespaces and...
clojure.core
;; ...Java packages
java.lang

;; Symbols
;; - Cannot start with a number
;; - Consist of alphanumeric characters as well as
;; - +, -, *, /, !, ?, ., _, and '
;;
;; Remember that Clojure treats the characters '/' and '.' specially
;; to support **namespaces**.

;; Collections

;; Vectors
[1 2 3]

;; Or with commas
[1, 2, 3]

;; Literal lists require the `quote` form or...
(quote (1 2 3))

;; ... the reader macro, '
'(1 2 3)

;; Sets are **unordered** collections...
#{1 2 3 5}

;; whose items are **unique**
(set [3 1 4 1 5 9 2 6 5])

;; A literal set with non-unique items raises a (reader) exception
;; #{3 1 4 1 5 9}

;; A Clojure map is a collection of key-value pairs
{"Lisp" "McCarthy" "Clojure" "Hickey"}

;; A comma between items is treated as whitespace
{"Lisp" "McCarthy", "Clojure" "Hickey"}
{"Lisp", "McCarthy", "Clojure", "Hickey"}

;; The most common key type is a **keyword**.
{:Lisp "McCarthy", :Clojure "Hickey"}

;; A keyword evaluates to itself.
:foo

;; "If several maps have keys in common, you can leverage this by
;; creating a record with `defrecord`"
(defrecord Book [title autho])

;; You instantiate a record with the `->Book` constructor function.
(->Book "Moby Dick" "Hermann Melville")

;; Once you instantiate a `Book`, it behaves almost like any other map.

;; Strings and Characters

;; Clojure strings reuse the Java String implementation
"This is a\nmultiline string"

"This is also
a multiline string"

;; The REPL shows string literals with escaped newlines; however, printing
;; a multiline string will print on multiple lines.
(println "Another\nmultiline\nstring")

;; Perhaps the most common string function is `str`. This function takes
;; any number of objects, converts them to strings, and concatenates the
;; results into a **single** string
(str 1 2 3)

;; The `str` function **ignores** `nil` values
(str 1 2 nil 3)

;; Similarly, Clojure characters are Java characters.

;; Their literal syntax is `\{letter}` where `letter` is a single
;; (Unicode) character.
\a

;; An example Unicode character is the Greek capital, omicron.
\u03a9

;; Some common characters are "named" to avoid the need for Unicode
\backspace
\formfeed
\newline
\return
\space
\tab

;; Booleans and `nil`

;; `true` is true
true

;; `false` is false
false

;; `nil` is nil
nil

;; Remember, in a Boolean context, `nil` evaluates to false
(if nil "True" "False")

;; Other than the two values, `false` and `nil`,
;; **everything else evaluates to true**
(if 4 "True" "False")

;; Notice that `true`, `false`, and `nil` follow the rules for symbols
;; but are read as **other special values** (either a Boolean or nil).

;; Perhaps unique to Clojure (or other LISPs), the empty list (`()`)
;; is **true**.
(if '() "() is true" "() is false")

;; Similarly unique, zero is **also true**
(if 0 "Zero is true" "Zero is false")

;; A predicate is a **function** that returns either `true` or `false`.
;; It is common in Clojure to include a trailing question mark for a
;; predicate function.
(true? true)
(true? false)
(true? "foo")

(false? true)
(false? false)

(nil? nil)
(nil? false)

(zero? 0)
(zero? 0.0)
(zero? 1e-315)

;; To find outher predicates, execute
(clojure.repl/find-doc #"\?$")

(ns examples.exploring
  (:require [clojure.string :as clj-str])
  (:import [java.io File]))

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

;; Functions

;; A Clojure function call is written as a list whose first element is
;; a Clojure function. For example, `str`
(str "hello" " " "world")

;; A function name is typically hyphenated. For example:
(take-while #(> % 0) [2 1 0])

;; By convention, if a function is a **predicate**, its name ends in a
;; question mark.
(string? "hello")
(keyword? :hello)
(symbol? 'hello)

;; To define you own (top-level) function, use `defn`.
(defn greeting
  "Returns a greeting of the form, 'Hello, username.'"
  [username]
  (str "Hello, " username))

(greeting "Larry")
(greeting 1)
(greeting 1.0)

;; If a function has a "doc-string" (and it should), you can print that
;; documentation
(clojure.repl/doc greeting)

;; If a caller fails to supply the `greeting` argument, Clojure raises
;; an `ArityException`.
(try
 (greeting)
 (catch Exception e
   (str (.getMessage e))))

;; An alternative form of `defn` allows one to create multiple functions
;; with the same name but different arities.
(defn greeting
  "Returns a greeting of the form, 'Hello, username'.
  The default `username` is 'World'."
  ([] (greeting "World"))
  ([username] (str "Hello, ", username)))

(greeting "Larry")
(greeting)

;; Additionally, you can create a function with variable arity by
;; including an **ampersand** in the parameter list. Clojure binds
;; the name **after the ampersand** to a sequence of all remaining
;; parameters. Clojure only supports a **single** variable arity
;; parameter and it must be **last** in the parameter list.

;; A function allowing two people to date with zero or more chaperones
(defn date [person-1 person-2 & chaperones]
  (println person-1 "and" person-2
           "went out with" (count chaperones) "chaperones."))

(date "Romeo" "Juliet" "Friar Lawrence" "Nurse")
(date "Romeo" "Juliet" "Monty Python")
(date "Romeo" "Juliet")

;; The `defn` form create functions at the **top-level** of a module.
;; To create a function local to another function, use an anonymous
;; function form.


;; Anonymous functions

;; Three reasons to create anonymous functions
;; - Function very brief
;; - Function only used inside **another** function
;; - Function **created** inside another function

;; An example: filtering short functions

;; Top-level
(defn indexable-word? [word]
  (> (count word) 2))

(filter indexable-word? (clj-str/split "A fine day it is" #"\W+"))

;; We can inline `indexable-word?` in the `filter` expression
(filter
 (fn [w] (> (count w) 2))
 (clj-str/split "A fine day it is"
                #"\W+"))

;; We can use the reader macro syntax for an anonymous function
(filter
 #(> (count %) 2)
 (clj-str/split "A fine day it is"
                #"\W+"))

;; Demonstrating the second reason for an anonymous function: to define
;; one function inside another.
(defn indexable-words [text]
  (let [indexable-word? (fn [w] (> (count w) 2))]
    (filter indexable-word?
            (clj-str/split text #"\W+"))))

(indexable-words "A fine day it is")

;; Creating a function at run-time
(defn make-greeter [greeting-prefix]
  (fn [username]
    (str greeting-prefix ", " username)))

(def hello-greeting (make-greeter "Hello"))

(def aloha-greeting (make-greeter "Aloha"))

(hello-greeting "World")
(aloha-greeting "World")

;; We can also use the result of `make-greeting` directly
((make-greeter "Howdy") "pardner")

;; Vars, Bindings and Namespaces

;; Some "definitions"
;; - A namespace is a collection of names (symbols) that refer to `vars`
;; - A `var` is **bound** to a value

;; Define a symbol, `foo`, refers to a `var` in the current namespace
;; bound to value 10.
(def foo 10)

foo

;; More specifically, the initial value of a `var` is called its
;; **root binding**.
;;
;; Sometimes, it is useful to have thread-local bindings for a `var`.

;; One can refer to a `var` directly.
(var foo)

;; The `#'` reader macro is almost always used in Clojure code; a
;; call to `var` is rare.
#'foo

;; One typically **does not** want to refer to a `var` directly;
;; however, a `var` has many abilities beyond simply storing a value.
;;
;; - The same name can be aliased into more than one namespace. This
;;   ability allows one to use convenient, short names.
;; - A `var` can have **metadata**. This metadata includes
;;   - Documentation
;;   - Type hints (for optimization)
;;   - Unit tests
;; - A `var` can be dynamically rebound on a per-thread basis.

;; Bindings

;; A `var` is bound to a name, but other kinds of bindings exist
;; as well. For example, a functional call binds values to parameters.
(defn triple [number]
  (* 3 number))

;; The value 10 is bound to the parameter, `number`, when the function,
;; `triple` is evaluated.
(triple 10)

;; The parameter bindings of a function have **lexical scope**. However,
;; parameter bindings are **not** the only way to create a lexical scope.
;; A `let` special form does nothing other than create a set of lexical
;; bindings. For example, `(let [bindings*] exprs*)` creates a set of
;; bindings that are in effect for each and every item in `exprs`.
;; Additional, the value of the `let` expression is the value of the
;; **last** expression in `exprs`.
;;
;; Imagine you want to calculat the coordinates of the four corners of
;; a square given the bottom ordinate, the left abscissa, and the size.
;; We can then calculate the `top` ordinate and `right` abscissa **once**,
;; and then use these values in **all** subsequent `exprs` evaluations.
(defn square-corners [bottom left size]
  (let [top (+ bottom size)
        right (+ left size)]
    [[bottom left]
     [top left]
     [top right]
     [bottom right]]))
(square-corners 3 5 7)

;; Destructuring

;; Destructuring, available in many languages, but perhaps pioneered
;; in Erlang, allows a developer to extract items inside structured
;; data without writing explicit code to "walk down" the data structure.

;; Imagine you're working with a database of book authors. Sometimes you
;; need the first name, sometimes you need the last name, and sometimes
;; you need both names.
;;
;; Without destructuring, code might look like:
(defn greet-author-1 [author]
  (println "Hello, " (:first-name author)))

(greet-author-1 {:last-name "Vinge" :first-name "Vernor"})

;; Although this code is typical of many languages, it also means
;; that `greet-author-1` actually has **implicet** access to
;; `:last-name`. This implicit access can cause problems. For example,
;; one might unit test this function by only supplying a map with a
;; `:first-name` key. The tests all work fine until the implementation
;; changes to access `:last-name`. Then the tests all break. Destructuring
;; more clearly indicates the error than all the tests failing by limiting
;; the data required by the function.

;; Here's the same example using destructuring of the `:first-name`
;; field **only**.
(defn greet-author-2 [{fname :first-name}]
  (println "Hello, " fname))
(greet-author-2 {:first-name "Vernor"})
(greet-author-2 {:last-name "Vinge" :first-name "Vernor"})

;; Destructuring works for **both** associative collections and
;; vectors.
;;
;; For example, in a three- (or larger) dimensional space, one
;; might only extract the first two coordinates of a point.
(let [[x y] [1 2 3]]
  [x y])

;; Destructuring of vectors supports **skipping** values.
;; For example,
(let [[_ _ z] [1 2 3]]
  z)

;; Sometimes, one wants to bind not only individual items in a
;; collection, but also the **entire** collection. Destructuring
;; supports **both** these bindings.
(let [[x y :as coords] [1 2 3 4 5 6]]
  (str "x: " x
       ", y: " y
       ", total dimensions " (count coords)))

;; As an example, lets create a function named `ellipsize` that takes a
;; string and returns the first three words of that string followed
;; by "..."
(defn ellipsize [words]
  (let [[w1 w2 w3] (clj-str/split words #"\W+")]
    (clj-str/join " " [w1 w2 w3 "..."])))
(ellipsize "The quick brown fox jumps over the lazy grey lamb.")

;; These examples illustrate only a **subset** of what is available when
;; using destructuring. See the Clojure documentation of binding forms and
;; the Clojure destructuring guide for all the details.

;; Namespaces

(def foo 10)

;; Resolve a `symbol` to see its fully qualified name. Remember, to avoid
;; **evaluating** the symbol, one must quote it.
(resolve 'foo)

;; Switching namespaces creating it if necessary.
;; This action must be taken in a REPL.
;; (in-ns 'myapp)
;;
;; Remember, when changing namespaces, the `java.lang` package is
;; automatically available, ...
;; String
;;
;; ...but Clojure namespaces are **not** automatically available.
;; (clojure.core/use 'clojure.core)
;;
;; Although `java.lang` is available automatically, other Java packages
;; **must be imported**. Executing
;; (File/separator)
;; ...raises an exception: "No such namespace: File"
;;
;; However, calling the same function with a fully-qualified package name
;; succeeds:
;; (java.io.File/separator)
;;
;; To use a short name instead of a fully-qualified name, one must import
;; (the) Java class(es) into the current namespace using `import`.
;; (import '(java.io File InputStream))
;;
;; Once imported, one can use the classes `File` and `InputStream`
;; **without** qualification.
;; (.exists (File. "/tmp"))
;;
;; Clojure functions are handled differently (although somewhat similarly).
;; For example, to use a Clojure `var` from another namespace without
;; qualification, one **refers** the external `var` into the **current**
;; namespace. For example, the following code works.
;; (require 'clojure.string)
;; (clojure.string/split "Something,separated,by,commas", #",")
;;
;; But, after the `require` call, using `split` without qualification
;; fails.
;; (split "Something,separated,by,commas", #",")
;;
;; One option to refer to functions without a fully qualified name is to
;; use an **alias**.
;; (require '[clojure.string :as clj-str])
;;
;; One can then call any function in `clojure.string` using the
;; specified alias:
;; (clj-str/split "Something,separated,by,commas" #",")
;;
;; This simple form of `require` imports **all** public `vars` in the
;; `clojure.string` namespace using the alias `clj-str`.

;; We can use certain functions **only** in the REPL
;; (clojure.repl/find-doc "ns-")

;; Metadata

;; "In Clojure, metadata is data that is *orthogonal to the logical value
;; of an object." (location 1488). For example, here is the metadat for
;; the `str` variable.
(meta #'str)

;; To add your own metadata (key/value pairs) to a `var`, use the
;; metadata reader macro, `^metadata form`.
;;
;; For example, the following (verbose) form adds metadata to the
;; `shout` function.
;;
(defn ^{:tag String} shout
  [^{:tag String} s]
  (clojure.string/upper-case s))

(meta #'shout)

;; Because `:tag` metadata is so common, one can use the short-form,
;; `^Classname` which expands to `^{:tag Classname}`. The definition
;; of `shout` can be rewritten as follows:
(defn ^String
  shout [^String s]
  (clojure.string/upper-case s))

(meta #'shout)

;; Finally, if you find the inline metadata disruptive, you can use the
;; `defn` form that defines one or more forms in parentheses followd by
;; the a metadata map.
(defn shout
  ([s] (clojure.string/upper-case s))
  {:tag String})

(meta #'shout)

;; Calling Java

;; Clojure provides simple, direct syntax for calling Java code:
;;
;; - Creating objects
;; - Invoking methods
;; - Accessing static methods
;; - Accessing static fields
;;
;; in addition, Clojure provides syntactic sugar that simplifies calling
;; Java from Clojure.
;;
;; Additionally, Clojure supports Java special casses such as primitives
;; and arrays.
;;
;; Finally, Clojure provides a set of convenience functions for common
;; tasks that would be unwieldy in Java.

;; Creating a new instance.
(new java.util.Random)

;; A more common shortcut: appending a period ('.') to the name of the
;; class to insantiate.
(java.util.Random.)

;; The result of the previous "new" Java calls were lost; however, we
;; can use `def` to capture the newly created instance. For example,
(def rnd (new java.util.Random))
rnd

;; Now that we have an instance of the class, we can call methods on
;; the instance using the dot (`.`) special form. For example,
(. rnd nextInt)

;; `Random` instances has a `nextInt` method that accetps an argument.
;; You could call this like:
(. rnd nextInt 10)

;; The `.` syntax can also be used to access:
;;
;; - Instance fields
;; - Static methods
;; - Static fields
;;
;; Here are some examples.

;; Instance field
(def p (java.awt.Point. 10 20))
p

;; Static method
(. System lineSeparator)

;; Static field
(. Math PI)

;; One can access methods and fields using the same `.` form.
;; If an instance has a method and a field with the same name, the
;; **method**  is preferred.
;;
;; To access a field with the same name as a method, one can prepend a
;; `-` to the name to apply **only to fields**. Here are some examples:

;; Instance field
(def p (java.awt.Point. 10 20))
(. p -x)

;; Static field
(. Math PI)

;; However, Clojure provides a more concise syntax for both instance and
;; static access is preferred.
;;
;; - `(.method instance & args)`
;; - `(.field instance)`
;; - `(.-field instance)`
;; - `(Class/method & args)`
;; - `Class/field`

;; One can rewrite the previous examples with the more concise syntax.
(.nextInt rnd 10)
(.x p)
(System/lineSeparator)
(Math/PI)


;; To avaid typing `java.util.Random` all the time, one can use the following
;; `import` form in a REPL or a similar `ns` option:
(import '(java.util Random Locale)
        '(java.text MessageFormat))

Random
Locale
MessageFormat

;; Finally, the Clojure REPL provides the function, `javadoc`, to access
;; Java documentation.
;; (javadoc java.net.URL)

;; Calling this expression like the book currently fails. However, one can;

(use 'clojure.java.javadoc)
(javadoc java.net.URL)

;; The result is tha the Clojure interpreter opens a brovser to the
;; specified information.

;; Comments

;; Clojure provides several ways to create comments:

;; This is a comment (automaticing formatting makes demonstration a bit
;; more difficult).
(comment (defn ignore-me []))
           ;; not done yet

;; A common use of the `comment` macro is to save a chunk of utility or
;; test code in a comment block at the bottom of a file.

;; Additionally, the reader macro, `#_` **reads** the next form but
;; **ignores** it.
(defn triple [number]
  #_(println "debug triple" number)
  (* 3 number))

;; Flow control

;; Branch with `if` (no `else`)
(defn is-small? [number]
  (if (< number 100) "yes"))

(is-small? 50)
(is-small? 50000)

;; `if` **with** `else`
(defn is-small? [number]
  (if (< number 100)
    "yes"
    (do
      ;; All expressions in the `do` form are evaluated for
      ;; **side-effects**. Only the value of the **last** form is
      ;; used as the value of the entire `do` expression.
      (println "Saw a big number" number)
      "no")))

(is-small? 20)
(is-small? 200)

;; Recur with loop/recur

;; The `loop` special form works like `let`:
;;
;; - It establishes **bindings** and, then,
;; - Evaluates expressions with these bindings.
;;
;; However, the `loop` special form also **sets a recursion point**;
;; that is, a point of execution which will be the "target" of a
;; `recur` special form.
;;
;; When the form `(recur exprs*)` is encountered, the interpreter
;; evaluates all the `exprs*` and then returns to the `loop` recursion
;; point binding the values of `exprs*` to the `bindings*` of the `loop`.
;;
;; Here is an example of a countdown using `loop/recur`.
(loop [result []
       x 5]
  (if (zero? x)
    result
    (recur (conj result x) (dec x))))

;; The top of a function is also a recursion point. For example,
(defn countdown [result x]
  (if (zero? x)
    result
    (recur (conj result x) (dec x))))

(countdown [] 5)

;; Although recursion is the "primary" technique for "loops" in Clojure,
;; you will probably not use it very much because the sequence library
;; provides so many common recursions. For example, our `countdown`
;; function could be expressed by:

(into [] (take 5 (iterate dec 5)))

(into [] (drop-last (reverse (range 6))))

(vec (reverse (rest (range 6))))

;; Beware, Clojure, unlike many other variants of LISP will **not**
;; perform automatic tail-call optimization (TCO). However, it **will**
;; optimize calls to `recur`.

;; Where's my for loop?

;; Clojure has **no** **for** loop and no direct mutable variables.
;; So how does one write code that would include a **for** loop in
;; most languages?
;;
;; We will illustrate this process by "porting" the function,
;; `StringUtils::indexOfAny`.

;; We begin by writing `indexed`, a Clojure function that pairs elements
;; in a collection with the index of each element.

(defn indexed [coll]
  (map-indexed vector coll))

(indexed "abcde")

;; Now let's write a function, `index-filter`, that returns the indices
;; of matching items instead of the matching items themselves.

(defn index-filter [pred coll]
  ;; When the predicate function is **not nil**
  (when pred
    ;; Loop over the indexed collection capturing the index of all
    ;; elements for which `(pred elt)` returns `true`.
    (for [[idx elt] (indexed coll) :when (pred elt)] idx)))

;; Clojure sets are **functions** that test membership in the set.
;; Consequently, one can pass a set of characters and a string to
;; `index-filter` and get back the indices of all characters in the
;; string that belong to the set.

(index-filter #{\a \b} "abcdbbb")

;; We've now actually accomplished **more** than the required
;; functionality. The function, `index-filter`, returns the indices
;; of **all** matching elements. We only need the **first match**.
;; Consequently, the Clojure function, `index-of-any` only needs
;; the **first** element of the result of `index-filter`.

(defn index-of-any [pred coll]
  (first (index-filter pred coll)))

(index-of-any #{\z \a} "zzabyycdxx")
(index-of-any #{\b \y} "zzabyycdxx")
(index-of-any #{\e} "zzabyycdxx")

;; The Clojure solution, `index-of-any`, is **vastly more general**
;; than the Java static method, `indexOfAny`.
;;
;; For example, suppose we wanted the third occurrence of "heads" in
;; a series of coin flips.
(nth
 (index-filter #{:h} [:t :t :h :t :h :t :t :t :h :t])
 2)

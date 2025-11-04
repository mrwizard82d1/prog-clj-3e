(ns examples.introduction)

;; Define functions
(defn blank? [str]
  (every? #(Character/isWhitespace %) str))


;; Define a structure (Record)
(defrecord Person [first-name last-name])

(def foo (->Person "Larry" "Jones"))

;; Define another function
(defn hello-world [username]
  (println (format "Hello, %s" username)))

;; Define a reference. In theory, it could be an `atom` instead of a `ref`;
;; however, the domain (banking) argues for a `ref` because transactions
;; occur in **pairs**.
(def accounts (ref #{}))

(defrecord Account [id balance])

accounts

;; Change the `ref`, `accounts`, in a "transaction."
(dosync
 (alter accounts conj (->Account "CLJ" 1000.00)))

accounts

;; Call a Java API directly
(System/getProperties)

;; Clojure provides syntactic sugar to making calling Java more easily.
(.. "hello" getClass getProtectionDomain)

;; Clojure functions implement the Java interfaces, `Runnable` and
;; `Callable`. These interfaces make it trivial to pass a Clojure function
;; to a Java `Thread`.
(.start (new Thread (fn [] (println "Hello" (Thread/currentThread)))))

;; Using the REPL
(println "hello world")

;; Encapsulate our "Hello World" into a function that can address a person
;; by name.
(defn hello [name]
  (str "Hello, " name))

;; Examples of using special REPL variables, `*1`, `*2`, and `*3`.
(hello "Larry")
(hello "Clojure")

(str *1 " and " *2)

;; The REPL also allows one to inspect the details of a thrown exception
;; using the special variable, `*e`
;;
;; Enter the following expression in the REPL
;; (/ 1 0)

;; And then query the details.
*e

;; The details of the exception may be elided. To get all the details,
;; use the function, `clojure.repl/pst`` (print stack trace).
(clojure.repl/pst)

;; One can save larger code snippets in a temporary file and load it into
;; the REPL.
(load-file "src/examples/temp.clj")

;; Create shared state using `atom`
(def visitors (atom #{}))

;; Swap a (new) visitor into `visitors`
(swap! visitors conj "Larry")

;; Peek at the current state using `deref` or the shortcut, `@`
(deref visitors)
@visitors

;; A newer version of our `hello` function that remembers who has visited.
(defn hello
  "Writes a hello message to `*out*`. Calls you by username. Knows if you
  have visited previously."
  [username]
  (swap! visitors conj username)
  (str "Hello, " username))

(hello "Mr Wizard")

@visitors

;; Require a library
;; (require 'clojure.java.io)
(clojure.java.io/file "temp.clj")

;; I copied the following code from the book example code.
(def fibs (lazy-cat [0 1] (map + fibs (rest fibs))))

(take 10 examples.introduction/fibs)

;; Other functions allow me to access documentation
(clojure.repl/doc str)

;; Additionally, the function `clojure.repl/find-doc` allows one to search
;; for any functions that match the regular expression argument.
(clojure.repl/find-doc "reduce")

;; One can query the source code of many Clojure functions; that is, the
;; one can print the source code of Clojure functions written in Clojure.
(clojure.repl/source identity)

;; Additionally, because Clojure functions **are** Java functions, one can
;; also use Java's reflection API. For example, one can use the methods:
;; - `class`
;; - `ancestors`
;; - `instance?`
(instance? java.util.Collection [1 2 3])

# sew lang

**sew lang** (**s**-**e**xpression **w**hile **lang**uage) é uma pequena linguagem de programação que implementa construções básicas de uma linguagem de programação imperativa.

Características: 

* [Imperativa](https://pt.wikipedia.org/wiki/Programação_imperativa)
* [Orientada a Expressões](https://en.wikipedia.org/wiki/Expression-oriented_programming_language), todas as construções da linguagem são expressões que podem ser avaliadas para algum valor, incluíndo declaração e atribuição de variáveis.
* [Dinamicamente Tipada](https://pt.wikipedia.org/wiki/Sistema_de_tipos#:~:text=Tipagem%20dinâmica%20é%20uma%20característica,ou%20a%20execução%20do%20programa.)

## Exemplos de programas

### Hello World

```scheme
(print "Hello World!")
```

### Fatorial

```scheme
(print "Enter the number:")
(var n (read-num))
(if (< n 0)
	(print "Invalid Number!")
	(begin
		(var fact 1)
		(var i 1)
		(while (< i (+ n 1))
		    (begin
		        (set fact (* fact i))
		        (set i (+ i 1))
		    )
		)
		(print "Factorial of " n " is " fact)
	)
)
```
Outros exemplos podem ser encontradas na pasta [examples](examples/).

## Guia

### Tipos

A **sew lang** permite valores de quantro tipos: *number*, *boolean*, *string* e *nil*. Todas as expressões da linguagem são avaliadar para algum valor desses tipos.

#### Número

Por questão de simplicidade, nossa linguagem só trabalha com um tipo numérico. Optamos por trabalhar com números de ponto flutuante para abranger mais valores.

Sintaxe: 

`number ::= "[-+]?[0-9]*\.?[0-9]+"`

Exemplos: `2`, `2.5` e `-3`

#### Booleano

Tipo auto-explicativo.

Sintaxe: 

`bool ::= true | false`

#### String

Nossa linguagem também suporta Strings, entretanto, seu uso é limitado visto que não implementamos expressões para manipulação de Strings. Seu uso principal é com o comando `print`.

Sintaxe: 

`string ::=  "\"[^\"]*\""`

Exemplos: `"Olá mundo!"`, `"Teste"`

#### Nil

Esse tipo foi criado para representar a ausência de valor ou valor nulo. É utilizado, por exemplo, em expressões de repetição para representar o caso em que uma expressão nunca é avaliada.

Sintaxe:
 
`nil ::= nil`

### Expressões

Nossa linguagem implementa uma Notação Prefixa (também conhecida como Notação Polonesa) para expressões aritméticas, relacionais e lógicas. Sendo assim, os operadores vem antes dos operandos. Por exemplo, a expressão "a + b" em uma notação prefixa fica "+ a b". Uma das vantagens dessa notação é que ela não exige uso de parênteses ou outros delimitadores para indicar quais  expressões devem ser avaliadas primeiro, fazendo com que regras de precedência e ambiguidades não sejam uma preocupação na nossa linguagem.

#### Expressões Aritméticas

Sintaxe: 

`arith-exp ::= (<op-arith> <exp> <exp>) | (- <exp>)`

`op-arith ::= + | - | * | /`

Exemplos: `(+ 2 3)`, `(* x 5)`, `(- (/ x 3) 32)`, `(- 1)`

#### Expressões Relacionais

Sintaxe: 

`rel-exp ::= (<op-rel> <exp> <exp>)`

`op-rel ::= = | < | > | <= | >=`

Exemplos: `(= 2 2)`, `(> x 0)`, `(<= x (+ y 2))`

#### Expressões Lógicas

Sintaxe: 

`bool-exp ::= (<op-bool> <exp> <exp>) | (not <exp>)`

`<opt-bool> ::= and | or`

Exemplos: `(and (>= x 0) (<= x 10))`, `(not (< x 1))`, `(or (= x y) (< y z))`


### Variáveis

#### Identificador

Toda variável deve ser associada a um identificado que é utilizado para que a variável possa ser referenciada no decorrer do programa. Um identificador deve iniciar com uma letra seguido de zero ou mais letras, dígitos ou o símbolo _.

#### Declaração

Na declaração de uma variável devemos atribuir a ela um identificador e um valor inicial. Essa variável é, então, vinculada ao ambiente mais próximo (escopo local). O valor de uma expressão de declaração é o valor atribuído a variável.

Sintaxe:

`<var-decl-exp> ::= (var <ident> <exp>)`

Exemplos: `(var x 0)`, `(var cond true)`, `(var y (+ y 1))`

#### Atribuição

Após declarada, uma variável pode ter seu valor alterado. Só podemos alterar uma variável que se encontra no ambiente de referenciamento (escopo local ou escopo mais externo). Caso essa variável não exista no ambiente de referenciamento, ou seja, ela não foi declarada previamente (com o `var`), será gerado um erro de execução na atribuição. O valor de uma expressão de atribuição é o valor atribuído a variável.

Sintaxe:

`<var-asig-exp> ::= (set <ident> <exp>)`

Exemplos: `(set x (+ x 1))`, `(set a (+ b c))`, `(set cond false)`


#### Expressão Condicional If

Uma expressão condicional `if` possui três expressões: uma condição (`<cond>`), uma expressão para o caso da condição ser verdadeira (`<then-exp>`) e uma expressão para o caso da condição ser falsa (`<else-exp>`). O valor da expressão depende do resultado da condição. A expressão `<then-exp>` só é avaliada se o resultado de `<cond>` for true, caso contrário é a expressão `<else-exp>` que é avaliada.

Sintaxe:

`<if-exp> ::= (if <cond> <then-exp> <else-exp>)`

Exemplos: `(if (> a b) (set max a) (set max b))`, `(if (< x 0) (set x (+ x 1)) x)`


#### Expressão de Repetição While

Uma expressão de repetição while possui duas expressões: uma condição (`<cond>`) e uma expressão do laço (`<do-exp>`). Na expressão while, a condição é avaliada primeiro. Caso ela seja verdadeira, a expressão do laço é avaliada e o processo é repetido novamente. Caso a condição seja falsa, o processo é finalizado. O resultado da expressão while é igual ao último resultado da avaliação da expressão do laço (resultado da última iteração). No caso em que a condição do while já começa sendo falsa, de modo que a expressão do laço nunca é avaliada, o resultado da expressão while é nil.

Sintaxe:

`<while-exp> ::= (while <cond> <do-exp>)`

Exemplos: `(while (< x 10) (set x (- x 1)))`


#### Expressão de Bloco

As expressões vistas acima são expressões com uma única instrução. Entretanto, é comum escrever programas que são compostos por várias instruções que são executadas de forma sequencial. Uma expressão de bloco permite definir uma lista de expressões que vão ser avaliadas de forma sequencial. O valor do bloco é igual ao valor da última expressão definida no bloco. Uma característica de um bloco é que ele possui seu próprio ambiente (escopo). Dessa forma, variáveis vinculadas (declaradas) dentro de um bloco só são visíveis dentro do próprio bloco ou em blocos internos (blocos aninhados), mas não são visíveis em blocos externos.

Sintaxe:

`<block-exp>: (begin <exp>*)`

Exemplos: 

```scheme
(begin
	(var x 10)
	(while (> x 0) (set x (- x 1)))
	(print x)
)
```

A definição de um bloco é a única forma de ter várias expressões sendo executadas dentro de um contexto[^1].


### Expressões de Entrada e Saída

#### Print

A expressão print permite que um ou mais valores sejam escrito na tela. O valor de uma expressão print é nil.

Sintaxe:

`<print> ::= (print <exp>*)`

Exemplos: `(print 1 " + " 2 " = " (+ 1 2))`

#### Read

A linguagem possui três expressões para leitura de dados: `(read-num)`, `(read-bool)` e `(read-str)` que, respectivamente, fazem a leitura de um número, valor booleano e string do teclado. O resultado de uma expressão de leitura é igual ao valor que foi lido.

[^1]: O interpretador da **sew lang** já cria de forma automática um bloco inicial para um programa que vai ser interpretado. Assim, já podemos definir um programa com várias expressões sequênciais sem ter que declarar elas dentro de um bloco inicial. Um exemplo disso é o programa do [Fatorial](examples/factorial.sew) que foi apresentado no início deste documento. Se o conteúdo desse programa não fosse inserido de forma automática dentro de um bloco, teríamos um erro de *parsing*.
# sew lang

## Projeto

Nesse projeto vamos implementar um interpretador para a **sew lang** (**s**-**e**xpression **w**hile **lang**uage), uma pequena linguagem de programação que possui apenas instruções básicas, como declaração e atribuição de variáveis, expressões aritméticas, relacionais e lógicas, uma estrutura condicional **if** e uma estrutura de repetição **while**. Nosso objetivo é por em prática conceitos de Linguagens de Programação através do projeto e implementação de um interpretador para a linguagem **sew lang**. Nesse projeto, o interpretador da nossa linguagem se encontra parcialmente implementado, de modo que a implementação de algumas expressões disponíveis na linguagem foram deixadas propositalmente de fora para que possam ser implementadas por você. As implementações pendentes são explicadas no decorrer deste documento e estão marcadas no código-fonte do projeto através de comentários iniciados com #. Seu objetivo é terminar a implementação desse interpretador completando as expressões que estão faltando.

Características da **sew lang**:

* [Imperativa](https://pt.wikipedia.org/wiki/Programação_imperativa)
* [Orientada a Expressões](https://en.wikipedia.org/wiki/Expression-oriented_programming_language), todas as construções da linguagem são expressões que podem ser avaliadas para algum valor, incluíndo declaração e atribuição de variáveis.
* [Dinamicamente Tipada](https://pt.wikipedia.org/wiki/Sistema_de_tipos#:~:text=Tipagem%20dinâmica%20é%20uma%20característica,ou%20a%20execução%20do%20programa.)

Mais detalhes da linguagem **sew lang**, como a sua especificação e exemplos de programas, podem ser encontrados no seu [guia](SEW-GUIDE.md).

Inspirações para a linguagem **sew lang** e para o projeto do interpretador:

* [While Language](http://profs.sci.univr.it/~merro/files/WhileExtra_l.pdf), uma pequena linguagem de programação (*toy language*) imperativa que possui poucos comandos e expressões (incluindo o laço **while**) que é utilizada como exemplo para o estudo de semântica de linguagens de programação.
* [Família Lisp](https://en.wikipedia.org/wiki/List_of_Lisp-family_programming_languages), lista de linguagens de programação que são descendentes ou inspiradas no [Lisp](https://pt.wikipedia.org/wiki/Lisp), tendo como destaques [Scheme](https://en.wikipedia.org/wiki/Scheme_(programming_language)), [Clojure](https://en.wikipedia.org/wiki/Clojure) e [Racket](https://en.wikipedia.org/wiki/Racket_(programming_language)).
* [Programming and Programming Languages](https://papl.cs.brown.edu/2020/Processing_Programs__Parsing.html) (PAPL), um livro sobre programação e conceitos de linguagens de programação que ensina semântica através da implementação de um interpretador para uma pequena linguagem de programação. Nosso projeto é fortemente influenciado por esse livro.
* [A Scalable Infrastructure for Teaching Concepts of Programming Languages in Scala with WebLab](https://dl.acm.org/doi/pdf/10.1145/2998392.2998402), nesse artigo os autores apresentam um relato de experiência do ensino de uma disciplina sobre conceitos de linguagens de programação utilizando a abordagem apresentada no livro PAPL e a linguagem de programação Scala.

### Requisitos

* Alguma IDE para Scala (é possível implementar sem, mas ela vai facilitar o processo). Sugerimos:
	- Eclipse
	- IntelliJ
*  [SBT](https://www.scala-sbt.org/download.html) (*Scala Build Tool*), uma ferramenta de build que vamos utilizar para gerenciar as dependências do projeto e automatizar os processos de compilação, execução e teste do nosso projeto:
	- Para quem for utilizar o Eclipse, é necessário instalar o SBT. 
	- Para quem for utilizar o IntelliJ, o SBT já vem junto com o plugin do Scala, portanto não é necessário instalá-lo de forma independente.
### Importando o projeto

Após baixado o projeto, este pode ser importado na sua IDE. Se estiver sendo utilizado o Eclipse, é necessário executar o comando `sbt eclipse` no terminal dentro da raiz do projeto. Isso vai fazer com que o projeto possa ser importado como um projeto Eclipse. Para quem utiliza o IntelliJ, o projeto pode ser importado de forma direta.

### Execução do projeto

Caso queira executar algum programa com o interpretador, é só executar o comando `sbt "run nome-programa.sew"` no terminal. Esse comando vai procurar o arquivo do programa indicado e vai interpretar ele. Caso você esteja dentro do ambiente do SBT, é só executar o comando `run nome-programa.sew`. Por exemplo, o comando `sbt "run examples/hello.sew"` vai executar o programa `hello.sew`.

## Sintaxe

A **sew lang** utiliza como sintaxe concreta [S-Expressions](https://en.wikipedia.org/wiki/S-expression) (*Symbolic Expressions*), uma notação simplificada para a definição de expressões que foi criada e popularizada com a linguagem de programação Lisp. Nessa notação, uma expressão pode ser um átomo (um valor atômico) ou uma lista (uma sequência de expressões entre parênteses). A expressão `(+ 3 (* 1 2))` é um exemplo de uma *s-expression* que é formada por uma lista contendo o símbolo `+`, o número 3 e uma outra lista, que por sua vez é formada pelo símbolo `*` e os números 1 e 2. 

Uma vantagem de se trabalhar com *s-expressions* é que por ter uma sintaxe muito simples, se torna fácil fazer a análise sintática (*parsing*), fazendo com que possamos implementar um analisador sintático (*parser*) com poucas linhas de código. No nosso projeto, esse parser é implementado no objeto [SExprParser](src/main/scala/sexpr/parser/SExprParser.scala), que implementa o método `parse` que recebe uma String como entrada, faz a análise sintática e retorna um objeto do tipo [SExpr](src/main/scala/sexpr/ast/SExpr.scala), que define uma base para a representação de *s-expressions* como uma árvore sintática abstrata (*Abstract Syntax Tree*, ou simplesmente AST). A AST para *s-expressions* é definida utilizando *case classes*:

```scala
abstract class SExpr
case class SNum(num: Double) extends SExpr
case class SString(string: String) extends SExpr
case class SSym(symbol: String) extends SExpr
case class SList(list: List[SExpr]) extends SExpr
```

Para exemplificar, o *parsing* da expressão `(+ x 1)` retorna o objeto `SList(List(SSym("+"), SSym("x"), SNum(1)))`.


## Sintaxe Abstrata

Apesar de simples e útil, uma *s-expression* sozinha não oferece nenhum significado ou intenção dado que sua representação é genérica, sendo necessário analisar suas expressões individuais para extrair algum significado específico. Por exemplo, a partir da expressão `(+ 2 3)` podemos interpretar que ela representa uma expressão aritmética de adição, dado o símbolo `+` no início, tendo como operandos os números 2 e 3, considerando uma notação pré-fixada. A partir disso, podemos avaliá-la para obter o resultado 5.

Ao invés de interpretar *s-expressions* de forma direta, vamos preferir uma representação mais direta que carrega consigo o seu significado (não seria mais fácil interpretar se a expressão `(+ 1 2)` fosse algo como `soma(1, 2)`?). Para isso, vamos definir uma AST para representar em alto nível as expressões suportadas na nossa linguagem. Nossa AST é baseada na sintaxe mostrada abaixo, que apresenta as expressões definidas na nossa linguagem (um subconjunto de *s-expressions* com palavras-chave e formas específicas):

```r
<exp> ::= NUMBER
       | 'true'
       | 'false'
       | 'nil'
       | STRING
       | ID
       | '(' '+' <exp> <exp> ')'
       | '(' '-' <exp> <exp> ')'
       | '(' '*' <exp> <exp> ')'
       | '(' '/' <exp> <exp> ')'
       | '(' '-' <exp> ')'
       | '(' '=' <exp> <exp> ')'
       | '(' '>' <exp> <exp> ')'
       | '(' '>=' <exp> <exp> ')'
       | '(' '<' <exp> <exp> ')'
       | '(' '<=' <exp> <exp> ')'
       | '(' 'and' <exp> <exp> ')'
       | '(' 'or' <exp> <exp> ')'
       | '(' 'not' <exp> ')'
       | '(' 'var' <exp> <exp> ')'
       | '(' 'set' <exp> <exp> ')'
       | '(' 'if' <exp> <exp> <exp> ')'
       | '(' 'while' <exp> <exp> ')'
       | '(' 'begin' <exp>* ')'
       | '(' 'print' <exp>* ')'
       | '(' 'read-num' ')'
       | '(' 'read-bool' ')'
       | '(' 'read-str' ')';
```

Assim como a AST de *s-expressions*, a AST da **sew lang** foi definida através de *case classes*, tendo como base a classe [Exp](src/main/scala/sewlang/ast/Exp.scala):

```scala
abstract class Exp
case class NumberExp(value: Double) extends Exp
case object TrueExp extends Exp
case object FalseExp extends Exp
case object NilExp extends Exp
case class StringExp(value: String) extends Exp
case class IdExp(id: String) extends Exp
case class SumExp(exp1: Exp, exp2: Exp) extends Exp // (+ exp1 exp2)
case class MultExp(exp1: Exp, exp2: Exp) extends Exp // (* exp1 exp2)
case class EqualExp(exp1: Exp, exp2: Exp) extends Exp // (= exp1 exp2)
case class LessThanExp(exp1: Exp, exp2: Exp) extends Exp // (< exp1 exp2)
case class NotExp(exp: Exp) extends Exp // (not exp)
case class AndExp(exp1: Exp, exp2: Exp) extends Exp // (and exp1 exp2)
case class VarDeclExp(id: Exp, exp: Exp) extends Exp // (var id exp)
case class VarAssignExp(id: Exp, exp: Exp) extends Exp // (set id exp)
case class IfExp(cond: Exp, thenExp: Exp, elseExp: Exp) extends Exp // (if cond exp-then exp-else)
case class WhileExp(cond: Exp, doExp: Exp) extends Exp // (while cond do-exp)
case class BlockExp(exps: List[Exp]) extends Exp // (begin exp*)
case class PrintExp(exps: List[Exp]) extends Exp // (print "x = " x)
case object ReadNumExp extends Exp // (read-num)
```

Se você olhar direito, vai ver que nossa AST não está completa. A representação de algumas expressões foram deixadas propositalmente de fora (expressões com: `-, /, <=, >, >=, or, read-bool, read-str`). Seu primeiro trabalho é completar elas (ver [#1](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/a359f3f659ab6c77de68ba5a732d0c2b8d4209ac/src/main/scala/sewlang/ast/Exp.scala#L16), [#2](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/a359f3f659ab6c77de68ba5a732d0c2b8d4209ac/src/main/scala/sewlang/ast/Exp.scala#L21), [#3](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/a359f3f659ab6c77de68ba5a732d0c2b8d4209ac/src/main/scala/sewlang/ast/Exp.scala#L26) e [#4](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/a359f3f659ab6c77de68ba5a732d0c2b8d4209ac/src/main/scala/sewlang/ast/Exp.scala#L44))!

Agora que temos nossa AST mais semântica, podemos implementar nosso interpretador de maneira mais fácil. Entretanto, nosso *parser* atual apenas analisa e retorna *s-expressions* (`SExpr`). Por isso, vamos passar por um segundo processo de *parsing* para converter *s-expressions* para a nossa nova notação `Exp`. Esse segundo *parser* é implementado no objeto [ExpParser](src/main/scala/sewlang/parser/ExpParser.scala), que implementa o método `parse` que recebe um objeto `SExpr` e retorna um objeto `Exp`. Além de traduzir *s-expressions* para um formato mais semântico para o nosso interpretador, esse novo *parser* também tem a função de rejeitar *s-expressions* que não se encaixam nas expressões definidas na nossa linguagem, uma vez que é gerado um erro caso a *s-expression* não case com nenhum dos padrões definidos. Visto que temos expressões que são formadas por outras expressões, nosso *parser* é implementado de forma recursiva de modo que também fazemos o *parsing* das expressões internas de uma expressão (percurso em árvore). Abaixo segue parte da implementação do nosso *parser*:

```scala
object ExpParser {
  def parse(sexp: SExpr): Exp = sexp match {
    case SNum(n) => NumberExp(n) // n
    case SString(s) => StringExp(s) // "string"
    case SSym("nil") => NilExp // nil
    case SSym("true") => TrueExp // true
    case SSym("false") => FalseExp // false
    case SSym(s) if isIdentifier(s) => IdExp(s) // id
    case SList(List(SSym("+"), exp1, exp2)) => SumExp(parse(exp1), parse(exp2)) // (+ exp1 exp2)
    case SList(List(SSym("="), exp1, exp2)) => EqualExp(parse(exp1), parse(exp2)) // (= exp1 exp2)
    case SList(List(SSym("not"), exp)) => NotExp(parse(exp)) // (not exp)
    case SList(List(SSym("var"), id, exp2)) => VarDeclExp(parse(id), parse(exp2)) // (var id exp)
    case SList(List(SSym("set"), id, exp2)) => VarAssignExp(parse(id), parse(exp2)) // (set id exp)
    case SList(List(SSym("if"), cond, thenExp, elseExp)) => IfExp(parse(cond), parse(thenExp), parse(elseExp)) // (if cond then-exp else-exp)
    case SList(List(SSym("while"), cond, doExp)) => WhileExp(parse(cond), parse(doExp)) // (while cond do-exp)
    case SList(SSym("begin") :: exps) => BlockExp(exps.map(parse(_))) // (begin exp*)
	// ...
    case _ => throw ExpParserException(s"error in the expression '$sexp'")
  }
}
```

Assim como na definição da nossa AST `Exp`, o nosso *parser* `ExpParser` também está incompleto. Seu segundo trabalho é implementar o processo de *parsing* para as expressões que foram deixadas de fora da AST (ver [#5](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/f3ffe229967dfd8473f6a39633bc31a51f8fca90/src/main/scala/sewlang/parser/ExpParser.scala#L21), [#6](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/f3ffe229967dfd8473f6a39633bc31a51f8fca90/src/main/scala/sewlang/parser/ExpParser.scala#L25), [#7](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/f3ffe229967dfd8473f6a39633bc31a51f8fca90/src/main/scala/sewlang/parser/ExpParser.scala#L29) e [#8](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/f3ffe229967dfd8473f6a39633bc31a51f8fca90/src/main/scala/sewlang/parser/ExpParser.scala#L43)). Além disso, também é seu trabalho implementar a verificação de palavras reservadas em identificadores. Nossa linguagem possui várias palavras-chave, algumas delas são `var`, `set`, `if` e `while`, por exemplo. Entretanto, da forma em que nossa verificação de identificador está implementada (ver método `isIdentifier`), é possível ter variáveis com nomes de palavras-chave. Apesar disso não ser um problema, o uso de palavras-chave como identificadores prejudica a legibilidade da linguagem visto que pode ser difícil diferenciar um identificador de uma palavra-chave no programa. Seu objetivo é proibir o uso de palavras-chave como identificadores (ver [#9](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/f3ffe229967dfd8473f6a39633bc31a51f8fca90/src/main/scala/sewlang/parser/ExpParser.scala#L59)).

Nesse mesmo arquivo também se encontram as tarefas [#14](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/f3ffe229967dfd8473f6a39633bc31a51f8fca90/src/main/scala/sewlang/parser/ExpParser.scala#L47), [#15](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/f3ffe229967dfd8473f6a39633bc31a51f8fca90/src/main/scala/sewlang/parser/ExpParser.scala#L50) e [#16](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/f3ffe229967dfd8473f6a39633bc31a51f8fca90/src/main/scala/sewlang/parser/ExpParser.scala#L53). Entretanto, elas só vão ser trabalhadas após a implementação do interpretador, quando a gente for trabalhar com *syntatic sugar*. Portanto, você deve pular essas tarefas e ir para as tarefas descritas na seção seguinte. Iremos retornar para elas mais para frente.

## Semântica

Agora que já passamos por todo o processo de *parsing*, já podemos implementar o nosso interpretador que vai receber uma expressão e gerar um valor a partir dela (avaliação).

### Sistema de Tipos

Nossa linguagem implementa quatro tipos de valores: número (*number*), booleano (*boolean*), *string* e *nil*. Por questão de simplicidade, vamos trabalhar com um único tipo numérico. Para isso, escolhemos o tipo `Double` do Scala que é o tipo numérico mais abrangente. O tipo booleano possui dois valores possíveis, `true` e `false`. O tipo *string* permite a definição de sequência de caracteres definidos entrem aspas duplas `"str"`. Por último, o valor `nil` serve para representar a ausência de valor ou valor nulo, algo que é necessário em alguns contextos. Para representar esses tipos de valores, definimos a classe [Value](src/main/scala/sewlang/interpreter/Value.scala) que define uma base para os outros tipos e uma classe para cada um dos tipos:

```scala
abstract class Value
case class NumberV(value: Double) extends Value
case class BoolV(value: Boolean) extends Value
case class StringV(value: String) extends Value
case object NilV extends Value
```

### Interpretador

Nosso interpretador é implementado no objeto [ExpInterpreter](src/main/scala/sewlang/interpreter/ExpInterpreter.scala) que implementa o método `eval` (*evaluate*) que recebe como entrada uma expressão (`Exp`) e retorna um valor (`Value`) resultante da avaliação da expressão. Assim como o nosso *parser*, o nosso interpretador é implementado de forma recursiva, de modo que é necessário avaliar as expressões internas de uma expressão que é formada por outras expressões.

#### Expressões Básicas

A avaliação das expressões básicas (que não são formadas por outras expressões e nem são variáveis) é quase trivial, sendo apenas uma tradução direta entre a expressão e o valor:

```scala
def eval(exp: Exp): Value = exp match {
    case NumberExp(n)   => NumberV(n)
    case StringExp(s)   => StringV(s)
    case TrueExp        => BoolV(true)
    case FalseExp       => BoolV(false)
    case NilExp         => NilV
    // ...
}
```

#### Variáveis, Escopo e Ambiente de Referenciamento

Nossa linguagem permite a declaração de variáveis que armazenam valores, podem ser utilizadas em expressões (sua avaliação é o valor que está sendo armazenado) e podem ter seu valor alterado (atribuição). Para armazenar variáveis, precisamos de uma estrutura de dados que seja capaz de armazenas valores que podem ser associados de alguma forma com um identificador único (se duas variáveis em um mesmo escopo possuem o mesmo nome, como iríamos diferenciá-las?). Para isso, vamos utilizar uma coleção mutável `Map` que armazena pares chave-valor, em que definimos a chave como sendo uma `String` contendo o nome do identificador e o valor é um objeto `Value` que é o valor que está sendo armazenado na variável:

```scala
val storage: Map[String, Value] = Map[String, Value]()
```

A coleção `storage` seria suficiente para armazenar todas as variáveis definidas em um programa. Entretanto, a **sew lang** permite a definição de diferentes [escopos](https://pt.wikipedia.org/wiki/Escopo_(computação)), de modo que podemos ter variáveis que pertencem a um ambiente e a outro não, assim como podemos ter um ambiente que é definido dentro de outro. Na nossa linguagem, esses ambientes são definidos a partir de blocos, que podem ter as suas próprias variáveis, assim como podem referenciar variáveis que são definidas em um bloco mais acima, caso esse ambiente tenha sido definido dentro de outro (bloco aninhado). Sendo assim, ter uma única coleção `storage` não seria suficiente para implementar isso (senão teríamos apenas um escopo global). Para permitir a existência de vários escopos, assim como permitir o referenciamento de variáveis de um ambiente em outro (mais interno), definimos a classe [Environment](src/main/scala/sewlang/interpreter/Environment.scala) que possui o seu próprio armazenamento (`storage`) e possui a referência para um ambiente pai (que está acima dele), quando ele existir. Nessa classe temos quatro métodos principais: `lookup`, que recebe como entrada um identificador e retorna o valor associado com ele; `declare`, que recebe um identificador e um valor e inclui eles no armazenamento local; `update`, que recebe um identificador e atualiza o seu valor no armazenamento (é necessário que ele tenha sido declarado primeiro); e `resolve`, que é o método de resolução de escopo. Esse método recebe um identificador como entrada e retorna o ambiente em que essa variável foi definida, olhando primeiro para o seu armazenamento local e em seguida olhando para o ambiente pai, caso essa variável não esteja sendo definida nele. O método retorna o ambiente em que a variável foi definida se esse ambiente existir e puder ser alcançado, caso contrário é gerado um erro de variável não vinculada. A classe `Environment` possui a seguinte estrutura:

```scala
class Environment(parent: Option[Environment]) {
  private val storage: Map[String, Value] = Map[String, Value]()
  def declare(id: String, value: Value): Value = // ...
  def update(id: String, value: Value): Value = /...
  def lookup(id: String): Value = resolve(id).storage(id)
  def resolve(id: String): Environment = // ...
}
```

Agora que já podemos armazenar variáveis, podemos implementar suas expressões. Uma vez que precisamos de um ambiente para poder avaliar uma variável, é necessário que este seja passado para o nosso método `eval` para que ele saiba onde uma variável pode ser obtida ou declarada. Por isso, vamos incluir o ambiente como um dos parâmetros de nosso método `eval`. Com isso, se torna fácil implementar a avaliação de uma variável:

```scala
  def eval(exp: Exp)(env: Environment): Value = exp match {
    // ...
    case IdExp(id)                     => env.lookup(id)
    // ...
  }
```

A declaração e atribuição de uma variável também é feita de forma simples, entretanto precisamos fazer algumas verificações a mais. Para que uma variável possa ser armazenada no ambiente, é necessário que a primeira expressão passada para a declaração ou atribuição seja a de um identificador para que este possa ser associado a um valor. Na nossa implementação, essa verificação é feita no momento da avaliação, de modo que se não for passado um identificador vai ser gerado um erro. Se a expressão do identificador for válida, então a expressão com a atribuição é avaliada e, em seguida, o identificador e o valor são inseridos ou atualizados no ambiente:

```scala
  def eval(exp: Exp)(env: Environment): Value = exp match {
    // ...
    case VarDeclExp(id, exp)           => evalVarDecl(id, exp)(env)
    case VarAssignExp(id, exp)         => evalVarAssign(id, exp)(env)
    // ...
  }
  // ...
  private def evalVarDecl(id: Exp, exp: Exp)(env: Environment): Value = id match {
    case IdExp(id) => env.declare(id, eval(exp)(env))
    case _         => throw ExpIdentifierRequiredException("identifier is required in a variable declaration")
  }
  
  private def evalVarAssign(id: Exp, exp: Exp)(env: Environment): Value = id match {
    case IdExp(id) => env.update(id, eval(exp)(env))
    case _         => throw ExpIdentifierRequiredException("identifier is required in a variable assignment")
  }
```

#### Expressões Aritméticas, Relacionais e Lógicas

Para a avaliação de expressões aritméticas, relacionais e lógicas, se faz necessário, primeiro, avaliar os seus operandos para depois efetuar a operação. Após avaliados, se faz necessário verificar o tipo dos valores obtidos para verificar se é possível efetuar a operação ou não: expressões aritméticas requerem dois valores numéricos, gerando um valor numérico como saída; expressões relacionais também requerem dois valores numéricos, mas o valor gerado é booleano; por fim, expressões lógicas requerem dois valores booleanos e geram um valor booleanos como saída. Para simplificar a avaliação dessas expressões, definimos métodos auxiliares que verificam o tipo dos valores gerados e efetuam a operação através de uma função que define o cálculo a ser feito. Abaixo mostramos a avaliação das expressões aritméticas:

```scala
  def eval(exp: Exp)(env: Environment): Value = exp match {
    // ...
    case SumExp(exp1, exp2)            => evalArithExp(_ + _, eval(exp1)(env), eval(exp2)(env))
    case MultExp(exp1, exp2)           => evalArithExp(_ * _, eval(exp1)(env), eval(exp2)(env))
    // ...
  }
  // ...
  private def evalArithExp(op: (Double, Double) => Double, val1: Value, val2: Value): Value = (val1, val2) match {
    case (NumberV(num1), NumberV(num2)) => NumberV(op(num1, num2))
    case _                              => throw ExpInvalidValueTypeException(s"arithmetic expression requires number values but received '$val1' and '$val2'")
  }
```

A avaliação de expressões relacionais e lógicas é feita de forma semelhante, mas com as devidas diferenças em relação aos tipos de valores que elas requerem e geram (ver código de [ExpInterpreter](src/main/scala/sewlang/interpreter/ExpInterpreter.scala)). Com o que vimos até o momento, você já é capaz de implementar a avaliação da maioria das expressões que não foram implementadas e que você já começou a implementar mais acima. Sendo assim, complete a implementação do interpretador para as expressões aritméticas (`-, /`), relacionais (`<=, >, >=`) e lógicas (`or`) que você está responsável por implementar (ver [#10](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/a359f3f659ab6c77de68ba5a732d0c2b8d4209ac/src/main/scala/sewlang/interpreter/ExpInterpreter.scala#L30), [#11](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/a359f3f659ab6c77de68ba5a732d0c2b8d4209ac/src/main/scala/sewlang/interpreter/ExpInterpreter.scala#L34) e [#12](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/a359f3f659ab6c77de68ba5a732d0c2b8d4209ac/src/main/scala/sewlang/interpreter/ExpInterpreter.scala#L38)).


#### Expressão Condicional If

Nossa linguagem também possui uma expressão condicional que recebe três expressões: uma condição, uma expressão que é avaliada caso a condição seja avaliada para o valor booleano `true` e uma expressão que é avaliada no caso da condição ser avaliada para o valor booleanos `false`. Para essa expressão, também se faz necessário verificar se o tipo do valor da condição é booleano visto que precisamos saber seu resultado para saber qual das duas expressões vão ser avaliadas e vão ser retornadas como resultado da expressão condicional. A implementação da expressão condicional segue abaixo:

```scala
  def eval(exp: Exp)(env: Environment): Value = exp match {
    // ...
    case IfExp(cond, thenExp, elseExp) => evalIfExp(eval(cond)(env), thenExp, elseExp)(env)
    // ...
  }
  // ...
  private def evalIfExp(cond: Value, thenExp: Exp, elseExp: Exp)(env: Environment): Value = cond match {
    case BoolV(true)  => eval(thenExp)(env)
    case BoolV(false) => eval(elseExp)(env)
    case _            => throw ExpInvalidValueTypeException(s"if expression requires a boolean expression in the condition")
  }
```

#### Expressão de Repetição While

Nossa linguagem possui a expressão `while` que permite com que uma expressão seja avaliada de forma repetida dada uma expressão de condição que deve ser avaliada em cada repetição. Enquanto a expressão de condição for avaliada para o valor `true`, a outra expressão vai ser avaliada. Esse processo só é encerrado quando a expressão de condição for avaliada para `false`. Como o nosso  `while` também é uma expressão, sua avaliação deve retornar um valor. Na **sew lang**, o valor retornado por uma expressão `while` corresponde ao último valor que foi avaliado antes da condição ser avaliada para `false`. Entretanto, existe a possibilidade da expressão no corpo do `while` nunca ser avaliada caso a condição já seja avaliada para `false` logo no início. Nessa situação, o valor retornado pela expressão é `nil`, que é um valor utilizado justamente para representar a ausência de valores. A implementação da avaliação da nossa expressão de repetição foi feita utilizando uma função recursiva de cauda:

```scala
  def eval(exp: Exp)(env: Environment): Value = exp match {
    // ...
    case WhileExp(cond, doExp)         => evalWhileExp(cond, doExp)(env)
    // ...
  }
  // ...
  private def evalWhileExp(cond: Exp, doExp: Exp)(env: Environment): Value = {
    @annotation.tailrec
    def loopWhile(cond: Exp, doExp: Exp, resultVal: Value): Value = eval(cond)(env) match {
      case BoolV(false) => resultVal
      case BoolV(true)  => loopWhile(cond, doExp, eval(doExp)(env))
      case _            => throw ExpInvalidValueTypeException(s"while expression requires a boolean expression in the condition")
    }
    loopWhile(cond, doExp, NilV)
  }
```

#### Expressão de Bloco

As expressões implementadas até o momento são únicas, no sentido que definem uma única instrução. Entretanto, programas imperativos são implementados, geralmente, através da execução de várias instruções de forma sequencial, de modo que executamos uma operação seguida de outra para realizar a computação que estamos desejando. Para permitir isso, nossa linguagem implementa expressões de bloco. Uma expressão de bloco é uma expressão formada por outras expressões que são avaliadas de maneira sequencial, seguindo a ordem em que elas são inseridas no bloco. Como um bloco por si só também é uma expressão, o resultado da avaliação de um bloco é o resultado da sua última expressão a ser avaliada (se nenhuma expressão for definida, o resultado do bloco é `nil`). Uma outra característica de expressões de bloco é que elas criam um novo escopo através criação de um ambiente para as suas expressões. O ambiente que é criado para um bloco possui como pai o ambiente ao qual o bloco pertencia. Por exemplo, em um bloco aninhado, o pai do ambiente do bloco mais interno é o ambiente do bloco mais externo. Isso possibilita que variáveis que são definidas em um bloco mais externos possam ser referenciadas no bloco mais interno, mas o contrário não é verdade, visto que um ambiente só possui referência para o seu ambiente pai e não para os filhos. A implementação da avaliação de uma expressão de bloco pode ser vista abaixo:

```scala
  def eval(exp: Exp)(env: Environment): Value = exp match {
    // ...
    case BlockExp(exps)                => evalBlockExp(exps)(env)
    // ...
  }
  // ...
  private def evalBlockExp(exps: List[Exp])(env: Environment): Value = {
    val blockEnv = Environment(env)
    val defaultVal: Value = NilV
    exps.foldLeft(defaultVal)((value: Value, exp: Exp) => eval(exp)(blockEnv))
  }
```

#### Expressões de Entrada e Saída

Para finalizar o nosso interpretador, vamos implementar as expressões de entrada e saída de dados da **sew lang**. A expressão `print` recebe uma sequência de expressões que são avaliadas e tem seu valor impresso na tela. Independente dos valores avaliados em uma expressão `print`, o seu resultado é `nil`. Para a entrada de dados, a **sew lang** possui a expressão `read-num` que permite a leitura de um número digitado no teclado. As expressões `print` e `read-num` são implementadas utilizando as funções de entrada e saída disponíveis no Scala:

```scala
  def eval(exp: Exp)(env: Environment): Value = exp match {
    // ...
    case PrintExp(exps)                => evalPrintExp(exps)(env)
    case ReadNumExp                    => NumberV(readDouble())
    // ...
  }
  // ...
  private def evalPrintExp(exps: List[Exp])(env: Environment): Value = {
    print(exps.map(exp => eval(exp)(env)).mkString(""))
    NilV
  }
```

Além de `read-num` para a leitura de números, a **sew lang** também tem as expressões `read-bool` e `read-str` para a leitura de valores booleanos e strings, respectivamente. Seu trabalho é implementar a avaliação dessas duas expressões (ver [#13](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/a359f3f659ab6c77de68ba5a732d0c2b8d4209ac/src/main/scala/sewlang/interpreter/ExpInterpreter.scala#L49)).

#### Execução

Com essa última implementação, nossa linguagem já está totalmente operável, considerando as expressões que foram descritas no seu [guia](SEW-GUIDE.md). O processo de execução do nosso interpretador é implementado no objeto [ExpFileRunner](src/main/scala/sewlang/runner/ExpFileRunner.scala). Esse processo consiste em: 

1. Abrir um arquivo que é passado como entrada contendo o código fonte do programa a ser interpretado
2. Colocar o conteúdo do arquivo dentro de uma expressão de bloco
3. Passar esse programa pelo parser de *s-expressions* ([SExprParser](src/main/scala/sexpr/parser/SExprParser.scala))
4. Passar a *s-expression* resultante pelo parser da AST da **sew lang** ([ExpParser](src/main/scala/sewlang/parser/ExpParser.scala))
5. Por último, passar a expressão resultante para o interpretador para que o programa possa ser executado ([ExpInterpreter](src/main/scala/sewlang/interpreter/ExpInterpreter.scala)).

## Syntactic Sugar

Nossa linguagem possui um número restrito de expressões, mas não seria interessante implementar mais algumas? 

Uma forma de se fazer isso é definir essas novas expressões, incrementar a nossa AST, depois incrementar o nosso parser para que ele reconheça as novas expressões, por fim incrementar nosso interpretador com a avaliação dessas novas expressões. Esse processo é inevitável quando queremos implementar expressões e recursos mais complexos, como adicionar a definição de funções na linguagem, por exemplo. Mas algumas novas expressões poderiam ser adicionadas sem que fosse necessário ter todo esse trabalho, visto que algumas expressões poderiam ser definidas em termos de outras que já foram implementadas. Isso é conhecido como [*syntatic sugar*](https://en.wikipedia.org/wiki/Syntactic_sugar) (açúcar sintático), uma forma de criar novas expressões mais "açucaradas" sem tornar a implementação da linguagem mais complexa. 

Algumas construções clássicas de algumas linguagens de programação são implementadas dessa forma. Por exemplo, as atribuições ampliadas do C nada mais são do que açúcar sintáticos. O comando `x += 5;`, por exemplo, nada mais é do que uma versão simplificada do comando `x = x + 5;`, assim como `++a` pode ser definido como `a = a + 1` (se levar em conta apenas a atribuição). Também poderíamos enxergar o comando `for` do C como uma açúcar sintático visto que ele também pode ser definido em termos do `while`. Os dois códigos abaixo, por exemplo, são equivalentes:

```c
for(int i = 0; i < 10; i++) {
	printf("%d\n", i);
}
```

```c
{
	int i = 0;
	while(i < 10) {
		printf("%d\n", i);
		i++;
	}
}
```

Mas se um comando ou expressão pode ser definido em termos de outros, por que ele deveria existir? A resposta para isso é a facilidade. Construções "açucaradas" tornam o código mais fácil de ler e escrever, além de deixar a linguagem mais expressiva. Isso tudo pode ser obtido sem que a "essência" da linguagem seja alterada.

Partindo dessa premissa, vamos implementar novas expressões para a **sew lang** sem ter que alterar o nosso interpretador. Para isso, nossa estratégia vai ser fazer transformações sintáticas em *s-expressions* para que as novas expressões sejam definidas em termos das outras que já foram implementadas. Essas modificações vão ser feitas no nosso segundo parser ([ExpParser](src/main/scala/sewlang/parser/ExpParser.scala)), onde vamos incluir novos padrões para as novas expressões e vamos reescrever essas novas expressões para que elas sejam definidas em termos das expressões que já foram implementadas. Esse processo é conhecido como *desugaring* (algo como "desaçucarar"). Com isso, não vamos precisar mudar a nossa AST ([Exp](src/main/scala/sewlang/ast/Exp.scala)), assim como não vamos precisar mudar o nosso interpretador ([ExpInterpreter](src/main/scala/sewlang/interpreter/ExpInterpreter.scala)), uma vez que as novas expressões já vão chegar "desaçucaradas". Para incrementar nossa linguagem, decidimos implementar as seguintes expressões:

1. `(++ id)`, que vai ser transformada na expressão `(set id (+ id 1))`
2. `(+= id exp)`, que vai ser transformada na expressão `(set id (+ id exp))`
3. `(for init cond mod body)`, que vai ser transformada na expressão `(begin init (while cond (begin body mod)))`

Todas essas expressões foram inspiradas nos exemplos em C que mostramos mais acima. Para facilitar o processo de *desugaring*, criamos o objeto [ExpDesugar](src/main/scala/sewlang/parser/ExpDesugar.scala) que implementa o método `desugar` que recebe como entrada um objeto `SExpr` e também retorna um objeto `SExpr`. Se a *s-expression* corresponder a alguma das expressões que queremos reescrever, então é criado uma nova *s-expression* com as transformações necessárias para que a expressão possa ser escrita em termos das outras expressões que já foram implementadas. O código do nosso *desugar* pode ser visto abaixo:

```scala
object ExpDesugar {
  def desugar(sexp: SExpr): SExpr = sexp match {
    case SList(List(SSym("++"), id))      => SList(List(SSym("set"), id, SList(List(SSym("+"), id, SNum(1)))))
    case SList(List(SSym("+="), id, exp)) => SList(List(SSym("set"), id, SList(List(SSym("+"), id, exp))))
    case SList(List(SSym("for"), init, cond, mod, body)) =>
      SList(List(
        SSym("begin"),
        init,
        SList(List(
          SSym("while"), cond,
          SList(List(
            SSym("begin"),
            body,
            mod))))))
    case _ => sexp
  }
}
```

Agora só precisamos adicionar essas transformações no nosso parser para que as novas expressões possam ser utilizadas:

```scala
object ExpParser {
  def parse(sexp: SExpr): Exp = sexp match {
    // ...
    case SList(List(SSym("++"), id)) => parse(ExpDesugar.desugar(sexp))
    case SList(List(SSym("+="), id, exp)) => parse(ExpDesugar.desugar(sexp))
    case SList(List(SSym("for"), init, cond, mod, body)) => parse(ExpDesugar.desugar(sexp))
    // ...
  }
}
```

Com isso, já podemos implementar um programa como o abaixo:

```scheme
(for (var x 1) (< x 11) (++ x)
	(print "Number: " x)
)
```

que acaba sendo muito mais simples do que esse outro (você não acha?):

```scheme
(begin
	(var x 1)
	(while (< x 11)
		(begin
			(print "Number: " x)
			(set x (+ x 1))
		)
	)
)
```

Sua última tarefa é implementar mais algumas expressões para a **sew lang**, mas dessa vez como açúcar sintáticos (ver [#14](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/f3ffe229967dfd8473f6a39633bc31a51f8fca90/src/main/scala/sewlang/parser/ExpParser.scala#L47) ([#14](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/f3ffe229967dfd8473f6a39633bc31a51f8fca90/src/main/scala/sewlang/parser/ExpDesugar.scala#L12)), [#15](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/f3ffe229967dfd8473f6a39633bc31a51f8fca90/src/main/scala/sewlang/parser/ExpParser.scala#L50) ([#15](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/f3ffe229967dfd8473f6a39633bc31a51f8fca90/src/main/scala/sewlang/parser/ExpDesugar.scala#L15)) e [#16](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/f3ffe229967dfd8473f6a39633bc31a51f8fca90/src/main/scala/sewlang/parser/ExpParser.scala#L53) ([#16](https://github.com/jbsneto-cefetmg/sewlang-interpreter/blob/f3ffe229967dfd8473f6a39633bc31a51f8fca90/src/main/scala/sewlang/parser/ExpDesugar.scala#L28))). Inspirado no C, você vai implementar a operação de decremento (`(-- x)`) e mais expressões de atribuição ampliada (`-=, *=, /=`). Por último, você vai implementar mais um laço de repetição, o `(repeat body until-cond)`, que é inspirado no laço `repeat-until` da linguagem de programação [Lua](https://www.lua.org/manual/5.2/pt/manual.html#3.3.4). Nessa expressão, a expressão no corpo do laço (`body`) é avaliada até a condição ser verdadeira (`until-cond`), ou seja, enquanto a condição for falsa o laço continua.

## Desafios

1. Na **sew lang**, o tipo *string* possui um uso mais restrito visto que não existe nenhuma expressão específica para manipular strings. Então na prática, seu uso serve apenas para a saída de dados (`print`). Você consegue pensar em operações específicas que tornariam o tipo mais útil na linguagem? Se sim, implemente!

2. Você consegue pensar em novos tipos de expressões que poderiam ser implementadas na **sew lang** (talvez inspirado em instruções de outras linguagens de programação)? Se sim, como implementaria, como um açúcar sintático ou como uma expressão padrão da linguagem? Implemente! Quer uma sugestão? Implementa a expressão `cond` da linguagem de programação [Scheme](https://en.wikibooks.org/wiki/Scheme_Programming/Conditionals), ela é uma forma mais simplificada de escrever uma expressão condicional `if` com várias condições.

3. Implemente um interpretador interativo para a **sew lang** semelhante aos interpretadores interativos disponíveis para linguagens como Python e Scala. Esses interpretadores são chamados de REPL, que é a sigla para *Read-Eval-Print-Loop*, que indica as operações que devem ser implementadas: ler um comando, avaliar ele, imprimir o resultado da avaliação e repetir o processo.

4. Implemente um *transpiler* (compilador) para a **sew lang**. Uma simples diferença entre um interpretador e um compilador é que o primeiro recebe como entrada o código fonte de um programa e o executa. Já um *transpiler*/compilador recebe o código fonte de um programa como entrada e gera como saída uma versão desse programa em alguma outra linguagem. Considerando nosso projeto, é possível aproveitar todo o código relacionado com o processo de *parsing* e implementar um *transpiler*/compilador que recebe como entrada uma expressão (`Exp`, assim como nosso interpretador) e retorna como saída o código fonte de um programa em outra linguagem de programação que corresponde a aquela expressão (seguindo as regras de tradução que você definir). Por exemplo, uma expressão como `(var x (+ 2 3))` poderia ser traduzida para a expressão `var x = (2 + 3);` em Javascript (ou qualquer outra linguagem com sintaxe parecida) mantendo a mesma semântica de nossa linguagem. O desafio é implementar um *transpiler* que traduz um código em **sew lang** para alguma outra linguagem de programação.
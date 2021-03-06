package com
package github
package kmizu
package macro_peg

/** This object provides types representing ASTs of extended PEG.
  * It is used as namespace.
  * @author Kota Mizushima
  */
object Ast {
  type ==>[-A, +B] = PartialFunction[A, B]
  
  val DUMMY_POS = Pos(-1, -1)
  
  /** A trait for types that has position. */
  trait HasPosition { def pos: Pos }
  /** This class represents position in a source file.
    * @param line line number (0-origin)
    * @param column column number (0-origin) */
  case class Pos (line: Int, column: Int)
  /** This class represents an AST of PEG grammar.
    * @param pos position in source file
    * @param rules the list of rules constituting PEG grammar */
  case class Grammar(pos: Pos, rules: List[Rule]) extends HasPosition {
    def +(newRule: Rule): Grammar = Grammar(pos, rules = newRule::rules)
  }
  /** This class represents an AST of rule in PEG grammar.
    * @param pos position in source file
    * @param name the name of this rule.  It is referred in body
    * @param body the parsing expression which this rule represents */
  case class Rule(pos: Pos, name: Symbol, body: Exp, args: List[Symbol] = Nil) extends HasPosition
  /** This trait represents common super-type of parsing expression AST. */
  sealed trait Exp extends HasPosition
  /** This class represents an AST of sequence (e1 e2).
    * @param pos position in source file
    * @param lhs e1
    * @param rhs e2 */
  case class Seq(pos: Pos, lhs: Exp, rhs: Exp) extends Exp
  /** This class represents an AST of ordered choice (e1 / e2).
    * @param pos position in source file
    * @param lhs e1
    * @param rhs e2 */
  case class Alt(pos: Pos, lhs: Exp, rhs: Exp) extends Exp
  /** This class represents an AST of repetition e*.
    * @param pos position in source file
    * @param body e */
  case class Rep0(pos: Pos, body: Exp) extends Exp
  /** This class represents an AST of one-or-more repetition e+.
    * @param pos position in source file
    * @param body e */
  case class Rep1(pos: Pos, body: Exp) extends Exp
  /** This class represents an AST of zero-or-one occurrence e?.
    * @param pos position in source file
    * @param body e */
  case class Opt(pos: Pos, body: Exp) extends Exp
  /** This class represents an AST of and-predicate &(e).
    * @param pos position in source file
    * @param body e */
  case class AndPred(pos: Pos, body: Exp) extends Exp
  /** This class represents an AST of not-predicate !(e).
    * @param pos position in source file
    * @param body e */
  case class NotPred(pos: Pos, body: Exp) extends Exp
  /** This class represents an AST of string literal "...".
    * @param pos position in source file
    * @param target literal */
  case class Str(pos: Pos, target: String) extends Exp
  /** This class represents an AST of wild-card character ..
    * @param pos position in source file */
  case class Wildcard(pos: Pos) extends Exp
  /** This class represents an AST of character set,
    *  which is created from CharSet.
    */
  case class CharSet(pos: Pos, positive: Boolean, elems: Set[Char]) extends Exp
  /** This class represents an AST of character class [...].
    * @param pos position in source file
    * @param elems the list of element constituting character class. */
  case class CharClass(pos: Pos, positive: Boolean, elems: List[CharClassElement]) extends Exp
  /** This trait represents common super-type of element in character class. */
  sealed trait CharClassElement
  /** An element of character class representing one character. */
  case class OneChar(ch: Char) extends CharClassElement
  /** An element of character class representing characters in this range.
    * @param from start of the range
    * @param to end of the range */
  case class CharRange(from: Char, to: Char) extends CharClassElement

  /**
    * This class represents an AST of Debug(e)
    * @param pos pos position in source file
    * @param body e */
  case class Debug(pos: Pos, body: Exp) extends Exp

  /** This class represents an AST of rule calls.
    * @param pos position in source file
    * @param name the name of identifier */
  case class Call(pos: Pos, name: Symbol, args: List[Exp]) extends Exp

  /** This class represents an AST of identifier.
    * An identifier is used as reference of nonterminal.
    * @param pos position in source file
    * @param name the name of identifier */
  case class Ident(pos: Pos, name: Symbol) extends Exp

  sealed abstract class Type(pos: Pos)
  case class SimpleType(pos: Pos) extends Type(pos)
  case class RuleType(pos: Pos, paramTypes: List[Type], resultType: Type) extends Type(pos)

  case class Fun(pos: Pos, args: List[Symbol], body: Exp) extends Exp
}

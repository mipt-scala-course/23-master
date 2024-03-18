package hw.applicatives

object Parser:
  opaque type P[A] = String => Either[String, (String, A)]

  extension [A](parser: P[A]) def parse(input: String): Either[String, (String, A)] = parser(input)

  def defer[A](p: => P[A]): P[A] = p(_)

  def charWhere(f: Char => Boolean): P[Char] = str =>
    str.headOption.toRight("expected at least one more char symbol").flatMap {
      case x if f(x) => Right((str.tail, x))
      case x         => Left(s"unexpected symbol \"$x\"")
    }

type Parser[A] = Parser.P[A]

import Parser.*

object DoubleCalculator:

  def calculator: Parser[Double] = ???

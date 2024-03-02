// Файл для реализации макросов использующихся в Task.scala
package hw

import scala.quoted.{Quotes, Expr, quotes, ToExpr}
import scala.util.matching.Regex

// Макрос для задания II.1
object SafeRegex:
  inline def apply(inline str: String): Regex =
    ${ SafeRegexMacro.parse('str) }

object SafeRegexMacro:
  def parse(str: Expr[String])(using Quotes): Expr[Regex] = ???

//-----------------------------------------------------------------------------
// Макрос для задания II.2

case class SourceFilePosition(file: String, line: Int)

object SourceFilePosition:
  inline def get: SourceFilePosition = ${ SourceFileMacro.getSourceFilePosition }

object SourceFileMacro:
  def getSourceFilePosition(using qctx: Quotes): Expr[SourceFilePosition] = ???

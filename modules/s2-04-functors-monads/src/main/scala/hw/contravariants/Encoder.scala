package hw.contravariants

import cats.Contravariant
import cats.implicits.toContravariantOps

final case class DegreesFahrenheit(value: Int)

trait Encoder[-T]:
  def apply(value: T): String

object Encoder:
  def encode[T](value: T)(using encoder: Encoder[T]): String =
    encoder(value)

  /** Реализуйте инстанс Contravariant для Encoder
    */
  given Contravariant[Encoder] = ???

object EncoderInstances:

  /** Реализуйте Encoder для Option и произвольного типа, для которого есть Encoder в скоупе. None должен
    * преобразовываться в значение `<none>`
    */
  given [T](using e: Encoder[T]): Encoder[Option[T]] = ???

  /** Реализуйте Encoder для List и произвольного типа, для которого есть Encoder в скоупе. Элементы листа в
    * результирующей строке должны быть разделены запятой.
    */
  given [T: Encoder]: Encoder[List[T]] = ???

  /** Реализуйте encoder для строки
    */
  given Encoder[String] = ???

  /** Реализуйте encoder числа в строку
    */
  given Encoder[Int] = ???

  /** Реализуйте encoder булева значения в строку
    */
  given Encoder[Boolean] = ???

  /** Реализуйте encoder для DegreesFahrenheit через использование существующего encoder и Contravariant
    */
  given Encoder[DegreesFahrenheit] = ???

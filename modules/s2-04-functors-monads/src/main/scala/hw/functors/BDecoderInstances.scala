package hw.functors

import cats.implicits.toBifunctorOps
import hw.functors.Decoder.*

object BDecoderInstances:

  /** Реализуйте декодер для Option и произвольного типа, для которого есть Decoder в скоупе. Если исходная строка -
    * пустая, или имеет значение `<none>` или null, то в результате должен быть None
    */
  given [E, T](using decoder: Decoder[E, T]): Decoder[E, Option[T]] = ???

  /** Реализуйте декодер для List и произвольного типа, для которого есть Decoder в скоупе. Элементы листа в исходной
    * строке разделены запятой.
    */
  given [E, T](using decoder: Decoder[E, T]): Decoder[E, List[T]] = ???

  /** Реализуйте декодер из строки в строку
    */
  given Decoder[DecoderError, String] = ???

  /** Реализуйте декодер из строки в число с заданным типом ошибки, используя Decoder.attempt() и Bifunctor
    */
  given Decoder[NumberFormatDecoderError.type, Int] = ???

  /** Реализуйте декодер из строки в булево значение, используя Decoder.attempt() и Bifunctor
    */
  given Decoder[IllegalArgumentDecoderError.type, Boolean] = ???

  /** Реализуйте декодер для DegreesFahrenheit через использование существующего декодера и Bifunctor
    */
  given Decoder[InvalidDegreesFahrenheitValue.type, DegreesFahrenheit] = ???

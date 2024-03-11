package hw.functors

import cats.implicits.toFunctorOps
import hw.functors.Decoder.Result
import hw.functors.FDecoder.FDecoder
import hw.functors.FDecoder.given_Functor_FDecoder

object FDecoderInstances:
  /** Реализуйте декодер для Option и произвольного типа, для которого есть Decoder в скоупе. Если исходная строка -
    * пустая, или имеет значение `<none>` или null, то в результате должен быть None
    */
  given [T](using decoder: FDecoder[T]): FDecoder[Option[T]] = ???

  /** Реализуйте декодер для List и произвольного типа, для которого есть Decoder в скоупе. Элементы листа в исходной
    * строке разделены запятой.
    */
  given [T: FDecoder]: FDecoder[List[T]] = ???

  /** Реализуйте декодер из строки в строку
    */
  given strDecoder: FDecoder[String] = ???

  /** Реализуйте декодер из строки в число, используя `NumberFormatDecoderError`в результате в случае, если строка - не
    * число
    */
  given intDecoder: FDecoder[Int] = ???

  /** Реализуйте декодер из строки в булево значение, используя ошибку `IllegalArgumentDecoderError` в случае,если
    * строка не парсится в boolean
    */
  given boolDecoder: FDecoder[Boolean] = ???

  /** Реализуйте декодер для DegreesFahrenheit через использование существующего декодера и инстанса Functor. В случае
    * ошибки данный декодер должен возвращать InvalidDegreesFahrenheitValue
    */
  given FDecoder[DegreesFahrenheit] = ???

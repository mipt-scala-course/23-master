package hw.functors

import cats.{Bifunctor, Functor}
import hw.functors.Decoder.Result

import scala.util.Try

sealed trait DecoderError
case object NumberFormatDecoderError      extends DecoderError
case object IllegalArgumentDecoderError   extends DecoderError
case object InvalidDegreesFahrenheitValue extends DecoderError

trait Decoder[+E, +T]:
  def apply(raw: String): Decoder.Result[E, T]

object Decoder:

  type Result[E, T] = Either[E, T]

  def apply[E, T](using decoder: Decoder[E, T]): Decoder[E, T] = decoder

  def attempt[T](unsafe: String => T): Decoder[Throwable, T] =
    (raw: String) => Try(unsafe(raw)).toEither

  def decode[E, T](raw: String)(using decoder: Decoder[E, T]): Decoder.Result[E, T] =
    decoder(raw)

  /** Реализуйте Bifunctor для Decoder, используя Either.left проекцию
    */
  given Bifunctor[Decoder] = ???

object FDecoder:

  type FDecoder[T] = Decoder[DecoderError, T]

  def decode[T](raw: String)(using decoder: FDecoder[T]): Decoder.Result[DecoderError, T] =
    decoder(raw)

  /** Реализуйте Functor для Decoder
    */
  given Functor[FDecoder] = ???

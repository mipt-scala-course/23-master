package hw.functors

import hw.functors.FDecoder.FDecoder
import org.scalatest.Ignore
import org.scalatest.flatspec.{AnyFlatSpec, AnyFlatSpecLike}
import org.scalatest.matchers.should.Matchers

class CommonDecoderTests(using
    Decoder[DecoderError, Int],
    Decoder[DecoderError, Boolean],
    Decoder[DecoderError, String],
    Decoder[DecoderError, DegreesFahrenheit],
    Decoder[DecoderError, Option[Int]],
    Decoder[DecoderError, Option[String]],
    Decoder[DecoderError, List[Int]],
    Decoder[DecoderError, List[Option[Int]]],
    Decoder[DecoderError, List[Option[String]]],
    Decoder[DecoderError, Option[List[Int]]]
) extends AnyFlatSpec
    with Matchers:
  behavior.of("Decoder")

  it should "correctly decode int" in {
    Decoder.decode[DecoderError, Int]("123") shouldBe Right(123)
    Decoder.decode[DecoderError, Int]("aaa1") shouldBe Left(NumberFormatDecoderError)
  }

  it should "correctly decode boolean" in {
    Decoder.decode[DecoderError, Boolean]("true") shouldBe Right(true)
    Decoder.decode[DecoderError, Boolean]("aaa1") shouldBe Left(IllegalArgumentDecoderError)
  }

  it should "correctly decode degrees celsius" in {
    Decoder.decode[DecoderError, DegreesFahrenheit]("1") shouldBe Right(DegreesFahrenheit(1))
    Decoder.decode[DecoderError, DegreesFahrenheit]("aaa") shouldBe Left(InvalidDegreesFahrenheitValue)
  }

  it should "correctly decode option" in {
    Decoder.decode[DecoderError, Option[Int]]("123") shouldBe Right(Some(123))
    Decoder.decode[DecoderError, Option[Int]]("abc") shouldBe Left(NumberFormatDecoderError)
    Decoder.decode[DecoderError, Option[String]]("abc") shouldBe Right(Some("abc"))
    Decoder.decode[DecoderError, Option[Int]]("") shouldBe Right(None)
    Decoder.decode[DecoderError, Option[Int]](null) shouldBe Right(None)
    Decoder.decode[DecoderError, Option[Int]]("<none>") shouldBe Right(None)
  }

  it should "correctly decode list" in {
    Decoder.decode[DecoderError, List[Int]]("123, 321, 333") shouldBe Right(List(123, 321, 333))
    Decoder.decode[DecoderError, List[Int]]("abc") shouldBe Left(NumberFormatDecoderError)
    Decoder.decode[DecoderError, List[Int]]("") shouldBe Right(List.empty)
    Decoder.decode[DecoderError, List[Option[Int]]]("1, 2, 3, <none>") shouldBe Right(
      List(Some(1), Some(2), Some(3), None)
    )
    Decoder.decode[DecoderError, List[Option[String]]]("abc, 111, <none>") shouldBe Right(
      List(Some("abc"), Some("111"), None)
    )
    Decoder.decode[DecoderError, Option[List[Int]]]("1, 2, 3") shouldBe Right(Some(List(1, 2, 3)))
  }

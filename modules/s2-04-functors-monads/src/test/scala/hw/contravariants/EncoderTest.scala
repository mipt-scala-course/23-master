package hw.contravariants

import hw.contravariants.Encoder
import org.scalatest.Inside
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EncoderTest extends AnyFlatSpec with Matchers with Inside:

  import hw.contravariants.EncoderInstances.given

  behavior.of("Encoder")

  it should "correctly encode int" in {
    Encoder.encode(123) shouldBe "123"
  }

  it should "correctly encode boolean" in {
    Encoder.encode(true) shouldBe "true"
  }

  it should "correctly encode DegreesFahrenheit" in {
    Encoder.encode(DegreesFahrenheit(451)) shouldBe "451"
  }

  it should "correctly encode option" in {
    Encoder.encode(Some(123)) shouldBe "123"
    Encoder.encode(Some("abc")) shouldBe "abc"
    Encoder.encode[Option[String]](None) shouldBe "<none>"
  }

  it should "correctly encode list" in {
    Encoder.encode(List(123, 321, 333)) shouldBe "123,321,333"
    Encoder.encode(List.empty[Int]) shouldBe ""
    Encoder.encode[List[Option[String]]](List(None)) shouldBe "<none>"
    Encoder.encode(List(Some(123), Some(321), None)) shouldBe "123,321,<none>"
  }

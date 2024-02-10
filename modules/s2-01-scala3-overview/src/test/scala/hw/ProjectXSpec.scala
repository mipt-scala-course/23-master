package hw

import io.circe.{Encoder, Json}
import io.circe.syntax.*

class ProjectXSpec extends munit.FunSuite {
  test("I. encoding") {
    import ProjectX.*

    val result = encode(
      SmthCanBeEncoded(42) ::
        SmthCanBeEncoded("lol") ::
        SmthCanBeEncoded(27 :: 1828 :: 1828 :: Nil) :: {
          implicit val enc: Encoder[String] =
            (x: String) => Json.fromString(x.map(_ => '*'))

          SmthCanBeEncoded("kek")
        } :: Nil
    )

    val expected = Json.arr(
      42.asJson,
      "lol".asJson,
      (27 :: 1828 :: 1828 :: Nil).asJson,
      "***".asJson
    )

    assertEquals(result, expected)
  }
}

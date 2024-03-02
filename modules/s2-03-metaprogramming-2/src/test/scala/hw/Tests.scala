package hw

import io.circe.Json
import io.circe.syntax.*
import io.circe.parser.parse

class Tests extends munit.FunSuite:
  test("I.1 toInt (value level)"):
    val x1: 0 = toInt[`0`]
    val x2: 3 = toInt[`3`]
    val x3: 10 = toInt[`10`]

    val badCode =
      "val x: 5 = toInt[`4`]"
    assertNoDiff(
      compileErrors(badCode),
      """|error:
         |Found:    (4 : Int)
         |Required: (5 : Int)
         |
         |The following import might make progress towards fixing the problem:
         |
         |  import munit.Clue.generate
         |
         |val x: 5 = toInt[`4`]
         |               ^
         |""".stripMargin
    )

  test("I.2 ToInt (type level)"):
    summon[ToInt[`1`] =:= 1]
    summon[ToInt[`4`] =:= 4]

    val badCode =
      "summon[ToInt[`4`] =:= 5]"
    assertNoDiff(
      compileErrors(badCode),
      """|error: Cannot prove that (4 : Int) =:= (5 : Int).
         |summon[ToInt[`4`] =:= 5]
         |                       ^
         |""".stripMargin
    )

  test("I.3 sum type"):
    summon[`2` + `2` =:= `4`]
    summon[`1` + `2` =:= `2` + `1`]
    summon[(`7` + `1`) + `10` =:= `7` + (`1` + `10`)]

    val badCode =
      "summon[ToInt[`1` + `8`] =:= 7]"
    assertNoDiff(
      compileErrors(badCode),
      """|error: Cannot prove that (9 : Int) =:= (7 : Int).
         |summon[ToInt[`1` + `8`] =:= 7]
         |                             ^
         |""".stripMargin
    )

  test("I.4 eq type"):
    summon[`2` + `2` == `4` =:= true]
    summon[(`7` + `1`) + `10` == `7` + (`1` + `10`) =:= true]
    summon[`2` + `1` == `4` =:= false]
    summon[`1` + `1` == `1` + `1` + `1` =:= false]

    val badCode =
      "summon[`1` + `1` == `1` + `1` + `1` =:= true]"
    assertNoDiff(
      compileErrors(badCode),
      """|error: Cannot prove that (false : Boolean) =:= (true : Boolean).
         |summon[`1` + `1` == `1` + `1` + `1` =:= true]
         |                                            ^
         |""".stripMargin
    )

  test("I.5 minus type"):
    summon[`2` - `2` == `0` =:= true]
    summon[(`7` + `1`) - `5` == `2` + `1` =:= true]
    summon[`3` - `2` == `4` + `1` =:= false]
    summon[`1` - `2` =:= Null]

    val badCode =
      "summon[`1` + `1` - `2` =:= Null]"
    assertNoDiff(
      compileErrors(badCode),
      """|error: Cannot prove that hw.Zero =:= Null.
         |summon[`1` + `1` - `2` =:= Null]
         |                               ^
         |""".stripMargin
    )

  test("I.6 mul type"):
    summon[`2` * `2` == `4` =:= true]
    summon[`2` * (`2` + `3`) == `2` * `2` + `2` * `3` =:= true]
    summon[`2` * (`5` - `1`) == `4` * `2` =:= true]
    summon[`10` == `4` * `2` + `1` =:= false]
    summon[`2` * `2` - `5` =:= Null]

    val badCode =
      "summon[`1` * `1` * `1` =:= `2`]"
    assertNoDiff(
      compileErrors(badCode),
      """|error: Cannot prove that hw.Succ[hw.Zero] =:= hw.2.
         |summon[`1` * `1` * `1` =:= `2`]
         |                              ^
         |""".stripMargin
    )

  test("II.1 regex macro"):
    assert(SafeRegex("[a-zA-Z][a-zA-Z0-9_]*").matches("foo"))
    assert(!SafeRegex(".+").matches(""))

    assert(compileErrors("""SafeRegex("[1 - 9] \\")""").nonEmpty)
    assert(compileErrors("""SafeRegex("*")""").nonEmpty)
    assert(compileErrors("""SafeRegex(".* \*")""").nonEmpty)

  def testOut[A](print: => Unit, predicate: String => Boolean): Unit =
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream)(print)
    val res = stream.toString
    stream.close()
    println(res)
    assert(predicate(res.stripTrailing), res)

  test("II.2 log method"):
    case class User(name: String, age: Int)
    given Loggable[User] with
      override def jsonLog(a: User): Json =
        Json.obj("name" -> a.name.asJson, "age" -> a.age.asJson)

    val user = User("Vasiliy", 42)
    import Loggable.*

    testOut(user.log("user signed in"), str =>
      parse(str).flatMap(_.hcursor.downField("position").as[String])
        .contains("Tests.scala:133")
    )
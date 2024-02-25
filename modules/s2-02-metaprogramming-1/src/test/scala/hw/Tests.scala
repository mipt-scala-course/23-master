package hw

import io.circe.Json
import io.circe.syntax.*
import scala.compiletime.summonInline
import scala.deriving.Mirror

class Tests extends munit.FunSuite:
  test("I.1 Standard instances"):
    val testString = "Test String"
    val resStr = summon[Loggable[String]].jsonLog(testString)
    assertEquals(resStr, Json.fromString(testString), resStr)

    val testBool = false
    val resBool = summon[Loggable[Boolean]].jsonLog(testBool)
    assertEquals(resBool, Json.fromBoolean(testBool), resBool)

    val testInt = 42
    val resInt = summon[Loggable[Int]].jsonLog(testInt)
    assertEquals(resInt, Json.fromInt(testInt), resInt)

    val testList = List("lol", "kek", "cheb")
    val resList = summon[Loggable[List[String]]].jsonLog(testList)
    assertEquals(resList, Json.arr("lol".asJson, "kek".asJson, "cheb".asJson), resList)

    val testOption = Some(true)
    val resOption = summon[Loggable[Option[Boolean]]].jsonLog(testOption)
    assertEquals(resOption, true.asJson, resOption)

    val resOptionEmpty = summon[Loggable[Option[Boolean]]].jsonLog(None)
    assertEquals(resOptionEmpty, Json.Null, resOptionEmpty)

  test("I.2 summonLabels"):
    val labels = Loggable.summonLabels[("lol", "kek", "Cheb")]
    assertEquals(labels, List("lol", "kek", "Cheb"), labels)

    val labelsSingle = Loggable.summonLabels["foo" *: EmptyTuple]
    assertEquals(labelsSingle, List("foo"), labelsSingle)

    val labelsEmpty = Loggable.summonLabels[EmptyTuple]
    assertEquals(labelsEmpty, List(), labelsEmpty)

  test("I.3 summonInst"):
    case class Foo(x: Int)
    object Foo:
      given Loggable[Foo] with
        override def jsonLog(a: Foo): Json = Json.Null

    val insts = Loggable.summonInst[(String, Int, Foo)]
    assertEquals(insts, List(summon[Loggable[String]], summon[Loggable[Int]], Foo.given_Loggable_Foo), insts)

  test("I.4 logProduct"):
    case class Boo(x: String, y: Int, z: List[String])
    val inst = Loggable.logProduct[Boo](
      summonInline[Mirror.ProductOf[Boo]],
      List("x" -> summon[Loggable[String]], "y" -> summon[Loggable[Int]], "z" -> summon[Loggable[List[String]]])
    )

    val result = inst.jsonLog(Boo("x", 42, List("lol", "kek")))
    val expected = Json.obj(
      "x" -> Json.fromString("x"),
      "y" -> Json.fromInt(42),
      "z" -> Json.arr("lol".asJson, "kek".asJson)
    )
    assertEquals(result, expected, result)

  test("I.5 derivation for records"):
    case class Token(token: String, hash: Int)
    object Token:
      given Loggable[Token] with
        override def jsonLog(a: Token): Json = Json.obj(
          "token" -> a.token.map(_ => '*').asJson,
          "hash" -> a.hash.asJson,
        )
    case class User(name: String, tokens: List[Token]) derives Loggable

    val result = summon[Loggable[User]].jsonLog(User("Vasiliy", List(Token("private_key", 42), Token("public_key", 13))))
    val expected = Json.obj(
      "name" -> "Vasiliy".asJson,
      "tokens" -> Json.arr(
        Json.obj(
          "token" -> "***********".asJson,
          "hash" -> 42.asJson
        ),
        Json.obj(
          "token" -> "**********".asJson,
          "hash" -> 13.asJson
        )
      )
    )
    assertEquals(result, expected, result)

//  test("II.1 logSum"):
//    sealed trait Koo
//    case class KooI(x: Int) extends Koo derives Loggable
//    case class KooB(y: Boolean) extends Koo derives Loggable
//
//    val inst = Loggable.logSum[Koo](
//      summonInline[Mirror.SumOf[Koo]],
//      List("KooI" -> summon[Loggable[KooI]], "KooB" -> summon[Loggable[KooB]])
//    )
//
//    val result = inst.jsonLog(KooB(false))
//    val expected = Json.obj(
//      "KooB" -> Json.obj("y" -> false.asJson)
//    )
//
//    assertEquals(result, expected, result)
//
//  test("II.2 derive sealed trait"):
//    sealed trait Koo derives Loggable
//    case class KooI(x: Int) extends Koo
//    object KooI:
//      given Loggable[KooI] with
//        override def jsonLog(a: KooI): Json = a.x.asJson
//
//    case class KooB(y: Boolean, z: List[String]) extends Koo derives Loggable
//
//    val result = summon[Loggable[Koo]].jsonLog(KooI(42))
//    val expected = Json.obj(
//      "KooI" -> 42.asJson
//    )
//
//    assertEquals(result, expected, result)
//
//  test("II.3 summonChild"):
//    enum Koo:
//      case KooI(x: Int)
//      case KooB(y: Boolean)
//
//    object Koo:
//      given Loggable[KooB] with
//        override def jsonLog(a: Koo.KooB): Json = Json.Null
//
//    val result1 = Loggable.summonChild[Koo.KooI, Koo].jsonLog(Koo.KooI(42))
//    val expected1 = Json.obj("x" -> 42.asJson)
//    assertEquals(result1, expected1, result1)
//
//    val result2 = Loggable.summonChild[Koo.KooB, Koo].jsonLog(Koo.KooB(true))
//    assertEquals(result2, Json.Null, result2)
//
//
//  test("II.4 derive sum"):
//    enum Koo derives Loggable:
//      case KooI(x: Int)
//      case KooB(y: Boolean)
//
//    object Koo:
//      given Loggable[KooB] with
//        override def jsonLog(a: Koo.KooB): Json = Json.Null
//
//    val result1 = summon[Loggable[Koo]].jsonLog(Koo.KooI(42))
//    val expected1 = Json.obj("KooI" -> Json.obj("x" -> 42.asJson))
//    assertEquals(result1, expected1, result1)
//
//    val result2 = summon[Loggable[Koo]].jsonLog(Koo.KooB(true))
//    assertEquals(result2, Json.obj("KooB" -> Json.Null), result2)
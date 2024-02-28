package hw

import java.time.LocalDate
import scala.jdk.CollectionConverters.*
import io.circe.Encoder

class LoggableSpec extends munit.FunSuite:
  extension (x: String)
    def toName: Name   = Name(x).getOrElse(throw new Exception(s"expected name `$x` is ok"))
    def toLogin: Login = Login(x).getOrElse(throw new Exception(s"expected login `$x` is ok"))

  test("II. 1 Loggable contramap and string instance"):
    val testString = "lol kek"
    val jsonTest   = summon[Loggable[String]].jsonLog(testString)
    assertEquals(jsonTest.toString, "\"lol kek\"", jsonTest)

    val dateLoggable: Loggable[LocalDate] = summon[Loggable[String]].contramap(_.toString)
    val testDate                          = LocalDate.parse("2023-04-02")
    val json                              = dateLoggable.jsonLog(testDate)
    assertEquals(json.toString, "\"2023-04-02\"", testDate)

  test("II. 2 Login new type"):
    val loginStartsWithNumber = Login("1foo")
    assertEquals(loginStartsWithNumber.isLeft, true, loginStartsWithNumber)

    val loginStartsWithUnderscore = Login("_foo")
    assertEquals(loginStartsWithUnderscore.isLeft, true, loginStartsWithUnderscore)

    val login = Login("cheburek")
    assertEquals(login: Either[String, String], Right("cheburek"), login)

  test("II. 2 Name new type"):
    val nameStartsWithSpace = Name(" Vasiliy")
    assertEquals(nameStartsWithSpace.isLeft, true, nameStartsWithSpace)

    val nameEmpty = Name("")
    assertEquals(nameEmpty.isLeft, true, nameEmpty)

    val name = Name("Vasiliy")
    assertEquals(name: Either[String, String], Right("Vasiliy"), name)

  def testJson[A: Loggable](a: A, expected: String): Unit =
    val json = summon[Loggable[A]].jsonLog(a)
    assertEquals(json.toString, expected, json)

  test("II. 2 Loggable for Name"):
    testJson("Vasiliy".toName, s"\"Vasil**\"")
    testJson("Siniy".toName, s"\"Siniy\"")
    testJson("Abrakadabra".toName, s"\"Abrak******\"")

  val sampleAccessToken =
    "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6InRtRG5YaUNldzlvS3hSc2gyZTNNdTVBNFBoNCIsImtpZCI6InRtRG5YaUNldzlvS3hSc2gyZTNNdTVBNFBoNCJ9.eyJhdWQiOiJtaWNyb3NvZnQ6aWRlbnRpdHlzZXJ2ZXI6MDY3ZTMxYjgtNWNkYi00ZmY3LTg5MmMtMTVjZWI4OGU3MDY4IiwiaXNzIjoiaHR0cDovL2ZzLnRpbmtvZmYucnUvYWRmcy9zZXJ2aWNlcy90cnVzdCIsImlhdCI6MTY5MzkyNTkyMiwibmJmIjoxNjkzOTI1OTIyLCJleHAiOjE2OTM5Mjk1MjIsImFjY291bnQiOiJzdmNfZHdoX2hlYWxlciIsImRpc3BsYXlOYW1lIjoic3ZjX2R3aF9oZWFsZXIiLCJ1c2VySWQiOiJlNXZJc3g5VFZrNmx1bnhSWVJ4ckhBPT0iLCJhcHB0eXBlIjoiUHVibGljIiwiYXBwaWQiOiIwNjdlMzFiOC01Y2RiLTRmZjctODkyYy0xNWNlYjg4ZTcwNjgiLCJhdXRobWV0aG9kIjoidXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmFjOmNsYXNzZXM6UGFzc3dvcmRQcm90ZWN0ZWRUcmFuc3BvcnQiLCJhdXRoX3RpbWUiOiIyMDIzLTA5LTA1VDE0OjU4OjQyLjA2NloiLCJ2ZXIiOiIxLjAifQ.mUj0qyMbFOFON2npD9bXWXQITe51D7ALoym3QQDJVHBXttwcO-0qKDg6XXpAC0qQsOE1M9ZACS45ZF8H2h0NrviY6MgCPgaU6FkX1IkVeizqiiO2Uj0Q6_nQFAO3TT5r8mhby04CYy0InoPlnK36Yu1ha9G2-mzKHwH-qS35q8JB9ROq0v44rtf0dV3QigjmSYkcuEAZYae1sD-n3sDEFJjG1QAPbKvUTmD3SLOnb5ELbzz9NL2j23EdH_Qmo1SmBayl1P2-e_KIkk5Yim5ThfzybcSmKbf7gsTl5ndizWjL2W1-6fDrl_Rq2dp56V8plsUzioir8DqpRzgZTe08xg"
  val sampleJwt = JwtToken(sampleAccessToken, 1693929522L)
  val loggedSampleJwt =
    s"""|{
        |  "token" : "***",
        |  "exp" : ${sampleJwt.exp}
        |}""".stripMargin

  test("II. 3 Loggable Jwt Token instance"):
    testJson(sampleJwt, loggedSampleJwt)

  def loggedTokenFmtd(prefix: String): String =
    List.from(loggedSampleJwt.lines().iterator().asScala) match
      case h :: t => (h :: t.map(prefix + _)).mkString("\n")
      case Nil    => ""

  test("II. 4 Loggable for User"):
    val login = "awes_ome_1".toLogin
    val name  = "Awesome".toName
    val expected =
      s"""|{
          |  "login" : "$login",
          |  "name" : "Aweso**",
          |  "token" : ${loggedTokenFmtd("  ")}
          |}""".stripMargin
    testJson(User(login, name, sampleJwt), expected)

    val login2 = "super_mega_chill".toLogin
    val name2  = "Chillovek Molekula".toName
    val expected2 =
      s"""|{
          |  "login" : "$login2",
          |  "name" : "Chill*************",
          |  "token" : ${loggedTokenFmtd("  ")}
          |}""".stripMargin
    testJson(User(login2, name2, sampleJwt), expected2)

  def testOut[A](print: => Unit, endsWith: String): Unit =
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream)(print)
    val res = stream.toString
    stream.close()
    println(endsWith)
    println(res.stripTrailing)
    assertEquals(res.stripTrailing.endsWith(endsWith), true, res)

  test("II. 5 log method"):
    val name = "Awesome".toName
    testOut(name.jsonLog("awesome name"), "\"message\":\"awesome name\",\"context\":\"Aweso**\"}")

    val login = "awes_ome_1".toLogin
    val user  = User(login, name, sampleJwt)
    testOut(
      user.jsonLog("awesome user just signed in"),
      "\"message\":\"awesome user just signed in\",\"context\":{\"login\":\"awes_ome_1\",\"name\":\"Aweso**\",\"token\":{\"token\":\"***\",\"exp\":1693929522}}}"
    )

  test("II. 6 sensitive type class"):
    val code =
      "case class ThereIsNoSensitiveInfo(int: Int, str: String)\nsummon[Sensitive[ThereIsNoSensitiveInfo]]"
    assertNoDiff(
      compileErrors(code),
      """|error: No given instance of type hw.Sensitive[ThereIsNoSensitiveInfo] was found for parameter x of method summon in object Predef
         |summon[Sensitive[ThereIsNoSensitiveInfo]]
         |                                        ^
         |""".stripMargin
    )

  test("II. 7 non-sensitive loggable instances"):
    import Loggable.given

    case class ThereIsNoSensitiveInfo(int: Int, str: String) derives Encoder.AsObject
    val data = ThereIsNoSensitiveInfo(42, "lol")
    testOut(data.jsonLog("data"), "\"message\":\"data\",\"context\":{\"int\":42,\"str\":\"lol\"}}")
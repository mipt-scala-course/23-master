package hw

import io.circe.{Encoder, Json}
import io.circe.syntax._
import scala.util.NotGiven

/** II. Знакомимся с новыми ключевыми словами из 3ей скалы: opaque, extension, given, using
 *
 * Loggable[A] - класс типов, описывающий как логировать в виде Json значения типа A. Данные с чувствительной
 * информацией должны маскироваться.
 *
 * 1) Реализовать метод contramap в классе типов Loggable. Добавить инстанс Loggable[String]
 *
 * 2) Реализовать "новые типы" Login и Name. Логин должен содержать латинские буквы (маленькие/большие), цифры и нижнее
 * подчеркивание. Логин не может начинаться c нижнего подчеркивания или цифры.
 *
 * Имя должно быть непустым и не может начинаться с пробела. Реализовать инстанс Loggable для Name, который маскирует
 * все символы имени после 6-го включительно.
 *
 * 3) Реализовать инстанс Loggable для JwtToken. Поле token - чувствительная инфлормация, поле token должно
 * логироваться как "***"
 *
 * 4) Используя Loggable[JwtToken], Loggable[Name] реализовать инстанс Loggable[User]
 *
 * 5) Добавить метод log, который будет логировать модель в соответствии с ее инстансом Loggable, добавляя поля message
 * и timestamp
 *
 * val user: User = ...
 * user.log("user signed in")
 *
 * должно печатать в формате: {"timestamp":"2023-09-06T12:33:10.784230","message":"user signed
 * in","context":{"login":"vasya","name":"Vasil**","token":{"token":"***","exp":1693929522}}}
 *
 * timestamp - текущее время в поле
 * context - информации о пользователе
 *
 * 6) Добавить класс типов Sensitive[A] без методов.
 *  Нужен только для того, чтобы отмечать, что данные (тип A) содержат чувствительную информацию.
 *  Используя синтаксис derives (реализуя необходимый для этого метод derived) "пометить"
 *  данные JwtToken, User, Name как чувствительные.
 *
 * 7) Написать универсальный инстанс Loggable, который будет выводить Loggable для любой модели, НЕ содержащей
 * чувствительную информацию, на основе инстанса Encoder.
 * Чтобы такой код работал без дополнительных импортов:
 *
 * import Loggable.given // или import Loggable.* в зависимости от вашей реализации log
 *
 * case class Custom(foo: String, bar: Int) derives Encoder.AsObject
 * Custom("foo", 42).log("custom event")
 */
trait Loggable[A]:
  def jsonLog(a: A): Json

  def contramap[B](func: B => A): Loggable[B] = (b: B) => jsonLog(func(b))

  given Loggable[String] with
  def jsonLog(a: String): Json = Json.fromString(a)

  type Login = Login.T
  object Login:
  opaque type T <: String = String
  def apply(s: String): Either[String, Login] =
  Either.cond(s.matches("^[a-zA-Z][a-zA-Z0-9_]*$"), s, s"Invalid login $s")

  given Loggable[Login] with
  def jsonLog(login: Login): Json = Json.fromString(login)

  type Name = Name.T
  object Name:
  opaque type T <: String = String
  def apply(s: String): Either[String, Name] =
  Either.cond(!s.isBlank && !s.startsWith(" "), s, s"Incorrect name $s")

  given Loggable[Name] with
  def jsonLog(name: Name): Json =
  if name.length < 5 then
  val log = Json.fromString (name)
  log
  else
  val log = Json.fromString(s"${name.substring(0, 5)}${"*" * (name.length - 5)}")
  log

  case class JwtToken(token: String, exp: Long)

  given Loggable[JwtToken] with
  def jsonLog(token: JwtToken): Json = Json.obj(
  "token" -> "***".asJson,
  "exp"   -> token.exp.asJson
  )

  case class User(login: Login, name: Name, token: JwtToken)

  given Loggable[User] with
  def jsonLog(user: User): Json =
  val log =
  Json.obj(
  "name"  -> summon[Loggable[Name]].jsonLog(user.name),
  "login" -> summon[Loggable[Login]].jsonLog(user.login),
  "token" -> summon[Loggable[JwtToken]].jsonLog(user.token)
  )
  log

  extension [A](a: A)(using loggable: Loggable[A])
  def jsonLog(message: String): Unit =
  val timeStamp = java.time.LocalDateTime.now()
  return println(s"""{"timestamp":"$timeStamp","message":"$message","context":${loggable.jsonLog(a).noSpaces}}""")

  trait Sensitive[A]
  given Sensitive[JwtToken] with {}
  given Sensitive[User] with     {}
  given Sensitive[Name] with     {}

  object Loggable:
  given [A](using encoder: Encoder[A], notGiven: NotGiven[Sensitive[A]]): Loggable[A] with
  def jsonLog(a: A): Json = a.asJson

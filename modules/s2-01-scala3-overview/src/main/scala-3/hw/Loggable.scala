package hw

import io.circe.Json

/** II. Знакомимся с новыми ключевыми словами из 3ей скалы: opaque, extension, given, using
  *
  * Loggable[A] - класс типов, описывающий как логировать в виде Json значения типа A. Данные с чувствительной
  * информацией должны маскироваться.
  *
  * 1) Реализовать метод contramap в классе типов Loggable Добавить инстанс Loggable[String]
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
  * val user: User = ... user.log("user signed in")
  *
  * должно распечатать: {"timestamp":"2023-09-06T12:33:10.784230","message":"user signed
  * in","context":{"login":"vasya","name":"Vasil**","token":{"token":"***","exp":1693929522}}}
  *
  * timestamp - текущее время в поле context - информации о пользователе
  *
  * 6) Добавить класс типов Sensitive[A] без методов Нужен только для того, чтобы отмечать, что данные (тип A) содержат
  * чувствительную информацию Используя синтаксис derives (реализуя необходимый для этого метод derived) "пометить"
  * данные JwtToken, User, Name как чувствительные
  *
  * 7) Написать универсальный инстанс Loggable, который будет выводить Loggable для любой модели, НЕ содержащей
  * чувствительную информацию, на основе инстанса Encoder Чтобы такой код работал без дополнительных инстансов:
  *
  * import Loggable.given // или import Loggable.* в зависимости от вашей реализации log
  *
  * case class Custom(foo: String, bar: Int) derives Encoder.AsObject Custom("foo", 42).log("custom event")
  */
trait Loggable[A]:
  def jsonLog(a: A): Json

  def contramap[B](f: B => A): Loggable[B] = ???

case class JwtToken(token: String, exp: Long)

type Login // = ???

type Name // = ???

case class User(
    login: Login,
    name: Name,
    token: JwtToken
)

package hw

import io.circe.Json
import scala.deriving.Mirror
import scala.compiletime.{erasedValue, summonFrom, summonInline}

/**
 *  Вывод Loggable.
 *  Цель этого задания - реализовать автоматическую деривацию инстансов Loggable для
 *    алгебраических типов данных.
 */
trait Loggable[A]:
  def jsonLog(a: A): Json

object Loggable extends Instances, Derivation:
  inline def derived[T](using m: Mirror.Of[T]): Loggable[T] = derive

/**
 *  I. Вывод Loggable для рекордов
 *
 *    1.Реализовать в трейте Instances инстансы Loggable для стандартных типов данных:
 *      - Int, Boolean, String
 *
 *      Реализовать вывод инстансов Loggable
 *      - для типа Option[A] (имея в контексте Loggable[A])
 *        кейс None в json печатать в виде null
 *        кейс Some(x) в json печатать как залоггированное значение x
 *
 *      - для типа List[A] (имея в контексте Loggable[A])
 */
trait Instances

trait Derivation:
  /**
   *    2.Реализовать метод `summonLabels[T <: Tuple]: List[String]`, которому в качестве тайп-аргумента T
   *      передается кортеж строк-синглтонов, а на выходе список этих строк.
   *
   *        summonLabels[("lol", "kek")] // List("lol", "kek")
   */
  inline def summonLabels[T <: Tuple]: List[String] = ???

  /**
   *     3.Реализовать метод `summonInst[T <: Tuple]: List[Loggable[?]]`, кoторому в качестве тайп-аргумента T
   *       передается кортеж типов, а на выходе список инстансов Loggable[?] для этих типов, сохраняя порядок.
   *       Для каждого типа из кортежа должен быть в скоупе инстанс Loggable, иначе дожна быть брошена ошибка компиляции.
   *
   *         case class Foo(x: Int)
   *         object Foo:
   *           given Loggable[Foo] with
   *             def jsonLog(a: A): Json = Json.Null
   *
   *         summonInst[(String, Foo, Boolean)] // = List(Loggable.given_Loggable_String, Foo.given_Loggable_Foo, Loggable.given_Loggable_Boolean)
   *
   *         case class Bar()
   *         summonInst[(Bar, String)] // not compiles
   */
  inline def summonInst[T <: Tuple]: List[Loggable[?]] = ???

  /**
   *    4. Реализовать метод
   *         `def logProduct[T](p: Mirror.ProductOf[T], vs: => List[(String, Loggable[?])]): Loggable[T]`,
   *       которому на вход передается тип Т, являющийся рекордом (аргумент p: Mirror.ProductOf[T])
   *       и аргумент vs - список пар из имени поля рекорда, инстанса Loggable для типа этого поля,
   *       на выходе инстанс Loggable для типа T
   *
   *         case class Boo(x: String, y: Int)
   *          val inst = Loggable.logProduct[Boo](
   *           summonInline[Mirror.ProductOf[Boo]],
   *           List("x" -> summon[Loggable[String]], "y" -> summon[Loggable[Int]])
   *         )
   *
   *         println(inst.jsonLog(Boo("x", 42))) // prints {"x" : "x", "y" : 42 }
   */
  def logProduct[T](p: Mirror.ProductOf[T], vs: => List[(String, Loggable[?])]): Loggable[T] = ???

  /**
   *    5.Используя полученные методы реализовать метод
   *        `inline def derive[T](using m: Mirror.Of[T]): Loggable[T]`
   *      который выводит инстансы `Loggable` только для рекордов
   *
   *        case class Boo(x: String, y: Int) derives Loggable
   *
   *        println(inst.jsonLog(Boo("x", 42))) // prints {"x" : "x", "y" : 42 }
   */
  inline def derive[T](using m: Mirror.Of[T]): Loggable[T] = ???

  /**  II. (*) Вывод Loggable для типов сумм
   *
   *    1.Реализовать метод
   *         `def logSum[T](s: Mirror.SumOf[T], vs: => List[(String, Loggable[?])]): Loggable[T]`
   *      которому на вход передается тип Т, являющийся типом-суммой (аргумент s: Mirror.SumOf[T])
   *      и аргумент vs - список пар из имен вариантов T (детей) и инстансов Loggable для них,
   *      на выходе инстанс Loggable для типа T.
   *      Итоговый json должен быть объектом с одним полем - именем соответствующего варианта, значение которого
   *      будет залогированно в соответствии с инстансом этого варианта значения.
   *
   *        sealed trait Koo
   *        case class KooI(x: Int) extends Koo derives Loggable // имя варианта KooI
   *        case class KooB(y: Boolean) extends Koo derives Loggable // имя варианта KooB
   *
   *        val inst = Loggable.logSum[Koo](
   *          summonInline[Mirror.SumOf[Koo]],
   *          List("KooI" -> summon[Loggable[KooI]], "KooB" -> summon[Loggable[KooB]])
   *        )
   *
   *        println(inst.jsonLog(KooB(false))) // prints { "KooB" : { "y" : false } }
   */
  def logSum[T](s: Mirror.SumOf[T], vs: => List[(String, Loggable[?])]): Loggable[T] = ???

  /**
   *    2.Поправить метод `derive`, чтобы вывод работал для типов-рекордов и для типов сумм,
   *      рализованных через sealed trait:
   *
   *        sealed trait Koo derives Loggable
   *        case class KooI(x: Int) extends Koo derives Loggable
   *        case class KooB(y: Boolean) extends Koo derives Loggable
   *
   *        println(summon[Loggable[Koo]].jsonLog(KooB(false))) // prints { "KooB" : { "y" : false } }
   *
   *      Работает ли при этом вывод для типов-сумм, реализованных через enum?
   *
   *        enum Koo derives Loggable:
   *          case KooI(x: Int)
   *          case KooB(y: Boolean)
   */

  /**
   *    3.Реализовать вспомогательный метод
   *        `inline def summonChild[C, P]: Loggable[C]`
   *      на входе которого тип P - тип-cумма
   *      тип C - вариант (и подтип) типа P
   *      на выходе инстанс Loggable для типа С
   *      Если в скуопе есть инстанс Loggable для С - вернуть его
   *      Иначе вывести Loggable для типа С с помощью метода derived и вернуть его
   *
   *        enum Koo:
   *          case KooI(x: Int)
   *          case KooB(y: Boolean)
   *
   *        object Koo:
   *          given Loggable[KooB] with
   *            override def jsonLog(a: Koo.KooB): Json = Json.Null
   *
   *        println(Loggable.summonChild[Koo.KooI, Koo].jsonLog(Koo.KooI(42)))   // prints { "x": 42 }
   *        println(Loggable.summonChild[Koo.KooB, Koo].jsonLog(Koo.KooB(true))) // prints null
   *
   *      Для реализации пригодится метод compiletime.summonFrom
   */
  inline def summonChild[C, P]: Loggable[C] = ???

  /**
   *    4. Исправить метод derive, чтобы деривация работала также для типов сумм, выраженных через enum.
   *       Для этого может пригодиться вспомогательный метод
   *         `inline def summonInstAuto[T <: Tuple, P]: List[Loggable[?]]`
   *       похожий на summonInst, но который выводит инстансы через метод summonChild
   */
  inline def summonInstAuto[T <: Tuple, P]: List[Loggable[?]] = ???

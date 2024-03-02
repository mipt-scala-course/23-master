package hw

import io.circe.Json

/**
 * I. Вычисления на уровне типов
 *
 *   В этом задании предлагается реализовать операции над типом данных,
 *   представляющим натуральные числа (включая 0) через числа Пеано на уровне типов.
 *
 */
sealed trait Nat

sealed trait Zero extends Nat

sealed trait Succ[x <: Nat] extends Nat

type `0` = Zero
type `1` = Succ[`0`]
type `2` = Succ[`1`]
type `3` = Succ[`2`]
type `4` = Succ[`3`]
type `5` = Succ[`4`]
type `6` = Succ[`5`]
type `7` = Succ[`6`]
type `8` = Succ[`7`]
type `9` = Succ[`8`]
type `10` = Succ[`9`]

/**
 * II.1) Реализовать метод toInt, который на входе в качестве типа A берет натуральное число в кодировке Пеано,
 *       а на выходе возвращает Int
 *
 *         val x: 5 = toInt[`5`] // compiles
 *         val y: 3 = toInt[`2`] // not compiles
 *
 */
transparent inline def toInt[A <: Nat]: Int = ???

/**
 * II.2) Реализовать тип ToInt[X], который на входе в качестве типа A берет натуральное число в кодировке Пеано,
 *       а на выходе возвращает числовой тип литерал
 *
 *         summon[ToInt[`3`] =:= 3] // compiles
 *         summon[ToInt[`2`] =:= 4] // not compiles
 *
 */
type ToInt[X <: Nat] <: Int //= ???

/**
 * II.3) Реализовать тип +[X , Y], который складывает натуральные числа
 *
 *         summon[`3` + `2` =:= 5] // compiles
 *         summon[`1` + `2` + `2` =:= 5] // compiles
 *         summon[`1` + `1` =:= 1] // not compiles
 *
 */
type +[X <: Nat, Y <: Nat] <: Nat //= ???

/**
 * II.4) Реализовать тип ==[X , Y], который проверяет что натуральные числа равны
 *
 *         summon[`3` == `1` + `2` =:= true] // compiles
 *         summon[`2` + `1` == `1` + `2` =:= true] // compiles
 *         summon[`2` == `1` =:= false] // compiles
 *         summon[`2` == `1` =:= true] // not compiles
 *
 */
type ==[X <: Nat, Y <: Nat] <: Boolean //= ???

/**
 * II.5) Реализовать тип -[X, Y], который вычитает натуральные числа
 *       Если X > Y, то на выходе должен быть тип Null
 *
 *         summon[`3` - `1` - `2` =:= `0`] // compiles
 *         summon[`3` - `5` =:= Null] // compiles
 *         summon[`3` - `5` =:= `1`] // not compiles
 *
 */
type -[X <: Nat, Y <: Nat] <: Null | Nat //= ???

/**
 * II.6) Реализовать тип *[X, Y], который перемножает натуральные числа
 *
 *         summon[`3` + `1` - `2` * `2` =:= `0`] // compiles
 *         summon[`1` * `2` =:= `1`] // not compiles
 *
 */
type *[X <: Nat, Y <: Nat] <: Nat //= ???

//-----------------------------------------------------------------------------

/**
 * II. Макросы
 *
 *   1.Реализовать макрос в файле Macro.scala для безопасной работы с регулярными выражениями
 *     Для корректного константного регулярного выражения и для регулярного выражения известного только в рантайме
 *     SafeRegex.apply должен просто создавать Regex:
 *
 *       SafeRegex("[1-9]") // = Regex("[1-9]")
 *
 *       def x: String = ...
 *       SafeRegex(x) // = Regex(x)
 *
 *     Для невалидного константного регулярного выражения SafeRegex.apply должен бросать ошибку на этапе компиляции
 *
 *       SafeRegex("[1-9]\\") // should not compile
 *       SafeRegex("*") // should not compile
 *
 *     Важно!!! Это задание не имеет никакого отношения к библиотеке regsafe, про которую рассказывали
 *              на лекции.
 */

/**
 *   2.Релизовать метод log, который логирует модель в соответствии с ее инстансом Loggable,
 *     добавляя поля message, timestamp и position
 *
 *     Поле position должно быть строковым, содержать имя файла и номер строки в файле в которой был вызван метод log
 *     в формате ${fileName}:${lineNumber}
 *
 *       val user: User = ...
 *
 *       import Loggable.* // импорт может быть не нужен (зависит от реализации)
 *       user.log("user signed in")
 *
 *    Должно распечатать (если вызов log находится в Test.scala на строке 42):
 *       {"message":"user signed in","position":"Test.scala:42","time":"2023-09-20T14:35:30.874527","context":{"name":"Vasiliy","age":42}}
 *
 *    timestamp - текущее время
 *    в поле context - информация о пользователе
 *
 *    Макрос, который будет доставать позицию, реализовать в файле Macros.scala
 */
trait Loggable[A]:
  def jsonLog(a: A): Json

package hw

/**
 * I. Нетипизированное лямбда-ичисление
 *
 * Term --- определение λ-исчисления, состоящее из
 *   App (применение)
 *   Abs (абстракция)
 *   Var (переменная)
 */
type Name = Name.T
object Name:
  opaque type T <: String = String
  def apply(s: String): T = s

sealed trait Term:
  def apply(t: Term): Term = Term.App(this, t)
  def =>:(v: Term.Var): Term = Term.Abs(v.v, this)

  override def toString: String =
    this match
      case Term.App(t1, t2) => s"($t1)($t2)"
      case Term.Abs(v, t)   => s"$v =>: $t"
      case Term.Var(v)      => v

object Term:
  case class App(t1: Term, t2: Term) extends Term
  case class Abs(v: Name, t: Term) extends Term
  case class Var(v: Name) extends Term

import Term.*

val x = Var(Name("x"))
val y = Var(Name("y"))
val z = Var(Name("z"))
val l = Var(Name("l"))
val m = Var(Name("m"))
val n = Var(Name("n"))
val s = Var(Name("s"))

val id: Term = x =>: x

val `true`: Term = x =>: y =>: x
val `false`: Term = x =>: y =>: y
val `if`: Term = l =>: m =>: n =>: (l(m))(n)

val succ: Term = x =>: s =>: z =>: s(x(s)(z))

val `0`: Term = s =>: z =>: z
val `1`: Term = s =>: z =>: s(z)
val `2`: Term = s =>: z =>: s(s(z))
val `3`: Term = s =>: z =>: s(s(s(z)))
val `4`: Term = s =>: z =>: s(s(s(s(z))))
val `5`: Term = s =>: z =>: s(s(s(s(s(z)))))
val `6`: Term = s =>: z =>: s(s(s(s(s(s(z))))))
val `7`: Term = s =>: z =>: s(s(s(s(s(s(s(z)))))))
val `8`: Term = s =>: z =>: s(s(s(s(s(s(s(s(z))))))))

val plus: Term = x =>: y =>: s =>: z =>: x(s)(y(s)(z))

/**
 * 1) Реализуйте beta-редукцию:
 *   (λx.in) to → [x ↦ to]in
 *
 *   x - имя переменной
 *   in - терм в котором мы заменяем все свободные переменные x
 *   to - терм на который мы заменяем x
 */
def betaReduction(x: Name, in: Term, to: Term): Term = ???

/**
 * 2) Реализуйте полную редукцию терма, выбор стратегии вычисления
 *    (call-by-value / call-by-name / ...) остется за вами
 */
def reduce(term: Term): Term = ???

/**
 * 3.a) Реализуйте терм `not`, который инвертирует булевы констнаты Черча:
 *
 * `not`(`true`) = `false`
 * `not`(`false`) = `true`
 */
val `not`: Term = ???

/**
 * 3.б) Реализуйте терм `times`, который перемножает числа в кодировке Черча:
 *
 *  times(`2`)(`2`) = `4`
 *  times(`0`)(`3`) = `0`
 */
val times: Term = ???

/**
 * 3.в) Реализуйте терм `power`, который возводит число в кодировке Черча в степень:
 *
 *  power(`2`)(`3`) = `8`
 *  power(`0`)(`3`) = `0`
 *  power(`3`)(`0`) = `1`
 */
val power: Term = ???

/**
 * 3.г) Реализуйте терм `iszero`, который проверяет является ли число в кодировке Черча нулем:
 *
 *  iszero(`0`) = `true`
 *  iszero(`5`) = `false`
 */
val iszero: Term = ???

package hw

import io.circe.{Encoder, Json}

/** I. Мигрируем forSome
  *
  * Для работы c сериализацией данных в проекте X используются экзистенциальные типы данных, выраженные через
  * конструкцию forSome, которая не поддерживается в scala 3. Помогите сделать кросс-билд проекта X на scala 3, добавив
  * реализацию ProjectX, не использующую forSome.
  *
  * `SmthCanBeEncoded` - тип-обертка, содержащий значение некоторого типа и его сериализатор (возможно, кастомный) в
  * Json. `Input` - список `SmthCanBeEncoded` для каких-то типов `encode` - метод, сериализующий `Input` в Json-список,
  * каждый элемент которого получен с помощью сериализации соответствующего значения `SmthCanBeEncoded` его
  * сериализатором.
  *
  * Сейчас тесты проходят для второй версии скалы: `2-1-scala3-overview / test`
  *
  * Реализуйте в scala3/ соответствующие интерфейсы, чтобы тесты для 3 версии скалы проходили: `2-1-scala3-overview3 /
  * test`
  */
object ProjectX {
  case class SmthCanBeEncoded[A](value: A)(implicit enc: Encoder[A]) {
    def encode(x: A): Json = enc(x)
  }

  type Input = List[SmthCanBeEncoded[A] forSome { type A }]

  def encode(in: Input): Json =
    Json.fromValues(
      in.map[Json] { case (x: SmthCanBeEncoded[_]) => x.encode(x.value) }
    )
}

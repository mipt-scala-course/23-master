package hw.traverse

import java.time.ZonedDateTime
import cats.Applicative

import hw.traverse.FormLocalization.*

class Tests extends munit.FunSuite:

  given Applicative[Option] = cats.instances.option.catsStdInstancesForOption
  given Localization[Option] = new Localization[Option]:
    val keys = Map(
      Template("origination") -> Rendered("Ориджинейшн"),
      Template("applications") -> Rendered("Заявки"),
      Template("products") -> Rendered("Продукты"),
      Template("applicant") -> Rendered("Заявитель"),
      Template("eio") -> Rendered("Единоличный исполнительный орган")
    )
    def localize: Template => Option[Rendered] = keys.get

  val now = ZonedDateTime.now()
  val products = Form(
    Template("products"),
    List(Template("products")),
    List.empty,
    now
  )
  val applicant = Form(
    Template("applicant"),
    List(Template("applicant")),
    List.empty,
    now
  )
  val origination = Form(
    Template("origination"),
    List(Template("products"), Template("applicant"), Template("eio")),
    List(products, applicant),
    now
  )

  test("Traverse.1 localize form"):
    assert(localize(origination) == Some(
      Form(
        Rendered("Ориджинейшн"),
        List(Rendered("Продукты"), Rendered("Заявитель"), Rendered("Единоличный исполнительный орган")),
        List(
          Form(
            Rendered("Продукты"),
            List(Rendered("Продукты")),
            List.empty,
            now
          ),
          Form(
            Rendered("Заявитель"),
            List(Rendered("Заявитель")),
            List.empty,
            now
          )
        ),
        now
      )
    ))
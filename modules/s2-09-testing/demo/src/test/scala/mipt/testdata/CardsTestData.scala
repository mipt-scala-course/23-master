package mipt.testdata

import mipt.model._

trait CardsTestData {

  val cards = List(
    Card(
      CardUcid("123"),
      CardNumber("5555-3333-2213-1231"),
      10000
    ),
    Card(
      CardUcid("1234"),
      CardNumber("5455-3333-2213-1231"),
      12000
    )
  )

  val userId = UserId("The-User")

  val anotherCards = List(
    Card(
      CardUcid("123"),
      CardNumber("5555-3333-2213-1231"),
      10000
    ),
    Card(
      CardUcid("1234"),
      CardNumber("5455-3333-2213-1231"),
      12000
    ),
    Card(
      CardUcid("1432"),
      CardNumber("5445-3333-2213-1231"),
      65000
    )
  )

  val anotherUserId = UserId("The-User-2")

}

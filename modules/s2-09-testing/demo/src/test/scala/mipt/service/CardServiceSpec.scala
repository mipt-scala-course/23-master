package mipt.service

import cats.effect.SyncIO
import mipt.cache.CardsCache
import mipt.external.CardsExternalService
import mipt.testdata.CardsTestData
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CardServiceSpec extends AnyFlatSpec with Matchers with MockFactory {

  "getUserCards" should "return cards from external service and put them to cache for fallback" in new Wirings {

    externalService.getUserCards _ expects userId returning SyncIO(cards)
    cache.putUserCards _ expects (userId, cards) returning SyncIO(())

    service.getUserCards(userId).unsafeRunSync() shouldBe cards

  }

  it should "not fail if external service is available but cache is not" in new Wirings {

    externalService.getUserCards _ expects userId returning SyncIO(cards)
    cache.putUserCards _ expects (userId, cards) returning SyncIO.raiseError(
      new RuntimeException("Cache is not available")
    )

    service.getUserCards(userId).unsafeRunSync() shouldBe cards

  }

  it should "return cards from fallback cache if external service is unavailable" in new Wirings {

    externalService.getUserCards _ expects userId returning SyncIO.raiseError(
      new RuntimeException("Database is unavailable")
    )
    cache.getUserCards _ expects userId returning SyncIO(cards)

    service.getUserCards(userId).unsafeRunSync() shouldBe cards

  }

  trait Wirings extends CardsTestData {

    val externalService = mock[CardsExternalService[SyncIO]]
    val cache           = mock[CardsCache[SyncIO]]
    val service         = CardService(externalService, cache)

  }

}

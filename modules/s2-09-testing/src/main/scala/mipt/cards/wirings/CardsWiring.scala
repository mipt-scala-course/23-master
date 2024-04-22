package mipt.cards.wirings

import cats.effect.{Async, Resource}
import dev.profunktor.redis4cats.RedisCommands
import mipt.cards.cache.CardsCache
import mipt.cards.external.CardsExternalService
import mipt.cards.service.CardService
import mipt.sharing.service.SharingService
import org.http4s.client.Client

object CardsWiring:
  def wire[F[_]: Async](
      redis: Resource[F, RedisCommands[F, String, String]],
      httpClient: Resource[F, Client[F]],
      sharingService: Resource[F, SharingService[F]],
      externalServiceUri: String
  ): Resource[F, CardService[F]] =
    for
      client        <- httpClient
      redisCommands <- redis
      accesses      <- sharingService
      externalService = CardsExternalService(client, externalServiceUri)
      cache           = CardsCache(redisCommands)
      service         = CardService(externalService, accesses, cache)
    yield service

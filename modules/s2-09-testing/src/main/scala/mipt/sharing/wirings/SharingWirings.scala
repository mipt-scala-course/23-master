package mipt.sharing.wirings

import cats.effect.{Async, Resource}
import dev.profunktor.redis4cats.RedisCommands
import mipt.sharing.cache.SharingCache
import mipt.sharing.external.SharingExternalService
import mipt.sharing.service.SharingService
import org.http4s.client.Client

object SharingWirings:
  def wire[F[_]: Async](
      redis: Resource[F, RedisCommands[F, String, String]],
      httpClient: Resource[F, Client[F]],
      externalServiceUri: String
  ): Resource[F, SharingService[F]] =
    for
      client        <- httpClient
      redisCommands <- redis
      externalService = SharingExternalService(client, externalServiceUri)
      cache           = SharingCache(redisCommands)
      service         = SharingService(externalService, cache)
    yield service

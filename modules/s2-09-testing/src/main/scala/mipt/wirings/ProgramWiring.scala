package mipt.wirings

import cats.effect.{Async, Resource}
import dev.profunktor.redis4cats.{Redis, RedisCommands}
import dev.profunktor.redis4cats.log4cats.*
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.client.Client
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object ProgramWiring:

  def redis[F[_]: Async](redisUri: String): Resource[F, RedisCommands[F, String, String]] =
    given Logger[F] = Slf4jLogger.getLogger[F]
    Redis[F].utf8(redisUri)

  def httpClient[F[_]: Async]: Resource[F, Client[F]] = BlazeClientBuilder[F].resource

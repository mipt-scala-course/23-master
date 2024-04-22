package mipt.sharing.cache

import cats.Functor
import cats.syntax.all.*
import dev.profunktor.redis4cats.RedisCommands
import io.circe.parser.*
import io.circe.syntax.*
import mipt.common.model.{UserId, given}

trait SharingCache[F[_]]:
  def getAccessibleUsers(userId: UserId): F[List[UserId]]
  def putAccessibleUsers(userId: UserId, users: List[UserId]): F[Unit]

object SharingCache:
  private class Impl[F[_]: Functor](redisCommands: RedisCommands[F, String, String]) extends SharingCache[F]:
    override def getAccessibleUsers(userId: UserId): F[List[UserId]] =
      redisCommands.get(userId).map {
        case Some(value) => decode[List[UserId]](value).getOrElse(List.empty)
        case None        => List.empty
      }

    override def putAccessibleUsers(userId: UserId, users: List[UserId]): F[Unit] =
      redisCommands.set(userId, users.asJson.noSpaces)

  def apply[F[_]: Functor](redisCommands: RedisCommands[F, String, String]): SharingCache[F] =
    new Impl[F](redisCommands)

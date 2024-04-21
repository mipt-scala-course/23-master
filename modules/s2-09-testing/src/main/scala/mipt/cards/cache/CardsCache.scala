package mipt.cards.cache

import cats.Functor
import cats.syntax.all.*
import dev.profunktor.redis4cats.RedisCommands
import io.circe.parser.*
import io.circe.syntax.*
import mipt.cards.model.Card
import mipt.common.model.UserId

trait CardsCache[F[_]]:
  def getUserCards(userId: UserId): F[List[Card]]

  def putUserCards(userId: UserId, cards: List[Card]): F[Unit]

object CardsCache:

  private class Impl[F[_]: Functor](redisCommands: RedisCommands[F, String, String]) extends CardsCache[F]:
    override def getUserCards(userId: UserId): F[List[Card]] =
      redisCommands.get(userId).map {
        case Some(value) => decode[List[Card]](value).getOrElse(List.empty)
        case None        => List.empty
      }

    override def putUserCards(userId: UserId, cards: List[Card]): F[Unit] =
      redisCommands.set(userId, cards.asJson.noSpaces)

  def apply[F[_]: Functor](redisCommands: RedisCommands[F, String, String]): CardsCache[F] =
    new Impl(redisCommands)

package mipt.cards.external

import cats.Functor
import cats.syntax.all.*
import io.circe.parser.*
import mipt.cards.model.Card
import mipt.common.model.UserId
import org.http4s.{EntityDecoder, Uri}
import org.http4s.client.Client

trait CardsExternalService[F[_]]:
  def getUserCards(userId: UserId): F[List[Card]]

object CardsExternalService:

  type StringDecoder[F[_]] = EntityDecoder[F, String]

  def getCardsRelativePath(userId: String): String = s"/users/$userId/cards"

  private class Impl[F[_]: StringDecoder: Functor](client: Client[F], baseUri: String) extends CardsExternalService[F]:
    def getCardsUri(userId: UserId): Uri =
      Uri.unsafeFromString(s"$baseUri${getCardsRelativePath(userId)}")

    override def getUserCards(userId: UserId): F[List[Card]] =
      client
        .expect[String](getCardsUri(userId))
        .map(decode[List[Card]])
        .map(_.getOrElse(List.empty))

  def apply[F[_]: StringDecoder: Functor](client: Client[F], baseUri: String): CardsExternalService[F] =
    new Impl[F](client, baseUri)

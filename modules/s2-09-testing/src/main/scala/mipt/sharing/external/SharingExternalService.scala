package mipt.sharing.external

import cats.Functor
import cats.syntax.all.*
import io.circe.parser.*
import mipt.common.model.{UserId, given_Decoder_UserId}
import org.http4s.{EntityDecoder, Uri}
import org.http4s.client.Client

trait SharingExternalService[F[_]]:
  def getAccessibleUsers(userId: UserId): F[List[UserId]]

object SharingExternalService:

  type StringDecoder[F[_]] = EntityDecoder[F, String]

  def getAccessesRelativePath(userId: String): String = s"/accesses/$userId"

  private class Impl[F[_]: StringDecoder: Functor](
      client: Client[F],
      baseUri: String
  ) extends SharingExternalService[F]:
    override def getAccessibleUsers(userId: UserId): F[List[UserId]] =
      client
        .expect[String](getAccessesUri(userId))
        .map(decode[List[UserId]])
        .map(_.getOrElse(List.empty))

    private def getAccessesUri(userId: UserId): Uri =
      Uri.unsafeFromString(s"$baseUri${getAccessesRelativePath(userId)}")

  def apply[F[_]: StringDecoder: Functor](client: Client[F], baseUri: String): SharingExternalService[F] =
    new Impl[F](client, baseUri)

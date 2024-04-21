package mipt.sharing.service

import cats.Monad
import cats.syntax.all.*
import mipt.common.model.UserId
import mipt.sharing.external.SharingExternalService
import mipt.sharing.cache.SharingCache

trait SharingService[F[_]]:
  def getAccessibleUsers(userId: UserId): F[List[UserId]]

object SharingService:

  private class Impl[F[_]: Monad](
      externalService: SharingExternalService[F[_]],
      cache: SharingCache[F]
  ) extends SharingService[F]:
    override def getAccessibleUsers(userId: UserId): F[List[UserId]] =
      for
        users  <- cache.getAccessibleUsers(userId)
        result <- if (users.nonEmpty) users.pure[F] else getAndCacheAccesses(userId)
      yield result

    private def getAndCacheAccesses(userId: UserId): F[List[UserId]] =
      for
        users <- externalService.getAccessibleUsers(userId)
        _     <- cache.putAccessibleUsers(userId, users)
      yield users

  def apply[F[_]: Monad](externalService: SharingExternalService[F], cache: SharingCache[F]): SharingService[F] =
    new Impl[F](externalService, cache)

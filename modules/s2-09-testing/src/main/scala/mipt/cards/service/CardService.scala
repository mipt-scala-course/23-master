package mipt.cards.service

import cats.MonadThrow
import cats.syntax.all.*
import mipt.cards.cache.CardsCache
import mipt.cards.external.CardsExternalService
import mipt.cards.model.Card
import mipt.common.model.UserId
import mipt.sharing.service.SharingService

trait CardService[F[_]]:
  def getUserCards(userId: UserId): F[List[Card]]

object CardService:
  private class Impl[F[_]: MonadThrow](
      externalService: CardsExternalService[F],
      sharingService: SharingService[F],
      cache: CardsCache[F]
  ) extends CardService[F]:
    override def getUserCards(userId: UserId): F[List[Card]] =
      for
        users            <- sharingService.getAccessibleUsers(userId)
        currentUserCards <- getSingleUserCards(userId)
        cards            <- users.traverse(getSingleUserCards)
      yield cards.flatten ++ currentUserCards

    private def getSingleUserCards(userId: UserId): F[List[Card]] = {
      for
        cards <- externalService.getUserCards(userId)
        _     <- cache.putUserCards(userId, cards).handleError(_ => ())
      yield cards
    }.handleErrorWith(_ => cache.getUserCards(userId))

  def apply[F[_]: MonadThrow](
      externalService: CardsExternalService[F],
      sharingService: SharingService[F],
      cache: CardsCache[F]
  ): CardService[F] =
    new Impl[F](externalService, sharingService, cache)

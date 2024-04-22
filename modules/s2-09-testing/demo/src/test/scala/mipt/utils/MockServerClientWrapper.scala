package mipt.utils

import cats.effect.IO
import com.dimafeng.testcontainers.MockServerContainer
import mipt.external.CardsExternalService
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response

object MockServerClientWrapper {

  def mockGetCards(mockServer: MockServerContainer, userId: String, cards: String): IO[Unit] = IO {
    new MockServerClient("localhost", mockServer.serverPort)
      .when(
        request()
          .withMethod("GET")
          .withPath(CardsExternalService.getCardsRelativePath(userId))
      )
      .respond(response().withBody(cards))
  }

}

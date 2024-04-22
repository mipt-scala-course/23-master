package mipt

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

package object model {

  object UserId {
    type Id <: String

    def apply(id: String): Id = id.asInstanceOf[Id]

    implicit val encoder: Encoder[Id] = Encoder[String].contramap(identity)
    implicit val decoder: Decoder[Id] = Decoder[String].map(UserId(_))

  }

  type UserId = UserId.Id

  object CardUcid {
    type Id <: String

    def apply(id: String): Id = id.asInstanceOf[Id]

    implicit val encoder: Encoder[Id] = Encoder[String].contramap(identity)
    implicit val decoder: Decoder[Id] = Decoder[String].map(CardUcid(_))
  }

  type CardUcid = CardUcid.Id

  object CardNumber {

    type Value <: String

    def apply(value: String): Value = value.asInstanceOf[Value]

    implicit val encoder: Encoder[Value] = Encoder[String].contramap(identity)
    implicit val decoder: Decoder[Value] = Decoder[String].map(CardNumber(_))

  }

  type CardNumber = CardNumber.Value

  case class Card(ucid: CardUcid, number: CardNumber, amount: Double)

  object Card {
    implicit val cardEncoder: Encoder[Card] = deriveEncoder[Card]
    implicit val cardDecoder: Decoder[Card] = deriveDecoder[Card]
  }

}

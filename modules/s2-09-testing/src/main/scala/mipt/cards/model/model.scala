package mipt.cards.model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

object CardUcid:
  opaque type Id <: String = String

  def apply(id: String): Id = id

type CardUcid = CardUcid.Id
given Encoder[CardUcid] = Encoder[String].contramap(identity)
given Decoder[CardUcid] = Decoder[String].map(CardUcid(_))

object CardNumber:
  opaque type Value <: String = String

  def apply(value: String): Value = value

type CardNumber = CardNumber.Value
given Encoder[CardNumber] = Encoder[String].contramap(identity)
given Decoder[CardNumber] = Decoder[String].map(CardNumber(_))

case class Card(ucid: CardUcid, number: CardNumber, amount: Double)

object Card:
  given Encoder[Card] = deriveEncoder[Card]
  given Decoder[Card] = deriveDecoder[Card]

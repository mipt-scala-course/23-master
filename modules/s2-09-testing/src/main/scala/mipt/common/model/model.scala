package mipt.common.model

import io.circe.{Decoder, Encoder}

object UserId:
  opaque type Id <: String = String

  def apply(id: String): Id = id

type UserId = UserId.Id
given Encoder[UserId] = Encoder[String].contramap(identity)
given Decoder[UserId] = Decoder[String].map(UserId(_))

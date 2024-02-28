package hw

import io.circe.{Encoder, Json}
import io.circe.syntax._

object ProjectX:
  trait SmthCanBeEncoded
    def encode: Json
  
  object SmthCanBeEncoded:
    def apply[X](a: X)(using Encoder[X]): SmthCanBeEncoded = new SmthCanBeEncoded
      override def encode: Json = a.asJson

  type Input = List[SmthCanBeEncoded]

  def encode(in: Input): Json =
    Json.fromValues(in.map(_.encode))

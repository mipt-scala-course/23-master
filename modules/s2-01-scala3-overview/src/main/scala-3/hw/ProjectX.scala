package hw

import io.circe.{Encoder, Json}

object ProjectX:
  trait SmthCanBeEncoded

  object SmthCanBeEncoded:
    def apply[X](a: X)(using Encoder[X]): SmthCanBeEncoded = ???

  type Input = List[SmthCanBeEncoded]

  def encode(in: Input): Json = ???

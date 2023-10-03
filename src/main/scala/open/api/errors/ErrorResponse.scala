package open.api.errors

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import sttp.tapir.Schema

case class ErrorResponse(message: String)

object ErrorResponse {
  implicit val errorResponseEncoder: Encoder[ErrorResponse] = deriveEncoder[ErrorResponse]
  implicit val errorResponseDecoder: Decoder[ErrorResponse] = deriveDecoder[ErrorResponse]
  implicit val errorResponseSchema: Schema[ErrorResponse] = Schema.derived
}

package open.api.models.responses

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import sttp.tapir.Schema

case class SuccessResponse(message: String)

object SuccessResponse {
  implicit val userEncoder: Encoder[SuccessResponse] = deriveEncoder[SuccessResponse]
  implicit val userDecoder: Decoder[SuccessResponse] = deriveDecoder[SuccessResponse]
  implicit val userSchema: Schema[SuccessResponse] = Schema.derived
}

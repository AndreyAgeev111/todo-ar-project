package open.api.models.responses

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import sttp.tapir.Schema

case class UserRegisterResponse(login: String)

object UserRegisterResponse {
  implicit val userEncoder: Encoder[UserRegisterResponse] = deriveEncoder[UserRegisterResponse]
  implicit val userDecoder: Decoder[UserRegisterResponse] = deriveDecoder[UserRegisterResponse]
  implicit val userSchema: Schema[UserRegisterResponse] = Schema.derived
}



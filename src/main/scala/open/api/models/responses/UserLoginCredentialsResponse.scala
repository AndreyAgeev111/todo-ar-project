package open.api.models.responses

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import sttp.tapir.Schema

case class UserLoginCredentialsResponse(login: String)

object UserLoginCredentialsResponse {
  implicit val userEncoder: Encoder[UserLoginCredentialsResponse] = deriveEncoder[UserLoginCredentialsResponse]
  implicit val userDecoder: Decoder[UserLoginCredentialsResponse] = deriveDecoder[UserLoginCredentialsResponse]
  implicit val userSchema: Schema[UserLoginCredentialsResponse] = Schema.derived
}

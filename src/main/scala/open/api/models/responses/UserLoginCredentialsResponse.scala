package open.api.models.responses

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import sttp.tapir.Schema

case class UserLoginCredentialsResponse(result: Boolean,
                                        login: String)

object UserLoginCredentialsResponse {
  implicit val userLoginFormEncoder: Encoder[UserLoginCredentialsResponse] = deriveEncoder[UserLoginCredentialsResponse]
  implicit val userLoginFormDecoder: Decoder[UserLoginCredentialsResponse] = deriveDecoder[UserLoginCredentialsResponse]
  implicit val userLoginFormSchema: Schema[UserLoginCredentialsResponse] = Schema.derived
}

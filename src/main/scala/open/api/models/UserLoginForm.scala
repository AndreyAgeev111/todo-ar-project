package open.api.models

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import sttp.tapir.EndpointIO.annotations.endpointInput
import sttp.tapir.Schema

@endpointInput("/login")
case class UserLoginForm(login: String,
                         password: String)

object UserLoginForm {
  implicit val userLoginFormEncoder: Encoder[UserLoginForm] = deriveEncoder[UserLoginForm]
  implicit val userLoginFormDecoder: Decoder[UserLoginForm] = deriveDecoder[UserLoginForm]
  implicit val userLoginFormSchema: Schema[UserLoginForm] = Schema.derived
}
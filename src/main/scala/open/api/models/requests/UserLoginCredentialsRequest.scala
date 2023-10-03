package open.api.models.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import sttp.tapir.EndpointIO.annotations.endpointInput
import sttp.tapir.{Codec, Schema}

@endpointInput("/login")
case class UserLoginCredentialsRequest(login: String, password: String)

object UserLoginCredentialsRequest {
  implicit val userLoginFormEncoder: Encoder[UserLoginCredentialsRequest] = deriveEncoder[UserLoginCredentialsRequest]
  implicit val userLoginFormDecoder: Decoder[UserLoginCredentialsRequest] = deriveDecoder[UserLoginCredentialsRequest]
  implicit val userLoginFormSchema: Schema[UserLoginCredentialsRequest] = Schema.derived
}

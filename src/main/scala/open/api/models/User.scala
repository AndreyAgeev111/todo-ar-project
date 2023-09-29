package open.api.models

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import sttp.tapir.EndpointIO.annotations.endpointInput
import sttp.tapir.Schema

@endpointInput("/signup")
case class User(login: String,
                password: String,
                firstName: Option[String],
                secondName: Option[String],
                email: Option[String])

object User {
  implicit val userEncoder: Encoder[User] = deriveEncoder[User]
  implicit val userDecoder: Decoder[User] = deriveDecoder[User]
  implicit val userSchema: Schema[User] = Schema.derived
}
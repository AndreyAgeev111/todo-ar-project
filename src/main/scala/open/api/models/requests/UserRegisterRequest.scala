package open.api.models.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import open.api.persistent.dto.{UserDto, UserLoginCredentialsDto}
import sttp.tapir.EndpointIO.annotations.endpointInput
import sttp.tapir.Schema

@endpointInput("/sign-up")
case class UserRegisterRequest(login: String,
                               password: String,
                               firstName: Option[String],
                               secondName: Option[String],
                               email: Option[String])

object UserRegisterRequest {
  def toUserCredentials(user: UserRegisterRequest): UserLoginCredentialsDto =
    UserLoginCredentialsDto(
      login = user.login,
      password = user.password
    )

  def toUser(user: UserRegisterRequest): UserDto =
    UserDto(
      login = user.login,
      firstName = user.firstName,
      secondName = user.secondName,
      email = user.email
    )

  implicit val userEncoder: Encoder[UserRegisterRequest] = deriveEncoder[UserRegisterRequest]
  implicit val userDecoder: Decoder[UserRegisterRequest] = deriveDecoder[UserRegisterRequest]
  implicit val userSchema: Schema[UserRegisterRequest] = Schema.derived
}
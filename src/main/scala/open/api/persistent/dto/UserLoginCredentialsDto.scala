package open.api.persistent.dto

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import open.api.models.requests.UserRegisterRequest
import sttp.tapir.EndpointIO.annotations.endpointInput
import sttp.tapir.Schema

case class UserLoginCredentialsDto(login: String, password: String)

object UserLoginCredentialsDto {
  def fromRegisterRequest(user: UserRegisterRequest): UserLoginCredentialsDto =
    UserLoginCredentialsDto(
      login = user.login,
      password = user.password
    )
}

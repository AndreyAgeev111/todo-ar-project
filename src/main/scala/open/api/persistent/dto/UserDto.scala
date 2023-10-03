package open.api.persistent.dto

import open.api.models.requests.UserRegisterRequest

case class UserDto(login: String, firstName: Option[String], secondName: Option[String], email: Option[String])

object UserDto {
  def fromRegisterRequest(user: UserRegisterRequest): UserDto =
    UserDto(
      login = user.login,
      firstName = user.firstName,
      secondName = user.secondName,
      email = user.email
    )
}

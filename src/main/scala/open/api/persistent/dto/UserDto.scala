package open.api.persistent.dto

case class UserDto(login: String,
                   firstName: Option[String],
                   secondName: Option[String],
                   email: Option[String])
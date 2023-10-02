package open.api.persistent.dto

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import sttp.tapir.EndpointIO.annotations.endpointInput
import sttp.tapir.Schema

case class UserLoginCredentialsDto(login: String,
                                   password: String)
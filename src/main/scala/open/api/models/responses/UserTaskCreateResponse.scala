package open.api.models.responses

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import sttp.tapir.Schema

case class UserTaskCreateResponse(message: String = "Task added or update successfully")

object UserTaskCreateResponse {
  implicit val userTaskZioEncoder: Encoder[UserTaskCreateResponse] = deriveEncoder[UserTaskCreateResponse]
  implicit val userTaskZioDecoder: Decoder[UserTaskCreateResponse] = deriveDecoder[UserTaskCreateResponse]
  implicit val userTaskSchema: Schema[UserTaskCreateResponse] = Schema.derived
}

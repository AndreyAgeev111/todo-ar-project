package open.api.models.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import open.api.models.TaskStatuses.TaskStatus
import sttp.tapir.Schema

import java.time.Instant

case class UserTaskResponse(
    id: String,
    name: String,
    description: Option[String],
    createdAt: Instant,
    deadline: Instant,
    status: TaskStatus
)

object UserTaskResponse {
  implicit val userTaskZioEncoder: Encoder[UserTaskResponse] = deriveEncoder[UserTaskResponse]
  implicit val userTaskZioDecoder: Decoder[UserTaskResponse] = deriveDecoder[UserTaskResponse]
  implicit val userTaskSchema: Schema[UserTaskResponse] = Schema.derived
}

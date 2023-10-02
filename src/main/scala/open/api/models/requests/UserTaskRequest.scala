package open.api.models.requests

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import open.api.models.TaskStatuses.TaskStatus
import sttp.tapir.EndpointIO.annotations.endpointInput
import sttp.tapir.Schema

import java.time.Instant

@endpointInput("/task")
case class UserTaskRequest(name: String,
                           description: Option[String],
                           deadline: Instant,
                           status: TaskStatus)

object UserTaskRequest {
  implicit val userTaskZioEncoder: Encoder[UserTaskRequest] = deriveEncoder[UserTaskRequest]
  implicit val userTaskZioDecoder: Decoder[UserTaskRequest] = deriveDecoder[UserTaskRequest]
  implicit val userTaskSchema: Schema[UserTaskRequest] = Schema.derived
}

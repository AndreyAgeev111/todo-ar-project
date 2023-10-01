package open.api.models

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._
import open.api.models.TaskStatuses.TaskStatus
import sttp.tapir.EndpointIO.annotations.endpointInput
import sttp.tapir.Schema

import java.time.Instant

@endpointInput("/task")
case class UserTask(id: String,
                    name: String,
                    description: Option[String],
                    createdAt: Instant,
                    deadline: Instant,
                    status: TaskStatus)

object UserTask {
  implicit val userTaskZioEncoder: Encoder[UserTask] = deriveEncoder[UserTask]
  implicit val userTaskZioDecoder: Decoder[UserTask] = deriveDecoder[UserTask]
  implicit val userTaskSchema: Schema[UserTask] = Schema.derived
}

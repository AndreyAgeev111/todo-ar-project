package open.api.models.requests

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import open.api.models.TaskStatuses.TaskStatus
import sttp.tapir.EndpointIO.annotations.endpointInput
import sttp.tapir.Schema

import java.time.Instant

@endpointInput("/task")
case class UserTaskCreateRequest(name: String, description: Option[String], deadline: Instant, status: TaskStatus)

object UserTaskCreateRequest {
  implicit val userTaskZioEncoder: Encoder[UserTaskCreateRequest] = deriveEncoder[UserTaskCreateRequest]
  implicit val userTaskZioDecoder: Decoder[UserTaskCreateRequest] = deriveDecoder[UserTaskCreateRequest]
  implicit val userTaskSchema: Schema[UserTaskCreateRequest] = Schema.derived
}

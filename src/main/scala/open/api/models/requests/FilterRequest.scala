package open.api.models.requests

import cats.data.NonEmptyList
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import open.api.models.TaskStatuses.TaskStatus
import sttp.tapir.Schema

import java.time.Instant

case class FilterRequest(
    name: Option[String],
    description: Option[String],
    createdAfter: Option[Instant],
    createdBefore: Option[Instant],
    deadlineAfter: Option[Instant],
    deadlineBefore: Option[Instant],
    statuses: List[TaskStatus]
)

object FilterRequest {
  implicit val userEncoder: Encoder[FilterRequest] = deriveEncoder[FilterRequest]
  implicit val userDecoder: Decoder[FilterRequest] = deriveDecoder[FilterRequest]
  implicit val userSchema: Schema[FilterRequest] = Schema.derived
}

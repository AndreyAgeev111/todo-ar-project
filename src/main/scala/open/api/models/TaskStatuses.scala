package open.api.models

import doobie.Meta
import doobie.postgres.implicits.pgEnum
import io.circe.{Decoder, Encoder}
import sttp.tapir.Schema

object TaskStatuses extends Enumeration {
  type TaskStatus = Value

  val ToDo, InProgress, Done: TaskStatus = Value

  implicit val taskStatusEncoder: Encoder[TaskStatus] = Encoder.encodeEnumeration(TaskStatuses)
  implicit val taskStatusDecoder: Decoder[TaskStatus] = Decoder.decodeEnumeration(TaskStatuses)
  implicit val taskStatusSchema: Schema[TaskStatuses.TaskStatus] = Schema.derivedEnumerationValue
  implicit val taskStatusMeta: Meta[TaskStatuses.Value] = pgEnum(TaskStatuses, "task_status")
}

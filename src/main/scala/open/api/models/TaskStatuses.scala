package open.api.models

import io.circe.{Decoder, Encoder}
import sttp.tapir.Schema


object TaskStatuses extends Enumeration {
  type TaskStatus = Value

  val ToDo: TaskStatus = Value("To be done")
  val InProgress: TaskStatus = Value("In progress")
  val Done: TaskStatus = Value("Done")

  implicit val taskStatusEncoder: Encoder[TaskStatus] = Encoder.encodeEnumeration(TaskStatuses)
  implicit val taskStatusDecoder: Decoder[TaskStatus] = Decoder.decodeEnumeration(TaskStatuses)
  implicit val taskStatusSchema: Schema[TaskStatuses.TaskStatus] = Schema.derivedEnumerationValue
}

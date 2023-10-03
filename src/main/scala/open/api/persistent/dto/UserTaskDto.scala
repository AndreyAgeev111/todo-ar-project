package open.api.persistent.dto

import open.api.models.TaskStatuses.TaskStatus
import open.api.models.requests.UserTaskCreateRequest
import open.api.models.responses.UserTaskResponse

import java.time.Instant
import scala.math.random

case class UserTaskDto(
    id: String,
    userLogin: String,
    name: String,
    description: Option[String],
    createdAt: Instant,
    deadline: Instant,
    status: TaskStatus
)

object UserTaskDto {
  def apply(userTask: UserTaskCreateRequest, userLogin: String): UserTaskDto =
    UserTaskDto(
      id = generateId(userTask, userLogin),
      userLogin = userLogin,
      name = userTask.name,
      description = userTask.description,
      createdAt = Instant.now(),
      deadline = userTask.deadline,
      status = userTask.status
    )

  def toTaskResponse(dto: UserTaskDto): UserTaskResponse =
    UserTaskResponse(
      id = dto.id,
      name = dto.name,
      description = dto.description,
      createdAt = dto.createdAt,
      deadline = dto.deadline,
      status = dto.status
    )

  private def generateId(userTask: UserTaskCreateRequest, userLogin: String): String =
    s"TASK-${userLogin}-${userTask.name.hashCode}-${(random() * 500000 + random() * 1000000).toInt}"
}

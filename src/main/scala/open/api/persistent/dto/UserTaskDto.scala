package open.api.persistent.dto

import open.api.models.TaskStatuses.TaskStatus
import open.api.models.UserTask

import java.time.Instant

case class UserTaskDto(id: String,
                       userLogin: String,
                       name: String,
                       description: Option[String],
                       createdAt: Instant,
                       deadline: Instant,
                       status: TaskStatus)

object UserTaskDto {
  def apply(userTask: UserTask, userLogin: String): UserTaskDto =
    UserTaskDto(
      id = userTask.id,
      userLogin = userLogin, 
      name = userTask.name, 
      description = userTask.description, 
      createdAt = userTask.createdAt, 
      deadline = userTask.deadline, 
      status = userTask.status
    )
    
  def toTask(dto: UserTaskDto): UserTask =
    UserTask(
      id = dto.id,
      name = dto.name,
      description = dto.description,
      createdAt = dto.createdAt,
      deadline = dto.deadline,
      status = dto.status)
}
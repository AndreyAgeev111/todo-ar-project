package open.api.services

import cats.effect.IO
import open.api.errors.ErrorResponse
import open.api.models.TaskStatuses.TaskStatus
import open.api.models.requests.UserTaskCreateRequest
import open.api.models.responses.{SuccessResponse, UserTaskResponse}
import open.api.persistent.dto.UserTaskDto
import open.api.persistent.errors.NotFoundTaskError
import open.api.persistent.repository.UserTaskRepository
import org.postgresql.util.PSQLException
import sttp.model.StatusCode

trait UserTaskService[F[_]] {
  def addUserTask(
      userTask: UserTaskCreateRequest,
      userLogin: String
  ): F[Either[(StatusCode, ErrorResponse), (StatusCode, SuccessResponse)]]
  def listUserTasks(userLogin: String): F[Either[(StatusCode, ErrorResponse), (StatusCode, List[UserTaskResponse])]]
  def updateUserTask(
      userTask: UserTaskCreateRequest,
      taskId: String,
      userLogin: String
  ): F[Either[(StatusCode, ErrorResponse), (StatusCode, SuccessResponse)]]
  def findUserTask(userLogin: String, taskId: String): F[Either[(StatusCode, ErrorResponse), (StatusCode, UserTaskResponse)]]
  def deleteUserTask(userLogin: String, taskId: String): F[Either[(StatusCode, ErrorResponse), (StatusCode, SuccessResponse)]]
  def updateTaskStatus(
      userLogin: String,
      taskId: String,
      status: TaskStatus
  ): F[Either[(StatusCode, ErrorResponse), (StatusCode, SuccessResponse)]]
}

class UserTaskServiceImpl(userTaskRepository: UserTaskRepository[IO]) extends UserTaskService[IO] {
  override def addUserTask(
      userTask: UserTaskCreateRequest,
      userLogin: String
  ): IO[Either[(StatusCode, ErrorResponse), (StatusCode, SuccessResponse)]] =
    userTaskRepository
      .addUserTask(userTask, userLogin)
      .map(_ => Right(StatusCode.Ok -> SuccessResponse(message = "Task added or update successfully")))
      .recover {
        case e: PSQLException if e.getMessage.contains(s"(user_login)=($userLogin)") =>
          Left(StatusCode.BadRequest -> ErrorResponse(s"Invalid request - user with login = $userLogin is unknown"))
        case e: PSQLException =>
          Left(StatusCode.BadGateway -> ErrorResponse(s"Internal server error with error = $e"))
      }

  override def listUserTasks(userLogin: String): IO[Either[(StatusCode, ErrorResponse), (StatusCode, List[UserTaskResponse])]] =
    userTaskRepository
      .listUserTasks(userLogin)
      .map(_.map(UserTaskDto.toTaskResponse))
      .map(tasks => Right(StatusCode.Ok -> tasks))
      .recover { case e: PSQLException =>
        Left(StatusCode.BadGateway -> ErrorResponse(s"Internal server error with error = $e"))
      }

  override def updateUserTask(
      userTask: UserTaskCreateRequest,
      taskId: String,
      userLogin: String
  ): IO[Either[(StatusCode, ErrorResponse), (StatusCode, SuccessResponse)]] =
    userTaskRepository
      .updateUserTask(userTask, taskId, userLogin)
      .map(_ => Right(StatusCode.Ok -> SuccessResponse(message = "Task added or update successfully")))
      .recover {
        case e: NotFoundTaskError =>
          Left(StatusCode.BadRequest -> ErrorResponse(e.message))
        case e: PSQLException =>
          Left(StatusCode.BadGateway -> ErrorResponse(s"Internal server error with error = $e"))
      }

  override def findUserTask(userLogin: String, taskId: String): IO[Either[(StatusCode, ErrorResponse), (StatusCode, UserTaskResponse)]] =
    userTaskRepository
      .findUserTaskById(userLogin, taskId)
      .map {
        case Some(task) => Right(StatusCode.Ok -> UserTaskDto.toTaskResponse(task))
        case None       => Left(StatusCode.BadRequest -> ErrorResponse(new NotFoundTaskError(taskId).message))
      }
      .recover { case e: PSQLException =>
        Left(StatusCode.BadGateway -> ErrorResponse(s"Internal server error with error = $e"))
      }

  override def deleteUserTask(userLogin: String, taskId: String): IO[Either[(StatusCode, ErrorResponse), (StatusCode, SuccessResponse)]] =
    userTaskRepository
      .deleteUserTask(userLogin, taskId)
      .map(_ => Right(StatusCode.Ok -> SuccessResponse(message = s"Task with id = $taskId was successfully deleted")))
      .recover {
        case _: NotFoundTaskError =>
          Left(StatusCode.BadRequest -> ErrorResponse(new NotFoundTaskError(taskId).message))
        case e: PSQLException =>
          Left(StatusCode.BadGateway -> ErrorResponse(s"Internal server error with error = $e"))
      }

  override def updateTaskStatus(
      userLogin: String,
      taskId: String,
      status: TaskStatus
  ): IO[Either[(StatusCode, ErrorResponse), (StatusCode, SuccessResponse)]] =
    userTaskRepository
      .updateTaskStatus(userLogin, taskId, status)
      .map(_ =>
        Right(StatusCode.Ok -> SuccessResponse(message = s"Task's status with id = $taskId was successfully updated with status = $status"))
      )
      .recover {
        case _: NotFoundTaskError =>
          Left(StatusCode.BadRequest -> ErrorResponse(new NotFoundTaskError(taskId).message))
        case e: PSQLException =>
          Left(StatusCode.BadGateway -> ErrorResponse(s"Internal server error with error = $e"))
      }
}

package open.api.services

import cats.effect.IO
import open.api.errors.ErrorResponse
import open.api.models.requests.UserTaskCreateRequest
import open.api.models.responses.{UserTaskCreateResponse, UserTaskResponse}
import open.api.persistent.repository.UserTaskRepository
import org.postgresql.util.PSQLException
import sttp.model.StatusCode

trait UserTaskService[F[_]] {
  def addUserTask(userTask: UserTaskCreateRequest, userLogin: String): F[Either[(StatusCode, ErrorResponse), (StatusCode, UserTaskCreateResponse)]]
  def listUserTasks(userLogin: String): F[Either[(StatusCode, ErrorResponse), (StatusCode, List[UserTaskResponse])]]
}

class UserTaskServiceImpl(userTaskRepository: UserTaskRepository[IO]) extends UserTaskService[IO] {
  override def addUserTask(userTask: UserTaskCreateRequest, userLogin: String): IO[Either[(StatusCode, ErrorResponse), (StatusCode, UserTaskCreateResponse)]] =
    userTaskRepository.addUserTask(userTask, userLogin)
      .map(_ => Right(StatusCode.Ok -> UserTaskCreateResponse()))
      .recover {
        case e: PSQLException if e.getMessage.contains(s"(user_login)=($userLogin)")=> Left(StatusCode.BadRequest -> ErrorResponse(s"Invalid request - user with login = $userLogin is unknown"))
      }

  override def listUserTasks(userLogin: String): IO[Either[(StatusCode, ErrorResponse), (StatusCode, List[UserTaskResponse])]] =
    userTaskRepository.listUserTasks(userLogin)
      .map(tasks => Right(StatusCode.Ok -> tasks))
      .recover {
        case e: PSQLException => Left(StatusCode.BadGateway -> ErrorResponse(s"Internal server error with error = $e"))
      }

}

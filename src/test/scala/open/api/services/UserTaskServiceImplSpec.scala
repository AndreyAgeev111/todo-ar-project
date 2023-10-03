package open.api.services

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.softwaremill.macwire.wire
import doobie.ConnectionIO
import doobie.free.connection
import open.api.errors.ErrorResponse
import open.api.models.TaskStatuses
import open.api.models.requests.UserTaskCreateRequest
import open.api.models.responses.UserTaskCreateResponse
import open.api.persistent.dto.UserTaskDto
import open.api.testutils.mocks.DefaultMocks
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.postgresql.util.PSQLException
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import sttp.model.StatusCode

import java.time.Instant

class UserTaskServiceImplSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with UserTaskServiceUtils {
  "listUserTasks" - {
    "return all user's task by login" in {
      when(mockUserTaskRepository.listUserTasks(login)).thenReturn(IO.pure(listUserTasks))

      userTaskService.listUserTasks(login).asserting(_ shouldBe Right(StatusCode.Ok -> listUserTasks.map(UserTaskDto.toTaskResponse)))
    }
    "throw error if problem with db connection" in {
      when(mockUserTaskRepository.listUserTasks(login)).thenReturn(IO.raiseError(new PSQLException("error", any)))

      userTaskService.listUserTasks(login).asserting(_ shouldBe Left(StatusCode.BadGateway -> ErrorResponse(s"Internal server error with error = org.postgresql.util.PSQLException: error")))
    }
  }

  "addUserTasks" - {
    "add user's task" in {
      when(mockUserTaskRepository.addUserTask(addTaskRequest, login)).thenReturn(IO.unit)

      userTaskService.addUserTask(addTaskRequest, login).asserting(_ shouldBe Right(StatusCode.Ok -> UserTaskCreateResponse()))
    }
    "not create user, if it is existing" in {
      when(mockUserTaskRepository.addUserTask(addTaskRequest, login)).thenReturn(IO.raiseError(new PSQLException(s"(user_login)=($login)", any)))

      userTaskService.addUserTask(addTaskRequest, login).asserting(_ shouldBe Left(StatusCode.BadRequest -> ErrorResponse(s"Invalid request - user with login = $login is unknown")))
    }
  }
}

trait UserTaskServiceUtils extends DefaultMocks {
  val userTaskService: UserTaskService[IO] = wire[UserTaskServiceImpl]

  val login: String = "login"
  val createdAt: Instant = Instant.now()
  val deadline: Instant = Instant.MAX
  val name: String = "name"
  val desc: Option[String] = Some("desc")
  val listUserTasks: List[UserTaskDto] = List(
    UserTaskDto(
      id = "1",
      userLogin = login,
      name = name,
      description = desc,
      createdAt = createdAt,
      deadline = deadline,
      status = TaskStatuses.ToDo
    )
  )
  val addTaskRequest: UserTaskCreateRequest = UserTaskCreateRequest(
    name = name,
    description = desc,
    deadline = deadline,
    status = TaskStatuses.ToDo
  )
  val listUserTaskDao: ConnectionIO[List[UserTaskDto]] = connection.pure(
    listUserTasks
  )
  val addTaskRequestDao: ConnectionIO[Int] = connection.pure(1)
}

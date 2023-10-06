package open.api.services

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.softwaremill.macwire.wire
import doobie.ConnectionIO
import doobie.free.connection
import open.api.errors.ErrorResponse
import open.api.models.TaskStatuses
import open.api.models.requests.{FilterRequest, UserTaskCreateRequest}
import open.api.models.responses.SuccessResponse
import open.api.persistent.dto.UserTaskDto
import open.api.persistent.errors.NotFoundTaskError
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

      userTaskService
        .listUserTasks(login)
        .asserting(
          _ shouldBe Left(
            StatusCode.BadGateway -> ErrorResponse(s"Internal server error with error = org.postgresql.util.PSQLException: error")
          )
        )
    }
  }

  "addUserTasks" - {
    "add user's task" in {
      when(mockUserTaskRepository.addUserTask(addTaskRequest, login)).thenReturn(IO.unit)

      userTaskService
        .addUserTask(addTaskRequest, login)
        .asserting(_ shouldBe Right(StatusCode.Ok -> SuccessResponse("Task added or update successfully")))
    }
    "not create user, if it is existing" in {
      when(mockUserTaskRepository.addUserTask(addTaskRequest, login))
        .thenReturn(IO.raiseError(new PSQLException(s"(user_login)=($login)", any, any)))

      userTaskService
        .addUserTask(addTaskRequest, login)
        .asserting(_ shouldBe Left(StatusCode.BadRequest -> ErrorResponse(s"Invalid request - user with login = $login is unknown")))
    }
  }

  "findUserTask" - {
    "find user task by id, if it is existing" in {
      when(mockUserTaskRepository.findUserTaskById(login, taskId)).thenReturn(IO.pure(Some(listUserTasks.head)))

      userTaskService
        .findUserTask(login, taskId)
        .asserting(_ shouldBe Right(StatusCode.Ok -> UserTaskDto.toTaskResponse(listUserTasks.head)))
    }
    "not found, if task isn't existing" in {
      when(mockUserTaskRepository.findUserTaskById(login, taskId)).thenReturn(IO.pure(None))

      userTaskService
        .findUserTask(login, taskId)
        .asserting(
          _ shouldBe Left(StatusCode.BadRequest -> ErrorResponse(new NotFoundTaskError(taskId).message))
        )
    }
  }

  "updateUserTask" - {
    "update user's task, if it is existing" in {
      when(mockUserTaskRepository.updateUserTask(addTaskRequest, taskId, login)).thenReturn(IO.unit)

      userTaskService
        .updateUserTask(addTaskRequest, taskId, login)
        .asserting(_ shouldBe Right(StatusCode.Ok -> SuccessResponse("Task added or update successfully")))
    }
    "not update, if it isn't existing" in {
      when(mockUserTaskRepository.updateUserTask(addTaskRequest, taskId, login)).thenReturn(IO.raiseError(new NotFoundTaskError(taskId)))

      userTaskService
        .updateUserTask(addTaskRequest, taskId, login)
        .asserting(_ shouldBe Left(StatusCode.BadRequest -> ErrorResponse(s"Not found task with taskId = $taskId")))
    }
  }

  "deleteUserTask" - {
    "delete user's task, if it is existing" in {
      when(mockUserTaskRepository.deleteUserTask(login, taskId)).thenReturn(IO.unit)

      userTaskService
        .deleteUserTask(login, taskId)
        .asserting(_ shouldBe Right(StatusCode.Ok -> SuccessResponse(s"Task with id = $taskId was successfully deleted")))
    }
    "not delete, if it isn't existing" in {
      when(mockUserTaskRepository.deleteUserTask(login, taskId)).thenReturn(IO.raiseError(new NotFoundTaskError(taskId)))

      userTaskService
        .deleteUserTask(login, taskId)
        .asserting(_ shouldBe Left(StatusCode.BadRequest -> ErrorResponse(s"Not found task with taskId = $taskId")))
    }
  }

  "updateUserTaskStatus" - {
    "update user's task status, if it is existing" in {
      when(mockUserTaskRepository.updateUserTaskStatus(login, taskId, TaskStatuses.ToDo)).thenReturn(IO.unit)

      userTaskService
        .updateUserTaskStatus(login, taskId, TaskStatuses.ToDo)
        .asserting(
          _ shouldBe Right(
            StatusCode.Ok -> SuccessResponse(s"Task's status with id = $taskId was successfully updated with status = ${TaskStatuses.ToDo}")
          )
        )
    }
    "not update, if it isn't existing" in {
      when(mockUserTaskRepository.updateUserTaskStatus(login, taskId, TaskStatuses.ToDo))
        .thenReturn(IO.raiseError(new NotFoundTaskError(taskId)))

      userTaskService
        .updateUserTaskStatus(login, taskId, TaskStatuses.ToDo)
        .asserting(_ shouldBe Left(StatusCode.BadRequest -> ErrorResponse(s"Not found task with taskId = $taskId")))
    }
  }

  "listUserTasksWithFilter" - {
    "return all user's task by login with filter" in {
      when(mockUserTaskRepository.listUserTasksWithFilter(login, filterRequest)).thenReturn(IO.pure(listUserTasks))

      userTaskService
        .listUserTasksWithFilter(login, filterRequest)
        .asserting(_ shouldBe Right(StatusCode.Ok -> listUserTasks.map(UserTaskDto.toTaskResponse)))
    }
    "throw error if problem with db connection" in {
      when(mockUserTaskRepository.listUserTasksWithFilter(login, filterRequest))
        .thenReturn(IO.raiseError(new PSQLException("error", any, any)))

      userTaskService
        .listUserTasksWithFilter(login, filterRequest)
        .asserting(
          _ shouldBe Left(
            StatusCode.BadGateway -> ErrorResponse(s"Internal server error with error = org.postgresql.util.PSQLException: error")
          )
        )
    }
  }
  "throw bad request, if request is invalidated" in {
    userTaskService
      .listUserTasksWithFilter(login, badFilterRequest)
      .asserting(
        _ shouldBe Left(StatusCode.BadRequest -> ErrorResponse(s"Bad request, createdAfter > createdBefore"))
      )
  }
}

trait UserTaskServiceUtils extends DefaultMocks {
  val userTaskService: UserTaskService[IO] = wire[UserTaskServiceImpl]

  val login: String = "login"
  val createdAt: Instant = Instant.now()
  val deadline: Instant = Instant.MAX
  val name: String = "name"
  val desc: Option[String] = Some("desc")
  val taskId: String = "1"
  val listUserTasks: List[UserTaskDto] = List(
    UserTaskDto(
      id = taskId,
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
  val filterRequest: FilterRequest = FilterRequest(
    name = None,
    description = None,
    createdAfter = None,
    createdBefore = None,
    deadlineAfter = None,
    deadlineBefore = None,
    statuses = List(TaskStatuses.ToDo)
  )
  val badFilterRequest: FilterRequest = FilterRequest(
    name = None,
    description = None,
    createdAfter = Some(Instant.MAX),
    createdBefore = Some(Instant.now()),
    deadlineAfter = None,
    deadlineBefore = None,
    statuses = List(TaskStatuses.ToDo)
  )
}

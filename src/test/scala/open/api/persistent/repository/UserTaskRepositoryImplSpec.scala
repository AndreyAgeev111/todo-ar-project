package open.api.persistent.repository

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.softwaremill.macwire.wire
import doobie.ConnectionIO
import doobie.free.connection
import open.api.models.TaskStatuses
import open.api.models.requests.UserTaskCreateRequest
import open.api.persistent.dto.UserTaskDto
import open.api.testutils.mocks.DefaultMocks
import org.mockito.Mockito.when
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

import java.time.Instant

class UserTaskRepositoryImplSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with UserTaskRepositoryUtils {
  "listUserTasks" - {
    "return all user's task by login" in {
      when(mockUserTaskDao.listUserTasks(login)).thenReturn(listUserTaskDao)

      userTaskRepository.listUserTasks(login).asserting(_ shouldBe listUserTasks)
    }
  }

  "addUserTasks" - {
    "add user's task" in {
      when(mockUserTaskDao.addUserTask(addTaskRequest, login)).thenReturn(addTaskRequestDao)

      userTaskRepository.addUserTask(addTaskRequest, login).asserting(_ shouldBe ())
    }
  }
}

trait UserTaskRepositoryUtils extends DefaultMocks {
  val userTaskRepository: UserTaskRepository[IO] = wire[UserTaskRepositoryImpl]

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

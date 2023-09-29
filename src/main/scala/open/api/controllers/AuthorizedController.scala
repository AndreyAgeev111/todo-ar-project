package open.api.controllers

import cats.effect.IO
import open.api.controllers.AuthorizedController.TAG
import open.api.models.{TaskStatuses, UserTask}
import open.api.persistent.dao.UserTaskDaoImpl
import open.api.persistent.repository.UserTaskRepositoryImpl
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.{PublicEndpoint, endpoint, query}

import java.time.Instant

trait AuthorizedController {
  private val userTaskRepository = new UserTaskRepositoryImpl(new UserTaskDaoImpl)

  private val userTasksAdd: PublicEndpoint[UserTask, Unit, String, Any] = endpoint
    .post
    .description("Add one user task")
    .tag(TAG)
    .in("tasks")
    .in(jsonBody[UserTask])
    .out(jsonBody[String])
  private val userTasksAddServerEndpoint: ServerEndpoint[Any, IO] = userTasksAdd.serverLogicSuccess(userTask => IO.pure(s"Your task is ${userTask.name}"))

  private val getUserTasks: PublicEndpoint[String, Unit, List[UserTask], Any] = endpoint
    .get
    .description("Get one user task")
    .tag(TAG)
    .in("tasks")
    .in(query[String]("login"))
    .out(jsonBody[List[UserTask]])
  private val getUserTasksServerEndpoint: ServerEndpoint[Any, IO] = getUserTasks.serverLogicSuccess(login => userTaskRepository.listUserTasks(login))

  // TODO
  // POST updateTask
  // DELETE deleteTask
  // POST updatePassword
  // POST updateStatus
  // GET filterTasks
  val authorizedApiEndpoints: List[ServerEndpoint[Any, IO]] = List(userTasksAddServerEndpoint, getUserTasksServerEndpoint)
}

object AuthorizedController {
  private val TAG = "Authorized API"
}

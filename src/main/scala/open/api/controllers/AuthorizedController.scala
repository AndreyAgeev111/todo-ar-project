package open.api.controllers

import open.api.controllers.AuthorizedController.TAG
import open.api.models.requests.UserTaskRequest
import open.api.models.responses.UserTaskResponse
import open.api.persistent.repository.UserTaskRepository
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.{PublicEndpoint, endpoint, query}


class AuthorizedController[F[_]](userTaskRepository: UserTaskRepository[F]) {
  private val userTasksAdd: PublicEndpoint[(String, UserTaskRequest), Unit, Int, Any] = endpoint
    .post
    .description("Add one user task")
    .tag(TAG)
    .in("tasks")
    .in(query[String]("login"))
    .in(jsonBody[UserTaskRequest])
    .out(jsonBody[Int])
  private val userTasksAddServerEndpoint: ServerEndpoint[Any, F] = userTasksAdd.serverLogicSuccess{
    case (userLogin, userTask) => userTaskRepository.addUserTask(userTask, userLogin)
  }

  private val getUserTasks: PublicEndpoint[String, Unit, List[UserTaskResponse], Any] = endpoint
    .get
    .description("Get one user task")
    .tag(TAG)
    .in("tasks")
    .in(query[String]("login"))
    .out(jsonBody[List[UserTaskResponse]])
  private val getUserTasksServerEndpoint: ServerEndpoint[Any, F] = getUserTasks.serverLogicSuccess(login => userTaskRepository.listUserTasks(login))

  // TODO
  // POST updateTask
  // DELETE deleteTask
  // POST updatePassword
  // POST updateStatus
  // GET filterTasks
  def authorizedApiEndpoints: List[ServerEndpoint[Any, F]] = List(userTasksAddServerEndpoint, getUserTasksServerEndpoint)
}

object AuthorizedController {
  private val TAG = "Authorized API"
}

package open.api.controllers

import open.api.controllers.AuthorizedController.TAG
import open.api.errors.ErrorResponse
import open.api.models.requests.UserTaskCreateRequest
import open.api.models.responses.{UserTaskCreateResponse, UserTaskResponse}
import open.api.services.UserTaskService
import sttp.model.StatusCode
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.{PublicEndpoint, endpoint, query, statusCode}


class AuthorizedController[F[_]](userTaskService: UserTaskService[F]) {
  private val userTasksAdd: PublicEndpoint[(String, UserTaskCreateRequest), (StatusCode, ErrorResponse), (StatusCode, UserTaskCreateResponse), Any] = endpoint
    .post
    .description("Add one user task")
    .tag(TAG)
    .in("tasks")
    .in(query[String]("login"))
    .in(jsonBody[UserTaskCreateRequest])
    .errorOut(statusCode)
    .errorOut(jsonBody[ErrorResponse])
    .out(statusCode)
    .out(jsonBody[UserTaskCreateResponse])

  private val userTasksAddServerEndpoint: ServerEndpoint[Any, F] = userTasksAdd.serverLogic {
    case (userLogin, userTask) => userTaskService.addUserTask(userTask, userLogin)
  }

  private val getUserTasks: PublicEndpoint[String, (StatusCode, ErrorResponse), (StatusCode, List[UserTaskResponse]), Any] = endpoint
    .get
    .description("Get one user task")
    .tag(TAG)
    .in("tasks")
    .in(query[String]("login"))
    .errorOut(statusCode)
    .errorOut(jsonBody[ErrorResponse])
    .out(statusCode)
    .out(jsonBody[List[UserTaskResponse]])

  private val getUserTasksServerEndpoint: ServerEndpoint[Any, F] = getUserTasks.serverLogic(login => userTaskService.listUserTasks(login))

  // TODO
  // POST updateTask
  // DELETE deleteTask
  // POST updatePassword
  // POST updateStatus
  // POST filterTasks
  def authorizedApiEndpoints: List[ServerEndpoint[Any, F]] = List(userTasksAddServerEndpoint, getUserTasksServerEndpoint)
}

object AuthorizedController {
  private val TAG = "Authorized API"
}

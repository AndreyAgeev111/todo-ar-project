package open.api.controllers

import open.api.controllers.AuthorizedController.TAG
import open.api.errors.ErrorResponse
import open.api.models.requests.UserTaskCreateRequest
import open.api.models.responses.{UserLoginCredentialsResponse, UserTaskCreateResponse, UserTaskResponse}
import open.api.services.{UserTaskService, UsersService}
import sttp.model.StatusCode
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.model.UsernamePassword
import sttp.tapir.server.{PartialServerEndpoint, ServerEndpoint}
import sttp.tapir.{Endpoint, PublicEndpoint, auth, endpoint, query, statusCode}


class AuthorizedController[F[_]](userTaskService: UserTaskService[F],
                                 usersService: UsersService[F]) {
  private val securityEndpoint: PartialServerEndpoint[UsernamePassword, (StatusCode, UserLoginCredentialsResponse), Unit, (StatusCode, ErrorResponse), Unit, Any, F] = endpoint
    .securityIn(auth.basic[UsernamePassword]())
    .errorOut(statusCode)
    .errorOut(jsonBody[ErrorResponse])
    .serverSecurityLogic {
      user => usersService.checkUserPassword(user.username, user.password)
    }

  private val userTasksAdd: PartialServerEndpoint[UsernamePassword, (StatusCode, UserLoginCredentialsResponse), (String, UserTaskCreateRequest), (StatusCode, ErrorResponse), (StatusCode, UserTaskCreateResponse), Any, F] = securityEndpoint
    .post
    .description("Add one user task")
    .tag(TAG)
    .in("tasks")
    .in(query[String]("login"))
    .in(jsonBody[UserTaskCreateRequest])
    .out(statusCode)
    .out(jsonBody[UserTaskCreateResponse])

  private val userTasksAddServerEndpoint: ServerEndpoint[Any, F] = userTasksAdd.serverLogic { _ => {
    case (userLogin, task) => userTaskService.addUserTask(task, userLogin)
  }
  }

  private val getUserTasks: PartialServerEndpoint[UsernamePassword, (StatusCode, UserLoginCredentialsResponse), Unit, (StatusCode, ErrorResponse), (StatusCode, List[UserTaskResponse]), Any, F] = securityEndpoint
    .get
    .description("Get one user task")
    .tag(TAG)
    .in("tasks")
    .out(statusCode)
    .out(jsonBody[List[UserTaskResponse]])

  private val getUserTasksServerEndpoint: ServerEndpoint[Any, F] = getUserTasks.serverLogic(user => _ => userTaskService.listUserTasks(user._2.login))

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

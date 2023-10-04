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
import sttp.tapir.{auth, endpoint, statusCode, path}

class AuthorizedController[F[_]](userTaskService: UserTaskService[F], usersService: UsersService[F]) {
  private val securityEndpoint: PartialServerEndpoint[
    UsernamePassword,
    (StatusCode, UserLoginCredentialsResponse),
    Unit,
    (StatusCode, ErrorResponse),
    Unit,
    Any,
    F
  ] = endpoint
    .securityIn(auth.basic[UsernamePassword]())
    .errorOut(statusCode)
    .errorOut(jsonBody[ErrorResponse])
    .serverSecurityLogic { user =>
      usersService.checkUserPassword(user.username, user.password)
    }

  private val userTasksAdd: PartialServerEndpoint[
    UsernamePassword,
    (StatusCode, UserLoginCredentialsResponse),
    UserTaskCreateRequest,
    (StatusCode, ErrorResponse),
    (StatusCode, UserTaskCreateResponse),
    Any,
    F
  ] = securityEndpoint.post
    .description("Add one user task")
    .tag(TAG)
    .in("tasks")
    .in(jsonBody[UserTaskCreateRequest])
    .out(statusCode)
    .out(jsonBody[UserTaskCreateResponse])

  private val userTasksAddServerEndpoint: ServerEndpoint[Any, F] = userTasksAdd.serverLogic { case (_, user) =>
    task => userTaskService.addUserTask(task, user.login)
  }

  private val getUserTasks: PartialServerEndpoint[
    UsernamePassword,
    (StatusCode, UserLoginCredentialsResponse),
    Unit,
    (StatusCode, ErrorResponse),
    (StatusCode, List[UserTaskResponse]),
    Any,
    F
  ] = securityEndpoint.get
    .description("Get user's tasks")
    .tag(TAG)
    .in("tasks")
    .out(statusCode)
    .out(jsonBody[List[UserTaskResponse]])

  private val getUserTasksServerEndpoint: ServerEndpoint[Any, F] = getUserTasks.serverLogic { case (_, user) =>
    _ => userTaskService.listUserTasks(user.login)
  }

  private val updateUserTask: PartialServerEndpoint[
    UsernamePassword,
    (StatusCode, UserLoginCredentialsResponse),
    (String, UserTaskCreateRequest),
    (StatusCode, ErrorResponse),
    (StatusCode, UserTaskCreateResponse),
    Any,
    F
  ] = securityEndpoint.patch
    .description("Update user's task by id")
    .tag(TAG)
    .in("tasks")
    .in(path[String]("taskId"))
    .in(jsonBody[UserTaskCreateRequest])
    .out(statusCode)
    .out(jsonBody[UserTaskCreateResponse])

  private val updateUserTaskServerEndpoint: ServerEndpoint[Any, F] = updateUserTask.serverLogic {
    case (_, userLogin) => { case (taskId, task) =>
      userTaskService.updateUserTask(task, taskId, userLogin.login)
    }
  }

  private val getUserTask: PartialServerEndpoint[
    UsernamePassword,
    (StatusCode, UserLoginCredentialsResponse),
    String,
    (StatusCode, ErrorResponse),
    (StatusCode, UserTaskResponse),
    Any,
    F
  ] = securityEndpoint.get
    .description("Get one user task by taskId")
    .tag(TAG)
    .in("tasks")
    .in(path[String]("taskId"))
    .out(statusCode)
    .out(jsonBody[UserTaskResponse])

  private val getUserTaskServerEndpoint: ServerEndpoint[Any, F] = getUserTask.serverLogic { _ => taskId =>
    userTaskService.findUserTaskById(taskId)
  }

  // TODO
  // DELETE deleteTask
  // POST updatePassword
  // POST updateStatus
  // POST filterTasks
  def authorizedApiEndpoints: List[ServerEndpoint[Any, F]] =
    List(userTasksAddServerEndpoint, getUserTasksServerEndpoint, updateUserTaskServerEndpoint, getUserTaskServerEndpoint)
}

object AuthorizedController {
  private val TAG = "Authorized API"
}

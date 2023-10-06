package open.api.controllers

import open.api.controllers.AuthorizedController.TAG
import open.api.errors.ErrorResponse
import open.api.models.TaskStatuses.TaskStatus
import open.api.models.requests.{FilterRequest, UserLoginCredentialsRequest, UserTaskCreateRequest}
import open.api.models.responses.{SuccessResponse, UserLoginCredentialsResponse, UserTaskResponse}
import open.api.services.{UserTaskService, UsersService}
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.model.UsernamePassword
import sttp.tapir.server.{PartialServerEndpoint, ServerEndpoint}
import sttp.tapir.{auth, endpoint, path, statusCode}

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
    (StatusCode, SuccessResponse),
    Any,
    F
  ] = securityEndpoint.post
    .description("Add one user task")
    .tag(TAG)
    .in("tasks")
    .in(jsonBody[UserTaskCreateRequest])
    .out(statusCode)
    .out(jsonBody[SuccessResponse])

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
    (StatusCode, SuccessResponse),
    Any,
    F
  ] = securityEndpoint.patch
    .description("Update user's task by id")
    .tag(TAG)
    .in("tasks")
    .in(path[String]("taskId"))
    .in(jsonBody[UserTaskCreateRequest])
    .out(statusCode)
    .out(jsonBody[SuccessResponse])

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

  private val getUserTaskServerEndpoint: ServerEndpoint[Any, F] = getUserTask.serverLogic { case (_, userLogin) =>
    taskId => userTaskService.findUserTask(userLogin.login, taskId)
  }

  private val deleteUserTask: PartialServerEndpoint[
    UsernamePassword,
    (StatusCode, UserLoginCredentialsResponse),
    String,
    (StatusCode, ErrorResponse),
    (StatusCode, SuccessResponse),
    Any,
    F
  ] = securityEndpoint.delete
    .description("Delete one user task by taskId")
    .tag(TAG)
    .in("tasks")
    .in(path[String]("taskId"))
    .out(statusCode)
    .out(jsonBody[SuccessResponse])

  private val deleteUserTaskServerEndpoint: ServerEndpoint[Any, F] = deleteUserTask.serverLogic { case (_, userLogin) =>
    taskId => userTaskService.deleteUserTask(userLogin.login, taskId)
  }

  private val updateUserPassword: PartialServerEndpoint[
    UsernamePassword,
    (StatusCode, UserLoginCredentialsResponse),
    UserLoginCredentialsRequest,
    (StatusCode, ErrorResponse),
    (StatusCode, SuccessResponse),
    Any,
    F
  ] = securityEndpoint.post
    .description("Update user's password")
    .tag(TAG)
    .in("login" / "password")
    .in(jsonBody[UserLoginCredentialsRequest])
    .out(statusCode)
    .out(jsonBody[SuccessResponse])

  private val updateUserPasswordServerEndpoint: ServerEndpoint[Any, F] = updateUserPassword.serverLogic { _ => userCredentials =>
    usersService.updateUserPassword(userCredentials)
  }

  private val updateUserTaskStatus: PartialServerEndpoint[
    UsernamePassword,
    (StatusCode, UserLoginCredentialsResponse),
    (String, TaskStatus),
    (StatusCode, ErrorResponse),
    (StatusCode, SuccessResponse),
    Any,
    F
  ] = securityEndpoint.post
    .description("Update user's task status")
    .tag(TAG)
    .in("tasks" / "status")
    .in(query[String]("taskId"))
    .in(query[TaskStatus]("status"))
    .out(statusCode)
    .out(jsonBody[SuccessResponse])

  private val updateUserTaskStatusServerEndpoint: ServerEndpoint[Any, F] = updateUserTaskStatus.serverLogic {
    case (_, userLogin) => { case (taskId, status) =>
      userTaskService.updateUserTaskStatus(userLogin.login, taskId, status)
    }
  }

  private val getUserTasksWithFilter: PartialServerEndpoint[
    UsernamePassword,
    (StatusCode, UserLoginCredentialsResponse),
    FilterRequest,
    (StatusCode, ErrorResponse),
    (StatusCode, List[UserTaskResponse]),
    Any,
    F
  ] = securityEndpoint.post
    .description("Get user's tasks with filter")
    .tag(TAG)
    .in("tasks" / "filter")
    .in(jsonBody[FilterRequest])
    .out(statusCode)
    .out(jsonBody[List[UserTaskResponse]])

  private val getUserTasksWithFilterServerEndpoint: ServerEndpoint[Any, F] = getUserTasksWithFilter.serverLogic { case (_, user) =>
    filter => userTaskService.listUserTasksWithFilter(user.login, filter)
  }

  def authorizedApiEndpoints: List[ServerEndpoint[Any, F]] =
    List(
      userTasksAddServerEndpoint,
      getUserTasksServerEndpoint,
      updateUserTaskServerEndpoint,
      getUserTaskServerEndpoint,
      deleteUserTaskServerEndpoint,
      updateUserPasswordServerEndpoint,
      updateUserTaskStatusServerEndpoint,
      getUserTasksWithFilterServerEndpoint
    )
}

object AuthorizedController {
  private val TAG = "Authorized API"
}

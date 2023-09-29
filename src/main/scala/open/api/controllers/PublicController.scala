package open.api.controllers

import cats.effect.IO
import open.api.controllers.PublicController.TAG
import open.api.models.{User, UserLoginForm}
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.{Endpoint, endpoint}

trait PublicController {
  private val userLogin: Endpoint[Unit, UserLoginForm, Unit, String, Any] = endpoint
    .post
    .description("Login with credentials")
    .tag(TAG)
    .in("login")
    .in(jsonBody[UserLoginForm])
    .out(jsonBody[String])

  private val userTasksAddServerEndpoint: ServerEndpoint[Any, IO] = userLogin.serverLogicSuccess(userLogin => IO.pure(s"Successfully login with login=${userLogin.login}"))

  private val userSignUp: Endpoint[Unit, User, Unit, String, Any] = endpoint
    .post
    .description("Signup with user's credentials and some optional data")
    .tag(TAG)
    .in("signup")
    .in(jsonBody[User])
    .out(jsonBody[String])

  private val userSignUpServerEndpoint: ServerEndpoint[Any, IO] = userSignUp.serverLogicSuccess(user => IO.pure(s"Successfully signup with login=${user.login} and password=${user.password}"))

  val publicApiEndpoints: List[ServerEndpoint[Any, IO]] = List(userTasksAddServerEndpoint, userSignUpServerEndpoint)
}

object PublicController {
  private val TAG = "Public API"
}

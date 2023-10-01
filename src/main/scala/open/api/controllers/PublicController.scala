package open.api.controllers

import cats.effect.IO
import open.api.controllers.PublicController.TAG
import open.api.models.{User, UserLoginForm}
import open.api.persistent.repository.UsersRepository
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.{Endpoint, endpoint}

class PublicController[F[_]](usersRepository: UsersRepository[F]) {
  private val userLogin: Endpoint[Unit, UserLoginForm, Unit, Int, Any] = endpoint
    .post
    .description("Login with credentials")
    .tag(TAG)
    .in("login")
    .in(jsonBody[UserLoginForm])
    .out(jsonBody[Int])

  private val userTasksAddServerEndpoint: ServerEndpoint[Any, F] = userLogin.serverLogicSuccess(userLogin => usersRepository.addUser(userLogin))

  /*private val userSignUp: Endpoint[Unit, User, Unit, String, Any] = endpoint
    .post
    .description("Signup with user's credentials and some optional data")
    .tag(TAG)
    .in("signup")
    .in(jsonBody[User])
    .out(jsonBody[String])

  private val userSignUpServerEndpoint: ServerEndpoint[Any, IO] = userSignUp.serverLogicSuccess(user => IO.pure(s"Successfully signup with login=${user.login}"))*/

  val publicApiEndpoints: List[ServerEndpoint[Any, F]] = List(userTasksAddServerEndpoint)
}

object PublicController {
  private val TAG = "Public API"
}

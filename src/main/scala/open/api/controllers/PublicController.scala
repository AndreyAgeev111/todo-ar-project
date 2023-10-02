package open.api.controllers

import open.api.controllers.PublicController.TAG
import open.api.models.requests.UserRegisterRequest
import open.api.persistent.repository.UsersRepository
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.{Endpoint, endpoint}

class PublicController[F[_]](usersRepository: UsersRepository[F]) {
  /*private val userLogin: Endpoint[Unit, UserLoginCredentials, Unit, Int, Any] = endpoint
    .post
    .description("Login with credentials")
    .tag(TAG)
    .in("login")
    .in(jsonBody[UserLoginCredentials])
    .out(jsonBody[Int])

  private val userTasksAddServerEndpoint: ServerEndpoint[Any, F] = userLogin.serverLogicSuccess(userLogin => usersRepository.addUser(userLogin))*/

  private val userSignUp: Endpoint[Unit, UserRegisterRequest, Unit, Unit, Any] = endpoint
    .post
    .description("Sign up with user's credentials and some optional data")
    .tag(TAG)
    .in("sign-up")
    .in(jsonBody[UserRegisterRequest])
    .out(jsonBody[Unit])

  private val userSignUpServerEndpoint: ServerEndpoint[Any, F] = userSignUp.serverLogicSuccess(user => usersRepository.registerUser(user))

  val publicApiEndpoints: List[ServerEndpoint[Any, F]] = List(userSignUpServerEndpoint)
}

object PublicController {
  private val TAG = "Public API"
}

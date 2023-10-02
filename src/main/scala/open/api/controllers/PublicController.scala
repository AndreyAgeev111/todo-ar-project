package open.api.controllers

import open.api.controllers.PublicController.TAG
import open.api.errors.ErrorResponse
import open.api.models.requests.{UserLoginCredentialsRequest, UserRegisterRequest}
import open.api.persistent.repository.UsersRepository
import open.api.services.UsersService
import sttp.model.StatusCode
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.{Endpoint, endpoint, statusCode}

class PublicController[F[_]](usersRepository: UsersRepository[F],
                             usersService: UsersService[F]) {
  private val userLogin: Endpoint[Unit, UserLoginCredentialsRequest, (StatusCode, ErrorResponse), (StatusCode, Boolean), Any] = endpoint
    .post
    .description("Login with credentials")
    .tag(TAG)
    .in("login")
    .in(jsonBody[UserLoginCredentialsRequest])
    .errorOut(statusCode)
    .errorOut(jsonBody[ErrorResponse])
    .out(statusCode)
    .out(jsonBody[Boolean])

  private val userLoginServerEndpoint: ServerEndpoint[Any, F] = userLogin.serverLogic(userLoginCredentials => usersService.checkUserPassword(userLoginCredentials.login, userLoginCredentials.password))

  private val userSignUp: Endpoint[Unit, UserRegisterRequest, Unit, Unit, Any] = endpoint
    .post
    .description("Sign up with user's credentials and some optional data")
    .tag(TAG)
    .in("sign-up")
    .in(jsonBody[UserRegisterRequest])
    .out(jsonBody[Unit])

  private val userSignUpServerEndpoint: ServerEndpoint[Any, F] = userSignUp.serverLogicSuccess(user => usersRepository.registerUser(user))

  val publicApiEndpoints: List[ServerEndpoint[Any, F]] = List(userSignUpServerEndpoint, userLoginServerEndpoint)
}

object PublicController {
  private val TAG = "Public API"
}

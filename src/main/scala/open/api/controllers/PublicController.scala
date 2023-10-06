package open.api.controllers

import cats.implicits.catsSyntaxOptionId
import open.api.controllers.PublicController.TAG
import open.api.errors.ErrorResponse
import open.api.models.requests.{UserLoginCredentialsRequest, UserRegisterRequest}
import open.api.models.responses.{SuccessResponse, UserLoginCredentialsResponse}
import open.api.services.UsersService
import sttp.model.StatusCode
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.{Endpoint, endpoint, statusCode}

class PublicController[F[_]](usersService: UsersService[F]) {
  private val userLogin
      : Endpoint[Unit, UserLoginCredentialsRequest, (StatusCode, ErrorResponse), (StatusCode, UserLoginCredentialsResponse), Any] =
    endpoint.post
      .description("Login with credentials")
      .tag(TAG)
      .in("login")
      .in(jsonBody[UserLoginCredentialsRequest])
      .errorOut(statusCode)
      .errorOut(jsonBody[ErrorResponse])
      .out(statusCode)
      .out(jsonBody[UserLoginCredentialsResponse])

  private val userLoginServerEndpoint: ServerEndpoint[Any, F] = userLogin.serverLogic(userLoginCredentials =>
    usersService.checkUserPassword(userLoginCredentials.login, userLoginCredentials.password.some)
  )

  private val userSignUp: Endpoint[Unit, UserRegisterRequest, (StatusCode, ErrorResponse), (StatusCode, SuccessResponse), Any] =
    endpoint.post
      .description("Sign up with user's credentials and some optional data")
      .tag(TAG)
      .in("sign-up")
      .in(jsonBody[UserRegisterRequest])
      .errorOut(statusCode)
      .errorOut(jsonBody[ErrorResponse])
      .out(statusCode)
      .out(jsonBody[SuccessResponse])

  private val userSignUpServerEndpoint: ServerEndpoint[Any, F] = userSignUp.serverLogic(user => usersService.createUser(user))

  val publicApiEndpoints: List[ServerEndpoint[Any, F]] = List(userSignUpServerEndpoint, userLoginServerEndpoint)
}

object PublicController {
  private val TAG = "Public API"
}

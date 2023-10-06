package open.api.services

import cats.effect.IO
import open.api.errors.ErrorResponse
import open.api.models.requests.{UserLoginCredentialsRequest, UserRegisterRequest}
import open.api.models.responses.{SuccessResponse, UserLoginCredentialsResponse}
import open.api.persistent.repository.UsersRepositoryImpl
import org.postgresql.util.PSQLException
import sttp.model.StatusCode

trait UsersService[F[_]] {
  def checkUserPassword(
      userLogin: String,
      pass: Option[String]
  ): F[Either[(StatusCode, ErrorResponse), (StatusCode, UserLoginCredentialsResponse)]]
  def createUser(user: UserRegisterRequest): F[Either[(StatusCode, ErrorResponse), (StatusCode, SuccessResponse)]]
  def updateUserPassword(
      userCredentials: UserLoginCredentialsRequest
  ): F[Either[(StatusCode, ErrorResponse), (StatusCode, SuccessResponse)]]
}

class UsersServiceImpl(usersRepository: UsersRepositoryImpl) extends UsersService[IO] {
  override def checkUserPassword(
      userLogin: String,
      password: Option[String]
  ): IO[Either[(StatusCode, ErrorResponse), (StatusCode, UserLoginCredentialsResponse)]] = {
    password match {
      case Some(pass) =>
        usersRepository.findUserPassword(userLogin).map {
          case Some(truePass) if truePass == pass => Right(StatusCode.Ok -> UserLoginCredentialsResponse(login = userLogin))
          case Some(_)                            => Left(StatusCode.BadRequest -> ErrorResponse("Invalid login credentials"))
          case None                               => Left(StatusCode.BadRequest -> ErrorResponse("Login was not found"))
        }
      case None => IO.pure(Left(StatusCode.BadRequest -> ErrorResponse("Invalid login credentials")))
    }
  }

  override def createUser(user: UserRegisterRequest): IO[Either[(StatusCode, ErrorResponse), (StatusCode, SuccessResponse)]] =
    usersRepository
      .registerUser(user)
      .map(_ => Right(StatusCode.Ok -> SuccessResponse(message = s"Account with login = ${user.login} was successfully created")))
      .recover {
        case e: PSQLException if e.getMessage.contains("users_credentials_pkey") =>
          Left(StatusCode.BadRequest -> ErrorResponse(s"Invalid request - user with login = ${user.login} is already existed"))
      }

  override def updateUserPassword(
      userCredentials: UserLoginCredentialsRequest
  ): IO[Either[(StatusCode, ErrorResponse), (StatusCode, SuccessResponse)]] =
    usersRepository
      .updateUserPassword(userCredentials)
      .map(_ =>
        Right(StatusCode.Ok -> SuccessResponse(message = s"Password with login = ${userCredentials.login} was successfully updated"))
      )
      .recover { case e: PSQLException =>
        Left(StatusCode.BadGateway -> ErrorResponse(s"Internal server error with error = $e"))
      }
}

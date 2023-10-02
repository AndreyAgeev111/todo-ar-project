package open.api.services

import cats.effect.IO
import open.api.errors.ErrorResponse
import open.api.models.requests.UserRegisterRequest
import open.api.models.responses.{UserLoginCredentialsResponse, UserRegisterResponse}
import open.api.persistent.repository.UsersRepositoryImpl
import org.postgresql.util.PSQLException
import sttp.model.StatusCode

trait UsersService[F[_]] {
  def checkUserPassword(userLogin: String, pass: Option[String]): F[Either[(StatusCode, ErrorResponse), (StatusCode, UserLoginCredentialsResponse)]]
  def createUser(user: UserRegisterRequest): F[Either[(StatusCode, ErrorResponse), (StatusCode, UserRegisterResponse)]]
}

class UsersServiceImpl(usersRepository: UsersRepositoryImpl) extends UsersService[IO] {
  override def checkUserPassword(userLogin: String, password: Option[String]): IO[Either[(StatusCode, ErrorResponse), (StatusCode, UserLoginCredentialsResponse)]] = {
    password match {
      case Some(pass) => usersRepository.findUserPassword(userLogin).map {
        case Some(truePass) if truePass == pass => Right(StatusCode.Ok -> UserLoginCredentialsResponse(result = true, login = userLogin))
        case Some(_) => Left(StatusCode.BadRequest -> ErrorResponse("Invalid login credentials"))
        case None => Left(StatusCode.BadRequest -> ErrorResponse("Password was not found"))
      }
      case None => IO.pure(Left(StatusCode.BadRequest -> ErrorResponse("Invalid login credentials")))
    }
  }

  override def createUser(user: UserRegisterRequest): IO[Either[(StatusCode, ErrorResponse), (StatusCode, UserRegisterResponse)]] =
    usersRepository.registerUser(user)
      .map(_ => Right(StatusCode.Ok -> UserRegisterResponse(user.login)))
      .recover {
        case e: PSQLException if e.getMessage.contains(s"users_credentials_pkey") => Left(StatusCode.BadRequest -> ErrorResponse(s"Invalid request - user with login = ${user.login} is already existed"))
      }
}

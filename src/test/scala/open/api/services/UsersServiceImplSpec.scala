package open.api.services

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.softwaremill.macwire.wire
import doobie.ConnectionIO
import doobie.free.connection
import open.api.errors.ErrorResponse
import open.api.models.requests.{UserLoginCredentialsRequest, UserRegisterRequest}
import open.api.models.responses.{SuccessResponse, UserLoginCredentialsResponse}
import open.api.testutils.mocks.DefaultMocks
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.postgresql.util.PSQLException
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import sttp.model.StatusCode

class UsersServiceImplSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with UsersServiceUtils {

  "createUser" - {
    "create user, if it isn't existing" in {
      when(mockUsersRepository.registerUser(request)).thenReturn(IO.unit)

      usersService
        .createUser(request)
        .asserting(_ shouldBe Right(StatusCode.Ok -> SuccessResponse(s"Account with login = $login was successfully created")))
    }
    "not create user, if it is existing" in {
      when(mockUsersRepository.registerUser(request)).thenReturn(IO.raiseError(new PSQLException("users_credentials_pkey", any)))

      usersService
        .createUser(request)
        .asserting(
          _ shouldBe Left(
            StatusCode.BadRequest -> ErrorResponse(s"Invalid request - user with login = ${request.login} is already existed")
          )
        )
    }
  }

  "checkUserPassword" - {
    "return true and ok response, if it was found" in {
      when(mockUsersRepository.findUserPassword(login)).thenReturn(IO.pure(Some(pass)))

      usersService
        .checkUserPassword(login, Some(pass))
        .asserting(_ shouldBe Right(StatusCode.Ok -> UserLoginCredentialsResponse(login = login)))
    }
    "return bad response, if it was found, but passes doesn't matches" in {
      when(mockUsersRepository.findUserPassword(login)).thenReturn(IO.pure(Some(newPass)))

      usersService
        .checkUserPassword(login, Some(pass))
        .asserting(_ shouldBe Left(StatusCode.BadRequest -> ErrorResponse("Invalid login credentials")))
    }
    "return bad response, if it wasn't found" in {
      when(mockUsersRepository.findUserPassword(login)).thenReturn(IO.pure(None))

      usersService
        .checkUserPassword(login, Some(pass))
        .asserting(_ shouldBe Left(StatusCode.BadRequest -> ErrorResponse("Login was not found")))
    }
  }

  "updateUserPassword" - {
    "update user's password" in {
      when(mockUsersRepository.updateUserPassword(credentialsRequest)).thenReturn(IO.unit)

      usersService
        .updateUserPassword(credentialsRequest)
        .asserting(_ shouldBe Right(StatusCode.Ok -> SuccessResponse(s"Password with login = $login was successfully updated")))
    }
    "not update, if db isn't responding" in {
      when(mockUsersRepository.updateUserPassword(credentialsRequest)).thenReturn(IO.raiseError(new PSQLException("message", any())))

      usersService
        .updateUserPassword(credentialsRequest)
        .asserting(
          _ shouldBe Left(
            StatusCode.BadGateway -> ErrorResponse(s"Internal server error with error = org.postgresql.util.PSQLException: message")
          )
        )
    }
  }
}

trait UsersServiceUtils extends DefaultMocks {
  val usersService: UsersService[IO] = wire[UsersServiceImpl]

  val login: String = "login"
  val pass: String = "pass"
  val newPass: String = "newPass"
  val request: UserRegisterRequest = UserRegisterRequest(
    login = login,
    password = pass,
    firstName = None,
    secondName = None,
    email = None
  )
  val credentialsRequest: UserLoginCredentialsRequest = UserLoginCredentialsRequest(
    login = login,
    password = newPass
  )
  val userRegisterDao: ConnectionIO[Int] = connection.pure(1)
  val findPasswordDao: ConnectionIO[Option[String]] = connection.pure(Some(pass))
  val notFindPasswordDao: ConnectionIO[Option[String]] = connection.pure(None)
}

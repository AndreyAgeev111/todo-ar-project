package open.api.persistent.repository

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.softwaremill.macwire.wire
import doobie.ConnectionIO
import doobie.free.connection
import open.api.models.requests.UserRegisterRequest
import open.api.persistent.dto.{UserDto, UserLoginCredentialsDto}
import open.api.testutils.mocks.DefaultMocks
import org.mockito.Mockito.when
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

class UsersRepositoryImplSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with UsersRepositoryUtils {

  "registerUser" - {
    "run transaction with UserRegisterRequest data" in {
      when(mockUsersDao.addUser(userCredDto, userDto)).thenReturn(userRegisterDao)

      usersRepository.registerUser(request).asserting(_ shouldBe ())
    }
  }

  "findUserPassword" - {
    "returns password, if user is existing" in {
      when(mockUsersDao.findUserPassword(login)).thenReturn(findPasswordDao)

      usersRepository.findUserPassword(login).asserting(_ shouldBe Some(pass))
    }
    "returns None, if user is not existing" in {
      when(mockUsersDao.findUserPassword(login)).thenReturn(notFindPasswordDao)

      usersRepository.findUserPassword(login).asserting(_ shouldBe None)
    }
  }
}

trait UsersRepositoryUtils extends DefaultMocks {
  val usersRepository: UsersRepository[IO] = wire[UsersRepositoryImpl]

  val login: String = "login"
  val pass: String = "pass"
  val request: UserRegisterRequest = UserRegisterRequest(
    login = login,
    password = pass,
    firstName = None,
    secondName = None,
    email = None
  )
  val userDto: UserDto = UserDto.fromRegisterRequest(request)
  val userCredDto: UserLoginCredentialsDto = UserLoginCredentialsDto.fromRegisterRequest(request)
  val userRegisterDao: ConnectionIO[Int] = connection.pure(1)
  val findPasswordDao: ConnectionIO[Option[String]] = connection.pure(Some(pass))
  val notFindPasswordDao: ConnectionIO[Option[String]] = connection.pure(None)
}

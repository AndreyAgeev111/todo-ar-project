package open.api.persistent.repository

import cats.effect.IO
import open.api.persistent.dao.UsersDao
import doobie.implicits._
import open.api.models.requests.{UserLoginCredentialsRequest, UserRegisterRequest}
import open.api.persistent.dto.{UserDto, UserLoginCredentialsDto}
import open.api.persistent.util.Connection.xa

trait UsersRepository[F[_]] {
  def registerUser(user: UserRegisterRequest): F[Unit]
  def findUserPassword(userLogin: String): F[Option[String]]
  def updateUserPassword(userCredentials: UserLoginCredentialsRequest): F[Unit]
}

class UsersRepositoryImpl(usersDao: UsersDao) extends UsersRepository[IO] {
  override def registerUser(user: UserRegisterRequest): IO[Unit] = {
    usersDao.addUser(UserLoginCredentialsDto.fromRegisterRequest(user), UserDto.fromRegisterRequest(user)).transact(xa).void
  }

  override def findUserPassword(userLogin: String): IO[Option[String]] =
    usersDao.findUserPassword(userLogin).transact(xa)

  override def updateUserPassword(userCredentials: UserLoginCredentialsRequest): IO[Unit] =
    usersDao.updateUserPassword(userCredentials).transact(xa).void
}

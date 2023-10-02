package open.api.persistent.repository

import cats.effect.IO
import open.api.persistent.dao.UsersDao
import doobie.implicits._
import doobie.util.transactor
import open.api.models.requests.UserRegisterRequest
import open.api.persistent.util.Connection.xa

trait UsersRepository[F[_]] {
  def registerUser(user: UserRegisterRequest): F[Unit]
}

class UsersRepositoryImpl(usersDao: UsersDao) extends UsersRepository[IO] {
  override def registerUser(user: UserRegisterRequest): IO[Unit] = {
    for {
      _ <- usersDao.addUserCredentials(UserRegisterRequest.toUserCredentials(user)).run.transact(xa)
      _ <- usersDao.addUserInfo(UserRegisterRequest.toUser(user)).run.transact(xa)
    } yield ()
  }

}

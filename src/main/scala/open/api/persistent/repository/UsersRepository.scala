package open.api.persistent.repository

import cats.effect.IO
import open.api.models.UserLoginForm
import open.api.persistent.dao.UsersDao
import doobie.implicits._
import open.api.persistent.util.Connection.xa

trait UsersRepository[F[_]] {
  def addUser(user: UserLoginForm): F[Int]
}

class UsersRepositoryImpl(usersDao: UsersDao) extends UsersRepository[IO] {
  override def addUser(user: UserLoginForm): IO[Int] =
    usersDao.addUser(user).run.transact(xa)
}

package open.api.persistent.repository

import cats.effect.IO
import open.api.models.UserTask
import open.api.persistent.dao.UserTaskDao
import doobie.implicits._
import open.api.persistent.util.Connection.xa



trait UserTaskRepository[F[_]] {
  def listUserTasks(userLogin: String): F[List[UserTask]]
}

class UserTaskRepositoryImpl(userTaskDao: UserTaskDao) extends UserTaskRepository[IO] {
  override def listUserTasks(userLogin: String): IO[List[UserTask]] =
    userTaskDao.listUserTasks(userLogin).to[List].transact(xa)
}

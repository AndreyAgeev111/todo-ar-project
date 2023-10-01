package open.api.persistent.repository

import cats.effect.IO
import open.api.models.UserTask
import open.api.persistent.dao.UserTaskDao
import doobie.implicits._
import open.api.persistent.dto.UserTaskDto
import open.api.persistent.util.Connection.xa


trait UserTaskRepository[F[_]] {
  def listUserTasks(userLogin: String): F[List[UserTask]]

  def addUserTask(userTask: UserTask, userLogin: String): F[String]
}

class UserTaskRepositoryImpl(userTaskDao: UserTaskDao) extends UserTaskRepository[IO] {
  override def listUserTasks(userLogin: String): IO[List[UserTask]] =
    userTaskDao.listUserTasks(userLogin).to[List].transact(xa).map(_.map(UserTaskDto.toTask))

  override def addUserTask(userTask: UserTask, userLogin: String): IO[String] =
    userTaskDao.addUserTask(userTask, userLogin).withUniqueGeneratedKeys[String]("id").transact(xa)
}

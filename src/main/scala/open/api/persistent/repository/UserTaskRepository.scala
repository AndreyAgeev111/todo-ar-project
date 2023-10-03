package open.api.persistent.repository

import cats.effect.IO
import open.api.persistent.dao.UserTaskDao
import doobie.implicits._
import open.api.models.requests.UserTaskCreateRequest
import open.api.models.responses.UserTaskResponse
import open.api.persistent.dto.UserTaskDto
import open.api.persistent.util.Connection.xa

trait UserTaskRepository[F[_]] {
  def listUserTasks(userLogin: String): F[List[UserTaskDto]]
  def addUserTask(userTask: UserTaskCreateRequest, userLogin: String): F[Unit]
}

class UserTaskRepositoryImpl(userTaskDao: UserTaskDao) extends UserTaskRepository[IO] {
  override def listUserTasks(userLogin: String): IO[List[UserTaskDto]] =
    userTaskDao.listUserTasks(userLogin).transact(xa)

  override def addUserTask(userTask: UserTaskCreateRequest, userLogin: String): IO[Unit] =
    userTaskDao.addUserTask(userTask, userLogin).transact(xa).as()
}

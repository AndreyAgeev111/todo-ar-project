package open.api.persistent.repository

import cats.effect.IO
import open.api.persistent.dao.UserTaskDao
import doobie.implicits._
import open.api.models.requests.UserTaskRequest
import open.api.models.responses.UserTaskResponse
import open.api.persistent.dto.UserTaskDto
import open.api.persistent.util.Connection.xa


trait UserTaskRepository[F[_]] {
  def listUserTasks(userLogin: String): F[List[UserTaskResponse]]

  def addUserTask(userTask: UserTaskRequest, userLogin: String): F[Int]
}

class UserTaskRepositoryImpl(userTaskDao: UserTaskDao) extends UserTaskRepository[IO] {
  override def listUserTasks(userLogin: String): IO[List[UserTaskResponse]] =
    userTaskDao.listUserTasks(userLogin).to[List].transact(xa).map(_.map(UserTaskDto.toTaskResponse))

  override def addUserTask(userTask: UserTaskRequest, userLogin: String): IO[Int] =
    userTaskDao.addUserTask(userTask, userLogin).run.transact(xa)
}

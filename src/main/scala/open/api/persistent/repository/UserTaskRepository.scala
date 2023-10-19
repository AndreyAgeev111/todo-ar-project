package open.api.persistent.repository

import cats.effect.IO
import open.api.persistent.dao.UserTaskDao
import doobie.implicits._
import open.api.models.TaskStatuses.TaskStatus
import open.api.models.requests.{FilterRequest, UserTaskCreateRequest}
import open.api.persistent.dto.UserTaskDto
import open.api.persistent.errors.NotFoundTaskError
import open.api.persistent.util.Connection.xa

trait UserTaskRepository[F[_]] {
  def listUserTasks(userLogin: String): F[List[UserTaskDto]]
  def addUserTask(userTask: UserTaskCreateRequest, userLogin: String): F[Unit]
  def updateUserTask(userTask: UserTaskCreateRequest, taskId: String, userLogin: String): F[Unit]
  def findUserTaskById(userLogin: String, taskId: String): F[Option[UserTaskDto]]
  def deleteUserTask(userLogin: String, taskId: String): F[Unit]
  def updateUserTaskStatus(userLogin: String, taskId: String, status: TaskStatus): F[Unit]
  def listUserTasksWithFilter(userLogin: String, filter: FilterRequest): F[List[UserTaskDto]]
}

class UserTaskRepositoryImpl(userTaskDao: UserTaskDao) extends UserTaskRepository[IO] {
  override def listUserTasks(userLogin: String): IO[List[UserTaskDto]] =
    userTaskDao.listUserTasks(userLogin).transact(xa)

  override def addUserTask(userTask: UserTaskCreateRequest, userLogin: String): IO[Unit] =
    userTaskDao.addUserTask(userTask, userLogin).transact(xa).void

  override def updateUserTask(userTask: UserTaskCreateRequest, taskId: String, userLogin: String): IO[Unit] =
    userTaskDao
      .findTaskById(userLogin, taskId)
      .transact(xa)
      .flatMap {
        case Some(_) => userTaskDao.updateTask(UserTaskDto(userTask, userLogin), taskId).transact(xa).void
        case None    => IO.raiseError(new NotFoundTaskError(taskId))
      }

  override def findUserTaskById(userLogin: String, taskId: String): IO[Option[UserTaskDto]] =
    userTaskDao.findTaskById(userLogin, taskId).transact(xa)

  override def deleteUserTask(userLogin: String, taskId: String): IO[Unit] =
    userTaskDao.deleteTask(userLogin, taskId).transact(xa).flatMap {
      case 0 => IO.raiseError(new NotFoundTaskError(taskId))
      case _ => IO.unit
    }

  override def updateUserTaskStatus(userLogin: String, taskId: String, status: TaskStatus): IO[Unit] =
    userTaskDao.updateUserTaskStatus(userLogin, taskId, status).transact(xa).flatMap {
      case 0 => IO.raiseError(new NotFoundTaskError(taskId))
      case _ => IO.unit
    }

  override def listUserTasksWithFilter(userLogin: String, filter: FilterRequest): IO[List[UserTaskDto]] =
    userTaskDao.listUserTasksWithFilter(userLogin, filter).transact(xa)
}

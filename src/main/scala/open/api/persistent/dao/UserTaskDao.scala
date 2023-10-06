package open.api.persistent.dao

import doobie._
import doobie.implicits._
import doobie.postgres._
import doobie.implicits._
import doobie.postgres.implicits._
import open.api.models.TaskStatuses.TaskStatus
import open.api.models.requests.UserTaskCreateRequest
import open.api.persistent.dto.UserTaskDto

trait UserTaskDao {
  def listUserTasks(userLogin: String): ConnectionIO[List[UserTaskDto]]
  def addUserTask(userTask: UserTaskCreateRequest, userLogin: String): ConnectionIO[Int]
  def findTaskById(userLogin: String, taskId: String): ConnectionIO[Option[UserTaskDto]]
  def updateTask(userTask: UserTaskDto, taskId: String): ConnectionIO[Int]
  def deleteTask(userLogin: String, taskId: String): ConnectionIO[Int]
  def updateTaskStatus(userLogin: String, taskId: String, status: TaskStatus): ConnectionIO[Int]
}

class UserTaskDaoImpl extends UserTaskDao {
  override def listUserTasks(userLogin: String): ConnectionIO[List[UserTaskDto]] =
    sql"SELECT * FROM user_tasks WHERE user_login = $userLogin".query[UserTaskDto].to[List]

  override def addUserTask(userTask: UserTaskCreateRequest, userLogin: String): ConnectionIO[Int] =
    sql"INSERT INTO user_tasks (id, user_login, name, description, created_at, deadline, status) VALUES (${Fragments.values(UserTaskDto(userTask, userLogin))})".update.run

  override def findTaskById(userLogin: String, taskId: String): ConnectionIO[Option[UserTaskDto]] =
    sql"SELECT * FROM user_tasks WHERE id = $taskId AND user_login = $userLogin".query[UserTaskDto].option

  override def updateTask(userTask: UserTaskDto, taskId: String): ConnectionIO[Int] =
    sql"UPDATE user_tasks SET (name, description, deadline, status) = (${userTask.name}, ${userTask.description}, ${userTask.deadline}, ${userTask.status}) WHERE id = $taskId AND user_login = ${userTask.userLogin}".update.run

  override def deleteTask(userLogin: String, taskId: String): ConnectionIO[Int] =
    sql"DELETE FROM user_tasks WHERE id = $taskId AND user_login = $userLogin".update.run

  override def updateTaskStatus(userLogin: String, taskId: String, status: TaskStatus): ConnectionIO[Int] =
    sql"UPDATE user_tasks SET status = $status WHERE id = $taskId AND user_login = $userLogin".update.run
}

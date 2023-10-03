package open.api.persistent.dao

import doobie._
import doobie.implicits._
import doobie.postgres._
import doobie.implicits._
import doobie.postgres.implicits._
import open.api.models.requests.UserTaskCreateRequest
import open.api.persistent.dto.UserTaskDto

trait UserTaskDao {
  def listUserTasks(userLogin: String): ConnectionIO[List[UserTaskDto]]
  def addUserTask(userTask: UserTaskCreateRequest, userLogin: String): ConnectionIO[Int]
}

class UserTaskDaoImpl extends UserTaskDao {
  override def listUserTasks(userLogin: String): ConnectionIO[List[UserTaskDto]] =
    sql"SELECT * FROM user_tasks WHERE user_login = $userLogin".query[UserTaskDto].to[List]

  override def addUserTask(userTask: UserTaskCreateRequest, userLogin: String): ConnectionIO[Int] = {
    sql"INSERT INTO user_tasks (id, user_login, name, description, created_at, deadline, status) VALUES (${Fragments.values(UserTaskDto(userTask, userLogin))})".update.run
  }
}

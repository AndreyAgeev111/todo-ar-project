package open.api.persistent.dao

import doobie._
import doobie.implicits._
import doobie.postgres._
import doobie.implicits._
import doobie.postgres.implicits._
import open.api.models.requests.UserTaskRequest
import open.api.persistent.dto.UserTaskDto


trait UserTaskDao {
  def listUserTasks(userLogin: String): Query0[UserTaskDto]

  def addUserTask(userTask: UserTaskRequest, userLogin: String): Update0
}

class UserTaskDaoImpl extends UserTaskDao {
  override def listUserTasks(userLogin: String): Query0[UserTaskDto] =
    sql"SELECT * FROM user_tasks WHERE user_login = $userLogin".query[UserTaskDto]

  override def addUserTask(userTask: UserTaskRequest, userLogin: String): Update0 = {
    sql"INSERT INTO user_tasks (id, user_login, name, description, created_at, deadline, status) VALUES (${Fragments.values(UserTaskDto(userTask, userLogin))})".update
  }
}

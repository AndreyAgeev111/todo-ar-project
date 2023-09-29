package open.api.persistent.dao

import doobie._
import doobie.implicits._
import open.api.models.UserTask
import doobie.postgres._
import doobie.postgres.implicits._


trait UserTaskDao {
  def listUserTasks(userLogin: String): Query0[UserTask]
}

class UserTaskDaoImpl extends UserTaskDao {
  override def listUserTasks(userLogin: String): Query0[UserTask] = {
    sql"SELECT * FROM user_tasks WHERE user_login = $userLogin".query[UserTask]
  }
}

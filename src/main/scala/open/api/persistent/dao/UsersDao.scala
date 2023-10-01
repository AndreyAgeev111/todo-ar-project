package open.api.persistent.dao

import doobie.Fragments
import doobie.implicits.toSqlInterpolator
import doobie.util.update.Update0
import open.api.models.UserLoginForm

trait UsersDao {
  def addUser(user: UserLoginForm): Update0
}

class UsersDaoImpl extends UsersDao {
  override def addUser(user: UserLoginForm): Update0 =
    sql"INSERT INTO users_credentials (login, pass) VALUES (${Fragments.values(user)})".update
}

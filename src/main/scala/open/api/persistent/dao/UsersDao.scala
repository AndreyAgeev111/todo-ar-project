package open.api.persistent.dao

import doobie.Fragments
import doobie.implicits.toSqlInterpolator
import doobie.util.update.Update0
import open.api.models.requests.UserLoginCredentials
import open.api.persistent.dto.{UserDto, UserLoginCredentialsDto}

trait UsersDao {
  def addUserCredentials(user: UserLoginCredentialsDto): Update0
  def addUserInfo(fullUser: UserDto): Update0
}

class UsersDaoImpl extends UsersDao {
  override def addUserCredentials(user: UserLoginCredentialsDto): Update0 =
    sql"INSERT INTO users_credentials (login, pass) VALUES (${Fragments.values(user)})".update

  override def addUserInfo(user: UserDto): Update0 =
    sql"INSERT INTO users (user_login, first_name, second_name, email) VALUES (${Fragments.values(user)})".update
}

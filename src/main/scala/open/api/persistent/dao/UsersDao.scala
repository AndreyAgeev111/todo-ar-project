package open.api.persistent.dao

import doobie.{ConnectionIO, Fragments}
import doobie.implicits.toSqlInterpolator
import open.api.persistent.dto.{UserDto, UserLoginCredentialsDto}

trait UsersDao {
  def addUser(userCredentials: UserLoginCredentialsDto, user: UserDto): ConnectionIO[Int]
  def findUserPassword(userLogin: String): ConnectionIO[Option[String]]
}

class UsersDaoImpl extends UsersDao {
  override def addUser(userCredentials: UserLoginCredentialsDto, user: UserDto): ConnectionIO[Int] =
    sql"INSERT INTO users_credentials (login, pass) VALUES (${Fragments.values(userCredentials)}); INSERT INTO users (user_login, first_name, second_name, email) VALUES (${Fragments
        .values(user)})".update.run

  override def findUserPassword(userLogin: String): ConnectionIO[Option[String]] =
    sql"SELECT pass FROM users_credentials WHERE login = $userLogin".query[String].option
}

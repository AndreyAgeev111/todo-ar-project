package open.api.wirings

import cats.effect.IO
import com.softwaremill.macwire.wire
import open.api.controllers.{AuthorizedController, PublicController}
import open.api.persistent.dao.{UserTaskDao, UserTaskDaoImpl, UsersDao, UsersDaoImpl}
import open.api.persistent.repository.{UserTaskRepositoryImpl, UsersRepositoryImpl}
import open.api.services.{UserTaskServiceImpl, UsersServiceImpl}

object Dependencies {

  private lazy val userTaskDao: UserTaskDao = wire[UserTaskDaoImpl]
  private lazy val usersDao: UsersDao = wire[UsersDaoImpl]

  private lazy val usersRepository: UsersRepositoryImpl = wire[UsersRepositoryImpl]
  private lazy val userTaskRepository: UserTaskRepositoryImpl = wire[UserTaskRepositoryImpl]

  private lazy val usersService: UsersServiceImpl = wire[UsersServiceImpl]
  private lazy val userTaskService: UserTaskServiceImpl = wire[UserTaskServiceImpl]

  private lazy val authorizedController: AuthorizedController[IO] = wire[AuthorizedController[IO]]
  private lazy val publicController: PublicController[IO] = wire[PublicController[IO]]

  lazy val controllersModule: ControllersModule = wire[ControllersModule]
}

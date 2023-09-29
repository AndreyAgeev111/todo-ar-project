package open.api.wirings

import com.softwaremill.macwire.wire
import open.api.controllers.{AuthorizedController, PublicController}
import open.api.persistent.dao.{UserTaskDao, UserTaskDaoImpl}
import open.api.persistent.repository.{UserTaskRepository, UserTaskRepositoryImpl}

object Dependencies {

  private lazy val userTaskDao: UserTaskDao = wire[UserTaskDaoImpl]
  private lazy val publicController: PublicController = wire[PublicController]

  private lazy val userTaskRepository: UserTaskRepositoryImpl = wire[UserTaskRepositoryImpl]

  private lazy val authorizedController: AuthorizedController = wire[AuthorizedController]

  lazy val controllersModule: ControllersModule = wire[ControllersModule]
}

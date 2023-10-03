package open.api.testutils.mocks

import open.api.persistent.dao.{UserTaskDao, UsersDao}
import open.api.persistent.repository.{UserTaskRepositoryImpl, UsersRepositoryImpl}
import org.scalatestplus.mockito.MockitoSugar

trait DefaultMocks extends MockitoSugar {
  val mockUsersDao: UsersDao = mock[UsersDao]
  val mockUserTaskDao: UserTaskDao = mock[UserTaskDao]
  val mockUsersRepository: UsersRepositoryImpl = mock[UsersRepositoryImpl]
  val mockUserTaskRepository: UserTaskRepositoryImpl = mock[UserTaskRepositoryImpl]
}

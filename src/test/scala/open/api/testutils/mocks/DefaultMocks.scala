package open.api.testutils.mocks

import open.api.persistent.dao.{UserTaskDao, UsersDao}
import org.scalatestplus.mockito.MockitoSugar.mock

trait DefaultMocks {
  val mockUsersDao: UsersDao = mock[UsersDao]
  val mockUserTaskDao: UserTaskDao = mock[UserTaskDao]
}

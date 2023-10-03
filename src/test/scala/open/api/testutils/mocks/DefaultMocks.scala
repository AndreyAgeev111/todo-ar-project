package open.api.testutils.mocks

import cats.effect.IO
import doobie.util.transactor.Transactor
import open.api.persistent.dao.{UserTaskDao, UsersDao}
import org.scalatestplus.mockito.MockitoSugar.mock

trait DefaultMocks {
  val mockUsersDao: UsersDao = mock[UsersDao]
  val mockUserTaskDao: UserTaskDao = mock[UserTaskDao]
  val mockTransactor: Transactor[IO] = mock[Transactor[IO]]
}

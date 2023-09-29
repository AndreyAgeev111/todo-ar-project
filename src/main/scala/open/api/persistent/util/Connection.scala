package open.api.persistent.util

import cats.effect.IO
import doobie.util.transactor.Transactor

object Connection {
  private val dbDriver = "org.postgresql.Driver"
  private val dbUrl = "jdbc:postgresql://localhost:5432/ar-project"
  private val dbUser = "postgres"
  private val dbPass = "andrey"

  val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
      driver = dbDriver,
      url = dbUrl,
      user = dbUser,
      pass = dbPass)
}

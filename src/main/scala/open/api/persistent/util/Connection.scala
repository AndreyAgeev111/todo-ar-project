package open.api.persistent.util

import cats.effect.IO
import doobie.util.transactor.Transactor
import open.api.persistent.configuration.DBConfiguration
import pureconfig.ConfigSource
import pureconfig.generic.auto._

object Connection {
  val xa: Transactor[IO] = ConfigSource.default.at("db").load[DBConfiguration] match {
    case Right(dbConfig) =>
      Transactor.fromDriverManager[IO](
        driver = dbConfig.driver,
        url = dbConfig.url,
        user = dbConfig.user,
        pass = dbConfig.password
      )
  }
}

package open.api.persistent.configuration

import pureconfig._
import pureconfig.generic.auto._
case class DBConfiguration(user: String, password: String, driver: String, url: String)

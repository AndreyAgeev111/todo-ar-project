package open.api

import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.{Host, IpLiteralSyntax, Port}
import open.api.wirings.Dependencies
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.http4s.Http4sServerInterpreter

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    val routes = Http4sServerInterpreter[IO](Dependencies.controllersModule.customOptions).toRoutes(Dependencies.controllersModule.all)

    val port = sys.env
      .get("HTTP_PORT")
      .flatMap(_.toIntOption)
      .flatMap(Port.fromInt)
      .getOrElse(port"8080")

    EmberServerBuilder
      .default[IO]
      .withHost(Host.fromString("0.0.0.0").get)
      .withPort(port)
      .withHttpApp(Router("/" -> routes).orNotFound)
      .build
      .useForever
      .as(ExitCode.Success)
  }
}

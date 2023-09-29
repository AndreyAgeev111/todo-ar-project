package open.api.wirings

import cats.effect.IO
import open.api.controllers.{AuthorizedController, PublicController}
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.swagger.bundle.SwaggerInterpreter

object ControllersModule extends AuthorizedController with PublicController {
  private val apiEndpoints = authorizedApiEndpoints ++ publicApiEndpoints

  private val docEndpoints: List[ServerEndpoint[Any, IO]] = SwaggerInterpreter()
    .fromServerEndpoints[IO](apiEndpoints, "todo-ar-project", "1.0.0")

  val all: List[ServerEndpoint[Any, IO]] = apiEndpoints ++ docEndpoints
}

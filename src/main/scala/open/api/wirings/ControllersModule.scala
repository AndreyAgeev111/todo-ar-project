package open.api.wirings

import cats.effect.IO
import open.api.controllers.{AuthorizedController, PublicController}
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerOptions
import sttp.tapir.swagger.bundle.SwaggerInterpreter

class ControllersModule(authorizedController: AuthorizedController[IO], publicController: PublicController[IO]) {
  private val apiEndpoints = authorizedController.authorizedApiEndpoints ++ publicController.publicApiEndpoints

  private val docEndpoints: List[ServerEndpoint[Any, IO]] = SwaggerInterpreter()
    .fromServerEndpoints[IO](apiEndpoints, "todo-ar-project", "1.0.0")

  val all: List[ServerEndpoint[Any, IO]] = apiEndpoints ++ docEndpoints
  val customOptions: Http4sServerOptions[IO] = Http4sServerOptions.customiseInterceptors[IO].options
}

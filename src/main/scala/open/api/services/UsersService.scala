package open.api.services

import cats.effect.IO
import open.api.errors.ErrorResponse
import open.api.persistent.repository.UsersRepositoryImpl
import sttp.model.StatusCode

trait UsersService[F[_]] {
  def checkUserPassword(userLogin: String, pass: String): F[Either[(StatusCode, ErrorResponse), (StatusCode, Boolean)]]
}

class UsersServiceImpl(usersRepository: UsersRepositoryImpl) extends UsersService[IO] {
  override def checkUserPassword(userLogin: String, pass: String): IO[Either[(StatusCode, ErrorResponse), (StatusCode, Boolean)]] =
    usersRepository.findUserPassword(userLogin).map {
      case Some(truePass) if truePass == pass => Right(StatusCode.Ok -> true)
      case Some(_) => Left(StatusCode.BadRequest -> ErrorResponse("Invalid login credentials"))
      case None => Left(StatusCode.BadRequest -> ErrorResponse("Password was not found"))
    }
}

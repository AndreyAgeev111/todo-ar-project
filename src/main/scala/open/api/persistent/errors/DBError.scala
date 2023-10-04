package open.api.persistent.errors

trait DBError extends Throwable {
  val message: String
}

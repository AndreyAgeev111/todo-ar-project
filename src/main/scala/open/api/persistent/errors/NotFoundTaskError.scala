package open.api.persistent.errors

class NotFoundTaskError(taskId: String) extends DBError {
  override val message: String = s"Not found task with taskId = $taskId"
}

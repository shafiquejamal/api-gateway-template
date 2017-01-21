package entrypoint

import java.util.UUID

import user.{ChangePasswordMessage, ChangeUsernameMessage, UserMessage}

import scala.util.Try

trait UserAPI {

  def changeUsername(userId: UUID, changeUsernameMessage: ChangeUsernameMessage): Try[UserMessage]

  def changePassword(userId: UUID, changePasswordMessage: ChangePasswordMessage): Try[UserMessage]

  def findByEmailLatest(email: String): Option[UserMessage]

  def findUnverifiedUser(email: String): Option[UserMessage]

  def by(username: String): Option[UUID]

  def by(userId: UUID): Option[String]
}

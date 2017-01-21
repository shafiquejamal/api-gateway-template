package entrypoint

import java.util.UUID

import access.registration.RegistrationMessage
import user.{UserMessage, UserStatus}

import scala.util.Try

trait RegistrationAPI {

  def signUp(registrationMessage: RegistrationMessage, statusOnRegistration: UserStatus): Try[UserMessage]

  def isUsernameIsAvailable(username: String): Boolean

  def isEmailIsAvailable(email: String): Boolean

  def activate(userId: UUID): Try[UserMessage]

}

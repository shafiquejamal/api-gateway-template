package entrypoint

import access.authentication.ToServerAuthenticateMessage.ToServerAuthenticate
import access.authentication.ToServerLogoutMessage.ToServerLogout
import access.authentication.ToServerPasswordResetRequestMessage.ToServerPasswordResetRequest
import access.registration.ToServerResendActivationCodeRequestMessage.ToServerResendActivationCodeRequest
import akka.actor.ActorRef
import play.api.libs.json.{JsValue, Json, Writes}


trait SocketMessage {

  def socketMessageType: SocketMessageType

}

trait SocketMessageType {

  def description: String

}

trait ToClientSocketMessage extends SocketMessage {

  def payload: AnyRef

  def toJson: JsValue


}

trait ToServerSocketMessage extends SocketMessage {

  def sendTo(toServerMessageActor: ActorRef): Unit = toServerMessageActor ! this

}

trait ToServerSocketMessageType extends SocketMessageType {

  def socketMessage(msg: JsValue): ToServerSocketMessage

}

object ToServerSocketMessageType {

  private val socketMessageTypeFrom = Map[String, ToServerSocketMessageType](
    ToServerPasswordResetRequest.description -> ToServerPasswordResetRequest,
    ToServerResendActivationCodeRequest.description -> ToServerResendActivationCodeRequest,
    ToServerAuthenticate.description -> ToServerAuthenticate,
    ToServerLogout.description -> ToServerLogout
  )

  def from(description:String): ToServerSocketMessageType = socketMessageTypeFrom(description)

}

object SocketMessageType {

  implicit object SocketMessageTypeWrites extends Writes[SocketMessageType] {
    override def writes(socketMessageType: SocketMessageType) = Json.toJson(socketMessageType.description)
  }


}
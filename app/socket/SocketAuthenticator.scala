package socket

import access.authentication.AuthenticationAPI
import access.{AuthenticatedActionCreator, JWTKeysProvider}
import com.google.inject.Inject
import play.Configuration
import util.TimeProvider

class SocketAuthenticator @Inject()(
    override val authenticationAPI:AuthenticationAPI,
    override val jWTParamsProvider: JWTKeysProvider,
    override val configuration: Configuration,
    override val timeProvider: TimeProvider)
  extends AuthenticatedActionCreator {

}

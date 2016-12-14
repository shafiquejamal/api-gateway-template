package modules

import access.authentication.{PasswordResetCodeSenderImpl, PasswordResetCodeSender, AuthenticationAPI, AuthenticationFacade}
import access.registration.{AccountActivationLinkSender, AccountActivationLinkSenderImpl, RegistrationAPI,
RegistrationFacade}
import access.{JWTParamsProvider, JWTParamsProviderImpl}
import com.google.inject.AbstractModule
import communication.{LinkSenderImpl, LinkSender, Emailer, EmailerImpl}
import contact.phone._
import db.{DBConfig, ScalikeJDBCDevProdDBConfig, ScalikeJDBCSessionProvider, ScalikeJDBCSessionProviderImpl}
import net.codingwell.scalaguice.ScalaModule
import user.{UserAPI, UserFacade}
import util._

class Module extends AbstractModule with ScalaModule {

  override def configure() {
    bind[ScalikeJDBCSessionProvider].to[ScalikeJDBCSessionProviderImpl]
    bind[TimeProvider].to[TimeProviderImpl]
    bind[UUIDProvider].to[UUIDProviderImpl]
    bind[RegistrationAPI].to[RegistrationFacade]
    bind[AuthenticationAPI].to[AuthenticationFacade]
    bind[UserAPI].to[UserFacade]
    bind[PhoneAPI].to[PhoneFacade]
    bind[DBConfig].to[ScalikeJDBCDevProdDBConfig]
    bind[JWTParamsProvider].to[JWTParamsProviderImpl]
    bind[Emailer].to[EmailerImpl]
    bind[AccountActivationLinkSender].to[AccountActivationLinkSenderImpl]
    bind[PasswordResetCodeSender].to[PasswordResetCodeSenderImpl]
    bind[LinkSender].to[LinkSenderImpl]
    bind[PhoneDAO].to[ScalikeJDBCPhoneDAOImpl]
  }

}

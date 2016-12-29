package user

import java.util.UUID

import access.{JWTParamsProvider, TestJWTParamsProviderImpl}
import db.{DBConfig, InitialMigration, OneAppPerTestWithOverrides, ScalikeJDBCTestDBConfig}
import org.scalatest._
import pdi.jwt.JwtJson
import play.api.inject._
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import scalikejdbc.NamedAutoSession
import util._

class UserControllerATest
  extends FlatSpec
  with ShouldMatchers
  with OneAppPerTestWithOverrides
  with BeforeAndAfterEach
  with InitialMigration
  with UserFixture {

  override def overrideModules =
    Seq(bind[DBConfig].to[ScalikeJDBCTestDBConfig],
        bind[JWTParamsProvider].to[TestJWTParamsProviderImpl],
        bind[UUIDProvider].to[TestUUIDProviderImpl],
        bind[TimeProvider].to[TestTimeProviderImpl]
       )

  val dBConfig = new ScalikeJDBCTestDBConfig()

  override def beforeEach() {
    implicit val session = NamedAutoSession(Symbol(dBConfig.dBName))
    dBConfig.setUpAllDB()
    migrate(dBConfig)
    sqlToAddUsers.foreach(_.update.apply())
    super.beforeEach()
  }

  override def afterEach() {
    dBConfig.closeAll()
    super.afterEach()
  }

  val timeProvider = new TestTimeProviderImpl()
  val jWTParamsProvider = new TestJWTParamsProviderImpl()
  val claim = Json.obj("userId" -> id1, "iat" -> timeProvider.now())
  val jWT = JwtJson.encode(claim, jWTParamsProvider.privateKey, jWTParamsProvider.algorithm)

  "changing the password" should "succeed if the existing password is valid" in {
    val content =
      contentAsJson(route(app, FakeRequest(POST, "/change-password")
        .withHeaders(("Authorization", "Bearer " + jWT))
        .withJsonBody(Json.obj("currentPassword" -> "passwordAliceID2", "newPassword" -> "some-new-password",
          "iat" -> timeProvider.now()))
      ).get)

    (content \ "status").asOpt[String] should contain("success")
  }

  it should "fail if the existing password does not match" in {
    val content =
      contentAsJson(route(app, FakeRequest(POST, "/change-password")
        .withHeaders(("Authorization",  "Bearer " + jWT))
        .withJsonBody(Json.obj("currentPassword" -> "wrong password", "newPassword" -> "some-new-password"))
      ).get)

    (content \ "status").asOpt[String] should contain("password change failed")
  }

  it should "fail if valid data is not sent" in {
    val result =
      route(app, FakeRequest(POST, "/change-password")
        .withHeaders(("Authorization",  "Bearer " + jWT))
        .withJsonBody(Json.obj("currentPassword" -> 1, "newPassword" -> "some-new-password")))
        .get

    status(result) shouldBe OK
    (contentAsJson(result) \ "status").asOpt[String] should contain("invalid data")
  }

  it should "fail if there is the authorization credentials are incorrect" in {
    val wrongJWT =
      JwtJson.encode(Json.obj("userId" -> UUID.fromString("00000000-0000-0000-0000-000000000002")),
        jWTParamsProvider.privateKey, jWTParamsProvider.algorithm)

    val result =
      route(app, FakeRequest(POST, "/change-password")
      .withHeaders(("Authorization", wrongJWT))
      .withJsonBody(Json.obj("currentPassword" -> 1, "newPassword" -> "some-new-password"))).get

    status(result) shouldBe UNAUTHORIZED
    contentAsString(result) shouldBe empty
  }

  it should "fail if the iat is before the last allLogout date" in {
    val claim = Json.obj("userId" -> id3, "iat" -> yesterday.minusMillis(1))
    val jWT = JwtJson.encode(claim, jWTParamsProvider.privateKey, jWTParamsProvider.algorithm)

    val result = route(app, FakeRequest(POST, "/change-password")
      .withHeaders(("Authorization",  "Bearer " + jWT))
      .withJsonBody(Json.obj("currentPassword" -> "passwordBobID3", "newPassword" -> "some-new-password",
        "iat" -> timeProvider.now()))
      ).get

    status(result) shouldBe UNAUTHORIZED
    contentAsString(result) shouldBe empty
  }

}

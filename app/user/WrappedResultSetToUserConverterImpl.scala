package user

import java.util.UUID

import com.google.inject.{Inject, Singleton}
import scalikejdbc.WrappedResultSet
import user.UserStatus._

@Singleton
class WrappedResultSetToUserConverterImpl @Inject() (user:User) extends WrappedResultSetToUserConverter {

  override def converter(rs: WrappedResultSet):User = user.create(
    Option(rs.string("id")).map(UUID.fromString),
    rs.string("username"),
    rs.string("email"),
    rs.string("password"),
    toUserStatus(rs.int("status")),
    Option(rs.jodaDateTime("createdat"))
  )

}

package hw.monads

import java.util.concurrent.atomic.AtomicInteger
import scala.util.Try

final case class SyncRepository(initUsers: Map[UserId, User] = Map.empty, initId: Int = 0) extends UserRepository[Try]:
  private var users: Map[UserId, User] = initUsers
  private val inc                      = new AtomicInteger(initId)

  override def create(name: UserName, age: Age, friends: Set[UserId]): Try[User] =
    Try {
      val newId   = UserId(inc.incrementAndGet())
      val newUser = User(newId, name, age, friends)
      users = users + (newId -> newUser)
      newUser
    }

  override def delete(userId: UserId): Try[Unit] =
    Try {
      users = users - userId
    }

  override def findAll: Try[List[User]] =
    Try(users.values.toList)

  override def update(user: User): Try[Unit] =
    Try {
      users = users + (user.id -> user)
    }

object SyncRepository:
  def empty: SyncRepository = SyncRepository()

  def apply(users: List[User]): SyncRepository =
    users match
      case Nil => SyncRepository.empty
      case us  => SyncRepository(us.map(u => (u.id, u)).toMap, us.map(_.id).max)

  def apply(user: User, users: User*): SyncRepository =
    apply(user :: users.toList)

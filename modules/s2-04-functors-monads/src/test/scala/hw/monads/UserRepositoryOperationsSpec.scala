package hw.monads

import cats.MonadThrow
import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.functor.toFunctorOps

import hw.monads.UserRepository.Operations.*
import hw.monads.UserRepository.UserNotFoundError

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.{Failure, Success, Try}

object TestData:
  val noFriendsAdultUser  = User(UserId(1), UserName("Paul van Dyk"), Age(51), Set.empty)
  val noFriendsJuniorUser = User(UserId(2), UserName("John Smith"), Age(15), Set.empty)

  val communicativeUser1 = User(UserId(3), UserName("Dolly"), Age(3), Set(UserId(4), UserId(5)))
  val communicativeUser2 = User(UserId(4), UserName("Donald Trump"), Age(76), Set(UserId(3), UserId(5)))
  val communicativeUser3 = User(UserId(5), UserName("Ozzy Osbourne"), Age(21), Set(UserId(3), UserId(4)))

  val repo =
    SyncRepository(noFriendsAdultUser, noFriendsJuniorUser, communicativeUser1, communicativeUser2, communicativeUser3)
  val nonexistedUserId = UserId(100)

class UserRepositoryOperationsSpec extends AnyFlatSpec with Matchers:
  import TestData.*

  behavior.of("UserRepository")

  it should "not optionally find non existed user" in {
    UserRepository.Operations.findMaybe(noFriendsAdultUser.id).apply(SyncRepository.empty) shouldBe Success(None)
  }

  it should "optionally find an existed user" in {
    UserRepository.Operations
      .findMaybe(noFriendsAdultUser.id)
      .apply(repo) shouldBe Success(Some(noFriendsAdultUser))
  }

  it should "not find non existed user" in {
    UserRepository.Operations.find(noFriendsAdultUser.id).apply(SyncRepository.empty) shouldBe Failure(
      UserNotFoundError(noFriendsAdultUser.id)
    )
  }

  it should "find an existed user" in {
    UserRepository.Operations
      .find(noFriendsAdultUser.id)
      .apply(repo) shouldBe Success(noFriendsAdultUser)
  }

  it should "add an existed friend to existed user" in {
    UserRepository.Operations
      .addFriend(noFriendsAdultUser.id, noFriendsJuniorUser.id)
      .apply(repo) shouldBe Success(
      noFriendsAdultUser.copy(friends = noFriendsAdultUser.friends + noFriendsJuniorUser.id)
    )
  }

  it should "return error while adding a non existed friend to existed user" in {
    UserRepository.Operations
      .addFriend(noFriendsAdultUser.id, nonexistedUserId)
      .apply(repo) shouldBe Failure(UserNotFoundError(nonexistedUserId))
  }

  it should "return error while adding an existed friend to non existed user" in {
    UserRepository.Operations
      .addFriend(nonexistedUserId, noFriendsJuniorUser.id)
      .apply(repo) shouldBe Failure(UserNotFoundError(nonexistedUserId))
  }

  it should "delete an existed friend from existed user" in {
    UserRepository.Operations
      .deleteFriend(noFriendsAdultUser.id, noFriendsJuniorUser.id)
      .apply(repo) shouldBe Success(
      noFriendsAdultUser.copy(friends = noFriendsAdultUser.friends - noFriendsJuniorUser.id)
    )
  }

  it should "return error while deleting a non existed friend to existed user" in {
    UserRepository.Operations
      .addFriend(noFriendsAdultUser.id, nonexistedUserId)
      .apply(repo) shouldBe Failure(UserNotFoundError(nonexistedUserId))
  }

  it should "return error while deleting an existed friend to non existed user" in {
    UserRepository.Operations
      .addFriend(nonexistedUserId, noFriendsJuniorUser.id)
      .apply(repo) shouldBe Failure(UserNotFoundError(nonexistedUserId))
  }

  it should "return all existed user friends" in {
    UserRepository.Operations
      .getUserFriends[Try](communicativeUser2.id)
      .map(_.toSet)
      .apply(repo) shouldBe Success(Set(communicativeUser1, communicativeUser3))
  }

  it should "return error while getting non existed user friends" in {
    UserRepository.Operations
      .getUserFriends(nonexistedUserId)
      .apply(repo) shouldBe Failure(UserNotFoundError(nonexistedUserId))
  }

  it should "return error while getting user unexisted friends" in {
    UserRepository.Operations
      .getUserFriends(noFriendsAdultUser.id)
      .apply(SyncRepository(noFriendsAdultUser.copy(friends = Set(nonexistedUserId)))) shouldBe Failure(
      UserNotFoundError(nonexistedUserId)
    )
  }

  it should "return all users with adult only friends" in {
    UserRepository.Operations
      .getUsersWithAdultOnlyFriends()
      .apply(repo) shouldBe Success(List(communicativeUser1))
  }

  it should "correctly delete all junior users" in {
    UserRepository.Operations
      .deleteAllJuniorUsers[Try]()
      .flatMap(_ => UserRepository.Operations.findAll[Try])
      .map(_.toSet)
      .apply(repo) shouldBe Success(Set(noFriendsAdultUser, communicativeUser2, communicativeUser3))
  }

  it should "correctly create a Community" in {

    val namesAndAges = Set(
      communicativeUser1.name -> communicativeUser1.age,
      communicativeUser2.name -> communicativeUser2.age,
      communicativeUser3.name -> communicativeUser3.age
    )
    UserRepository.Operations
      .createCommunity[Try](namesAndAges.toList)
      .map(_.toSet)
      .map { result =>
        val allCreated = result.map(u => u.name -> u.age) == namesAndAges
        val ids        = result.map(_.id)
        result.forall(u => u.friends == ids - u.id) -> allCreated
      }
      .apply(SyncRepository.empty) shouldBe Success((true, true))

  }

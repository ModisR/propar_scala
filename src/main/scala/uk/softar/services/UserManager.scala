package uk.softar.services

import scalaz.Monad
import scalaz.Scalaz.{ApplicativeIdV, ToBindOps}
import uk.softar.Main.Username
import uk.softar.models.User

/** While creating a general interface for injectable services
 *  is good practice for unit testing and mocks, this situation
 *  was especially interesting due to the task specifying that
 *  all application state be persisted in memory.
 *  As a result, it is potentially useful here to not only abstract
 *  this interface over the methods but also over the type constructor
 *  M which provides the computational context through which persistence
 *  is achieved.
 *  In a real web service, this context would typically be a Future due
 *  to the ORM performing these operations asynchronously from the main
 *  application.
 *  In this mock demo, however, that context is the humble state monad,
 *  which is suitable for expressing in-memory operations in a
 *  functional style.
 *  */
abstract class UserManager[M[_] : Monad] {
  def setUser(username: Username, user: User): M[Unit]

  def findUser(username: Username): M[Option[User]]

  def withUser(username: Username)(func: User => M[Unit]): M[Unit] =
    findUser(username) flatMap {
      case Some(user) => func(user)
      case None => {
        println(s"User $username not found.")
      }.pure[M]
    }
}

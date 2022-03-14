package uk.softar.services

import scalaz.Monad
import scalaz.Scalaz.{ApplicativeIdV, ToBindOps, ToFunctorOps}
import uk.softar.Main.{Message, Username}
import uk.softar.models.User

import java.time.{LocalDateTime, ZoneOffset}
import scala.collection.immutable.SortedMap

class AppController[M[_] : Monad](userManager: UserManager[M]) {

  def post(username: Username, message: Message): M[Unit] =
    (userManager findUser username) flatMap {
      userOpt =>
        val user = userOpt getOrElse User(SortedMap.empty, Set.empty)
        val timestamp = LocalDateTime.now()
        val modifiedUser = user.copy(messages = user.messages + (timestamp -> message))
        userManager setUser(username, modifiedUser)
    }

  def read(username: Username): M[Unit] =
    (userManager withUser username) { user => {
      user.messages.toSeq.reverse foreach { case (time, msg) =>
        println(prettify(time, msg))
      }
    }.pure[M]
    }

  def follow(followerName: Username, followeeName: Username): M[Unit] = {
    userManager findUser followerName flatMap {
      case Some(follower) =>
        (userManager withUser followeeName) { _ =>
          val newFollower = follower.copy(follows = follower.follows + followeeName)
          userManager.setUser(followerName, newFollower)
        }
      case None =>
        (userManager findUser followeeName) map {
          case Some(_) => println(s"User $followerName not found.")
          case None => println(s"Neither user $followerName nor $followeeName found.")
        }
    }
  }

  def wall(username: Username): M[Unit] =
    (userManager withUser username) { user =>
      (user.follows foldLeft SortedMap(username -> user).pure) {
        case (acc, username) => for {
          followers <- acc
          optionUser <- userManager findUser username
        } yield optionUser match {
          case Some(user) => followers + (username -> user)
          case None => followers
        }
      } map {
        _.flatMap {
          case (username, user) => user.messages map {
            case (timestamp, message) => timestamp -> (username, message)
          }
        }.toSeq.reverse foreach { case (timestamp, (username, message)) =>
          println(username + " - " + prettify(timestamp, message))
        }
      }
    }

  /** append user-friendly timestamp to message */
  private def prettify(timestamp: LocalDateTime, message: Message): String = {
    val currentEpoch = LocalDateTime.now() toEpochSecond ZoneOffset.UTC
    val messageEpoch = timestamp toEpochSecond ZoneOffset.UTC
    val secsAgo = currentEpoch - messageEpoch
    /** Make timestamp user-friendly
     *  by hunting for appropriate time unit */
    val timeString = timeUnits collectFirst {
      case (unit, secsInUnit) if secsAgo / secsInUnit > 0 =>
        val timeAgo = secsAgo / secsInUnit
        /** Pluralise time unit if needed */
        val maybeS = if (timeAgo > 1) "s" else ""
        s"$timeAgo $unit$maybeS ago"
    } getOrElse "Just now"

    s"$message ($timeString)"
  }

  /** conversions for user-friendly timestamping */
  private val timeUnits = Seq(
    "day" -> 86400,
    "hour" -> 3600,
    "minute" -> 60,
    "second" -> 1
  )
}

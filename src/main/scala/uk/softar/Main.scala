package uk.softar

import scalaz.State
import uk.softar.models.User
import uk.softar.services.{AppController, UserManagerImpl}

import scala.annotation.tailrec
import scala.collection.immutable.{SortedMap, TreeMap}
import scala.io.StdIn.readLine

object Main extends App {
  /** Initialise application services */
  private val userManager = new UserManagerImpl
  private val appController = new AppController(userManager)

  /** Print intro */
  List(
    "-----",
    "Hello and welcome to our CLI! We have the following commands:",
    "Post: <username> -> <message>",
    "Read: <username>",
    "Follow: <username> follows <username>",
    "Wall: <username> wall",
    "-----"
  ) foreach println
  /** Intialise command loop with empty set of users */
  loop(TreeMap.empty)
  /** Print outro */
  println("Exiting. Good bye!")

  /** Declare command loop, which runs recursively until
   *  given "quit" command. */
  @tailrec
  private def loop(appUsers: UserIndex): Unit = {
    val line = readLine()
    if (line != "quit")
      loop {
        parse(line) match {
          case Some(command) =>
            val (newAppUsers, _) = command run appUsers
            newAppUsers
          case None =>
            println("Command not found.")
            appUsers
        }
      }
  }

  /** Split a command into words and optionally map to a controller handler
   *  @param line The command received from standard input
   *  @return a possible action on the AppState
   */
  private def parse(line: String): Option[AppState[Unit]] = {
    (line split " +").toSeq match {
      case username +: "->" +: message => Some(appController.post(username, message mkString " "))
      case Seq(username) => Some(appController.read(username))
      case Seq(follower, "follows", followee) => Some(appController.follow(follower, followee))
      case Seq(username, "wall") => Some(appController.wall(username))
      case _ => None
    }
  }

  /** Some useful type synonyms */
  type AppState[A] = State[UserIndex, A]
  type UserIndex = TreeMap[Username, User]
  type Username = String
  type Message = String
}

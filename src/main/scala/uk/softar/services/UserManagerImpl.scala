package uk.softar.services

import scalaz.State
import uk.softar.Main.{AppState, Username}
import uk.softar.models.User

class UserManagerImpl extends UserManager[AppState] {
  def setUser(username: Username, user: User): AppState[Unit] =
    State { appState => (appState + (username -> user), ()) }

  def findUser(username: Username): AppState[Option[User]] =
    State { appState => (appState, appState get username) }
}

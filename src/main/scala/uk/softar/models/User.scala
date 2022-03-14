package uk.softar.models

import uk.softar.Main.{Message, Username}

import java.time.LocalDateTime
import scala.collection.immutable.SortedMap

case class User(messages: SortedMap[LocalDateTime, Message], follows: Set[Username])

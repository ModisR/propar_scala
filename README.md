# Property Partners Task
To build and run the app:
1. Ensure [SBT](https://www.scala-sbt.org/) is installed. On Debian-based systems, this can
be done with `sudo apt install sbt`.
2. Navigate to the project directory and execute the command `sbt run`.

Alternatively, if one can do all of this from within the IntelliJ IDE.

## Architecture
At the core of this demo app's design is the understanding implementing a service as an
extension of an abstract interface is an important design pattern which allows a certain
modularity in an application built via dependency injection.

This flexibility allows one to opt for different module bindings depending on environment,
such as implementing mocks for unit tests or possibly even A/B testing in production.

In this example, an attempt is made to not only demonstrate this pattern but also the power
of monads and higher-kinded types to allow such an interface to be generally described for
multiple computational contexts, e.g. Futures and States.
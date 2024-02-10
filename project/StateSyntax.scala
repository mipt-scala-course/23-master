import sbt.State
import sbt.complete.Parser

object StateSyntax {
  @annotation.tailrec
  def runCommand(command: String, state: State): State = {
    val nextState = Parser.parse(command, state.combinedParser) match {
      case Right(cmd) => cmd()
      case Left(msg) => throw sys.error(s"Invalid programmatic input:\n$msg")
    }
    nextState.remainingCommands match {
      case Nil => nextState
      case head :: tail => runCommand(head.commandLine, nextState.copy(remainingCommands = tail))
    }
  }

  def runCommandAndRemaining(command: String, st: State): State = {
    runCommand(command, st.copy(remainingCommands = Nil)).copy(remainingCommands = st.remainingCommands)
  }

}

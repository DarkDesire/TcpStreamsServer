import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Tcp.{IncomingConnection, ServerBinding}
import akka.stream.scaladsl.{Flow, Framing, Source, Tcp}
import akka.util.ByteString

import scala.concurrent.{ExecutionContextExecutor, Future}

object Main extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()


  val connections: Source[IncomingConnection, Future[ServerBinding]] =
    Tcp().bind("127.0.0.1", 8888)
  connections.runForeach { connection =>
    import connection._

    // server logic, parses incoming commands
    val commandParser = Flow[String].takeWhile(_ != "BYE").map(_ + "!")

    val welcomeMsg = s"Welcome to: $localAddress, you are: $remoteAddress!"
    val welcome = Source.single(welcomeMsg)

    val serverLogic = Flow[ByteString]
      .via(Framing.delimiter(
        ByteString("\n"),
        maximumFrameLength = 256,
        allowTruncation = true))
      .map(_.utf8String)
      // merge in the initial banner after parser
      .merge(welcome)
      .via(commandParser)
      .map(_ + "\n")
      .map(ByteString(_))

    connection.handleWith(serverLogic)
  }
}
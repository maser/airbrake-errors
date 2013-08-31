import airbrake_errors.airbrake_api.ApiClient
import airbrake_errors.airbrake_api.Project
import dispatch._, Defaults._
import org.joda.time.DateTime
import akka.actor.ActorSystem
import akka.actor.Props
import com.typesafe.config._

object AirbrakeErrors {
  trait ApiSettings {
    private val conf = ConfigFactory.load()
    val account = conf.getString("airbrake-errors.airbrake-account")
    val authToken = conf.getString("airbrake-errors.airbrake-auth-token")
  }

  val client = new ApiClient with ApiSettings

  def main(args: Array[String]): Unit = {
    listProjects()
    listErrors()
    val system = ActorSystem("airbrake")
    val actor = system.actorOf(Props[HelloWorld])
    val foo = (x: Int) ⇒ x + 5
    val map = Map(5 → "foo")
    println(map)
    system.shutdown()
  }

  def listErrors() = {
    val errors = client.errors()()
    val e = errors.head
    val notices = e.notices()()
    println(notices.filter(_.createdAt.isAfter(DateTime.now().minusMinutes(100))).length)
    
    // val projectF = e.project()
    // for (projectO <- projectF)
    //   for (project <- projectO)
    //     println(project.name + ": " + e.errorMessage)
    // projectF()

    // val allErrors = client.allErrors(3, true)()
    // println("number of errors loaded: " + allErrors.length)
    // val sortedErrors = allErrors.sortWith(_.noticesCount > _.noticesCount)
    // for (error <- sortedErrors.take(10)) {
    //   println(error.noticesCount + ": " + error.errorMessage)
    // }
  }

  def listProjects() = {
    val projects = client.projects
    val project = projects().head
    println(project.name)
  }
}

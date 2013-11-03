import airbrake_errors.airbrake_api.ApiClient
import airbrake_errors.airbrake_api.Project
import airbrake_errors.airbrake_api.Error
import airbrake_errors.airbrake_api.Notice
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

  def main(args: Array[String]): Unit = {
    val projects = listProjects
    val notices = listNotices
    projects()
    notices()
  }

  def listNotices: Future[Seq[Notice]] = {
    implicit val client = new ApiClient with ApiSettings
    val cutoffTime = DateTime.now().minusHours(2)
    val futureErrors = Error.where(_.mostRecentNoticeAt.isAfter(cutoffTime))

    futureErrors onSuccess {
      case errors ⇒
        val e = errors.head
        println("Loading notices for error " + e.errorClass + ": " + e.errorMessage)
    }

    for {
      errors ← futureErrors
      notices ← errors.head.notices(_.createdAt.isAfter(cutoffTime))
    } yield {

      println("Found " + notices.length + " notices in the last two hours")

      notices
    }
  }

  def listProjects: Future[Seq[Project]] = {
    implicit val client = new ApiClient with ApiSettings
    val allProjects = Project.all
    for {
      projects ← allProjects
    } yield for {
      project ← projects
    } println(project.name)

    allProjects
  }
}

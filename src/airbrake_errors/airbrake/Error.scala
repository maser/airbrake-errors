package airbrake_errors.airbrake_api
import org.joda.time.DateTime
import scala.concurrent.Future, scala.concurrent.ExecutionContext.Implicits.global

class Error (
  private val client: ApiClient,
  val id: BigInt,
  val projectId: Int,
  val errorClass: String,
  val errorMessage: String,
  val file: String,
  val lineNumber: Int,
  val mostRecentNoticeAt: DateTime,
  val resolved: Boolean,
  val noticesCount: Int
) {
  def project() = {
    client.project(projectId)
  }

  def notices(where: Notice ⇒ Boolean): Future[Seq[Notice]] = {
    val r = for {
      results ← client.notices(id, _.forall(where))
    } yield results.filter(where)
    r
  }
}

object Error extends XmlHelper {
  def fromXml(client: ApiClient, xml: scala.xml.Node) = {
    new Error(
      client = client,
      id = extractBigIntFromXml(xml, "id"),
      errorClass = extractFromXml(xml, "error-class"),
      errorMessage = extractFromXml(xml, "error-message"),
      file = extractFromXml(xml, "file"),
      lineNumber = extractIntFromXml(xml, "line-number"),
      mostRecentNoticeAt = extractDateFromXml(xml, "most-recent-notice-at"),
      resolved = extractFromXml(xml, "resolved") == "true",
      noticesCount = extractIntFromXml(xml, "notices-count"),
      projectId = extractIntFromXml(xml, "project-id")
    )
  }

  def where(where: Error ⇒ Boolean)(implicit client: ApiClient): Future[Seq[Error]] = {
    val showResolved = true
    client.errors(showResolved, _.forall(where))
  }
}

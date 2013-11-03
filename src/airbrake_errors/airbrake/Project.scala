package airbrake_errors.airbrake_api
import scala.concurrent.Future

/** Project
  *
  * a project
  * 
  */
class Project(
  private val client: ApiClient,
  val id: BigInt,
  val name: String
)

object Project extends XmlHelper {
  def fromXml(client: ApiClient, xml: scala.xml.Node) = {
    new Project(
      client,
      extractBigIntFromXml(xml, "id"),
      extractFromXml(xml, "name")
    )
  }

  def all(implicit client: ApiClient): Future[Seq[Project]] = {
    client.projects
  }
}

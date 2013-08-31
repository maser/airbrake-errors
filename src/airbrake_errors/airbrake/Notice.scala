package airbrake_errors.airbrake_api
import org.joda.time.DateTime

class Notice (
  private val client: ApiClient,
  val id: BigInt,
  val createdAt: DateTime,
  val groupId: Int,
  val projectId: Int
)

object Notice extends XmlHelper {
  def fromXml(client: ApiClient, xml: scala.xml.Node) = {
    new Notice(
      client = client,
      id = extractBigIntFromXml(xml, "id"),
      createdAt = extractDateFromXml(xml, "created-at"),
      groupId = extractIntFromXml(xml, "group-id"),
      projectId = extractIntFromXml(xml, "project-id")
    )
  }
}

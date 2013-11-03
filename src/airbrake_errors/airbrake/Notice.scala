package airbrake_errors.airbrake_api
import org.joda.time.DateTime

class Notice (
  private val client: ApiClient,
  val id: BigInt,
  val createdAt: DateTime,
  val errorId: BigInt,
  val projectId: Int
)

object Notice extends XmlHelper {
  def fromXml(client: ApiClient, errorId: BigInt, xml: scala.xml.Node) = {
    new Notice(
      client = client,
      id = extractBigIntFromXml(xml, "id"),
      createdAt = extractDateFromXml(xml, "created-at"),
      errorId = errorId,
      projectId = extractIntFromXml(xml, "project-id")
    )
  }
}

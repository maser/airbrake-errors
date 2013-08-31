package airbrake_errors.airbrake_api
import org.joda.time.DateTime
import scala.xml.Node

trait XmlHelper {
  // private lazy val xmlDateFormatter = {
  //   val formatter = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
  //   formatter.setTimeZone(TimeZone.getTimeZone("Z"))
  //   formatter
  // }
  
  private def parseDate(s: String): DateTime = {
    DateTime.parse(s)
  }

  protected def extractFromXml(xml: Node, field: String) = (xml \\ field).text

  protected def extractIntFromXml(xml: Node, field: String) = extractFromXml(xml, field).toInt

  protected def extractDateFromXml(xml: Node, field: String) = parseDate(extractFromXml(xml, field))

  protected def extractBigIntFromXml(xml: Node, field: String) =
    BigInt(extractFromXml(xml, field))
}

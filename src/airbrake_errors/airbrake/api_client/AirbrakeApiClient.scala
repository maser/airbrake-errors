package airbrake_errors.airbrake_api

import dispatch._, Defaults._

trait ApiClient {
  val account: String
  val authToken: String

  private val domain = "airbrake.io"

  def hostName = account + "." + domain

  def errors(showResolved: Boolean = false, continue: Seq[Error] ⇒ Boolean): Future[Seq[Error]] = {
    errorsForPage()
  }

  def errorsForPage(page: Int = 0, showResolved: Boolean = false) = {
    for {
      xml ← errorsXml(page, showResolved)
    } yield for {
      group <- xml \\ "group"
    } yield Error.fromXml(this, group)
  }

  def errorsXml(page: Int = 0, showResolved: Boolean = false) = {
    Http(errorsRequest(page, showResolved) OK as.xml.Elem)
  }

  def errorsRequest(page: Int = 0, showResolved: Boolean = false) = {
    val base = defaultUrl / "errors.xml" <<? Map("page" -> page.toString)
    if (showResolved) {
      base <<? Map("show_resolved" -> "true")
    } else {
      base
    }
  }

  def projects = {
    for {
      xml <- projectsXml
    } yield for {
      project <- xml \\ "project"
    } yield Project.fromXml(this, project)
  }

  def projectsXml = Http(projectsRequest OK as.xml.Elem)

  def projectsRequest = defaultUrl / "data_api" / "v1" / "projects.xml"

  def project(projectId: Int) = {
    for {
      allProjects <- projects
    } yield allProjects.find(_.id == projectId)
  }

  def notices(errorId: BigInt, continue: Seq[Notice] ⇒ Boolean): Future[Seq[Notice]] = {
    val results = List[Notice]()
    rnotices(errorId, 0, results, continue)
  }

  private def rnotices(errorId: BigInt, page: Int, results: Seq[Notice], continue: Seq[Notice] ⇒ Boolean): Future[Seq[Notice]] = {
    if (continue(results)) {
      noticesForPage(errorId, page) flatMap { newResults ⇒
        if (newResults.isEmpty)
          Future.successful(results)
        else
          rnotices(errorId, page + 1, results ++ newResults, continue)
      }
    } else {
      println(s"aborting on page $page")
      Future.successful(results)
    }
  }

  def noticesForPage(errorId: BigInt, page: Int = 0): Future[Seq[Notice]] = {
    for {
      xml <- noticesXml(errorId, page)
    } yield for {
      notice <- xml \\ "notice"
    } yield Notice.fromXml(this, errorId, notice)
  }

  def noticesXml(errorId: BigInt, page: Int = 0) = {
    Http(noticesRequest(errorId, page) OK as.xml.Elem)
  }

  def noticesRequest(errorId: BigInt, page: Int = 0) = {
    defaultUrl / "groups" / errorId.toString / "notices.xml" <<? Map("page" -> page.toString)
  }

  private lazy val defaultParams = Map("auth_token" -> authToken)

  private def defaultUrl = host(hostName).secure <<? defaultParams
}

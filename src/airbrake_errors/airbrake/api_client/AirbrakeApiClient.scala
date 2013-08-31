package airbrake_errors.airbrake_api

import dispatch._, Defaults._

trait ApiClient {
  val account: String
  val authToken: String

  private val domain = "airbrake.io"

  def hostName = account + "." + domain

  def errors(page: Int = 0, showResolved: Boolean = false) = {
    for {
      xml ← errorsXml(page, showResolved)
    } yield for {
      group <- xml \\ "group"
    } yield Error.fromXml(this, group)
  }

  def allErrors(maxPage: Int = 2, showResolved: Boolean = false) = {
    val pages = for (page <- 0 to maxPage) yield errors(page, showResolved)
    for (pageRequests <- Future.sequence(pages))
    yield pageRequests.flatten
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

  def notices(errorId: BigInt, page: Int = 0) = {
    for {
      xml <- noticesXml(errorId, page)
    } yield for {
      notice <- xml \\ "notice"
    } yield Notice.fromXml(this, notice)
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

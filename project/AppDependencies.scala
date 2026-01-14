import sbt.*
import sbt.Keys.dependencyOverrides

object AppDependencies {

  private val bootstrapVersion = "10.5.0"
  private val hmrcMongoVersion = "2.11.0"
  private val enumeratumVersion = "1.9.0"


  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.objectstore" %% "object-store-client-play-30"                       % "2.5.0",
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-30"                        % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc-play-30"                        % "12.25.0",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-30"                                % hmrcMongoVersion,
    "uk.gov.hmrc"             %% "centralised-authorisation-resource-client-play-30" % "1.15.0",
    "com.beachape"            %% "enumeratum-play"                                   % enumeratumVersion,
    "uk.gov.hmrc"             %% "domain-play-30"                                    % "11.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "com.github.tomakehurst" % "wiremock"                % "2.7.1" % Test,
    "org.scalatest"          %% "scalatest"              % "3.2.19"   % Test,
    "org.scalatestplus"      %% "scalacheck-1-17"        % "3.2.18.0" % Test,
    "org.scalatestplus"      %% "mockito-3-4"            % "3.2.10.0" % Test,
    "org.scalatestplus.play" %% "scalatestplus-play"     % "7.0.2"    % Test,
    "org.scalamock"          %% "scalamock"              % "7.5.2"    % Test,
    "uk.gov.hmrc"            %% "bootstrap-test-play-30" % bootstrapVersion % Test,
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-30"% hmrcMongoVersion % Test,
    "org.jsoup"               % "jsoup"                  % "1.21.1"   % Test
  )

  val it: Seq[Nothing] = Seq.empty
}

import sbt.*

object AppDependencies {

  private val bootstrapVersion = "9.13.0"
  private val hmrcMongoVersion = "2.6.0"
  private val enumeratumVersion = "1.8.2"
  private val akkaVersion = "2.6.21" // or align with Play’s version, see notes below


  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-30"                        % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc-play-30"                        % "12.5.0",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-30"                                % hmrcMongoVersion,
    "uk.gov.hmrc"             %% "centralised-authorisation-resource-client-play-30" % "1.7.0",
    "com.beachape"            %% "enumeratum-play"                                   % enumeratumVersion,
    "uk.gov.hmrc"             %% "domain-play-30"                                    % "11.0.0",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"     % bootstrapVersion            % Test,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-30"    % hmrcMongoVersion            % Test,
    "org.jsoup"               %  "jsoup"                      % "1.13.1"                    % Test,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
  )

  val it = Seq.empty
}

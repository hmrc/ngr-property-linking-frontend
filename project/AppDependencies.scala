import sbt.*

object AppDependencies {

  private val bootstrapVersion = "9.13.0"
  private val hmrcMongoVersion = "2.6.0"
  private val enumeratumVersion = "1.8.2"

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-30"                        % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc-play-30"                        % "12.2.0",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-30"                                % hmrcMongoVersion,
    "uk.gov.hmrc"             %% "centralised-authorisation-resource-client-play-30" % "1.7.0",
    "com.beachape"            %% "enumeratum-play"                                   % enumeratumVersion,
    "uk.gov.hmrc"             %% "domain-play-30"                                    % "11.0.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"     % bootstrapVersion            % Test,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-30"    % hmrcMongoVersion            % Test,
    "org.jsoup"               %  "jsoup"                      % "1.13.1"                    % Test
  )

  val it = Seq.empty
}

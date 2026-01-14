/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ngrpropertylinkingfrontend.models.sdes

import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.*

class SdesFileReturnSpec
  extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks {

  // Bring the format into scope from the companion
  import SdesFileReturn.*

  // ---------- Arbitrary generator for property-based tests ----------
  // Keep it realistic: non-empty strings, plausible content types, hex-ish checksum
  private val nonEmptyStr: Gen[String] =
    Gen.nonEmptyListOf(Gen.alphaNumChar).map(_.mkString)

  private val contentTypeGen: Gen[String] =
    Gen.oneOf(
      "application/pdf",
      "application/json",
      "text/plain",
      "image/png",
      "application/octet-stream"
    )

  private val checksumHexGen: Gen[String] =
    Gen.listOfN(64, Gen.oneOf(('0' to '9') ++ ('a' to 'f'))).map(_.mkString)

  implicit val arbSdesFileReturn: Arbitrary[SdesFileReturn] = Arbitrary {
    for {
      url         <- nonEmptyStr.map(s => s"https://example.com/$s")
      id          <- nonEmptyStr
      checksum    <- checksumHexGen
      contentType <- contentTypeGen
      nrId        <- nonEmptyStr
    } yield SdesFileReturn(
      attachmentUrl             = url,
      attachmentId              = id,
      attachmentSha256Checksum  = checksum,
      attachmentContentType     = contentType,
      nrSubmissionId            = nrId
    )
  }

  "SdesFileReturn JSON format" - {

    "write to the expected JSON shape" in {
      val model = SdesFileReturn(
        attachmentUrl            = "https://files.service/download/abc123",
        attachmentId             = "abc123",
        attachmentSha256Checksum = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef",
        attachmentContentType    = "application/pdf",
        nrSubmissionId           = "NR-0001"
      )

      val json = Json.toJson(model)
      json mustBe Json.obj(
        "attachmentUrl"            -> "https://files.service/download/abc123",
        "attachmentId"             -> "abc123",
        "attachmentSha256Checksum" -> "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef",
        "attachmentContentType"    -> "application/pdf",
        "nrSubmissionId"           -> "NR-0001"
      )
    }

    "read from valid JSON" in {
      val json = Json.obj(
        "attachmentUrl"            -> "https://files.service/download/abc123",
        "attachmentId"             -> "abc123",
        "attachmentSha256Checksum" -> "deadbeefdeadbeefdeadbeefdeadbeefdeadbeefdeadbeefdeadbeefdeadbeef",
        "attachmentContentType"    -> "application/json",
        "nrSubmissionId"           -> "NR-0002"
      )

      val result = json.validate[SdesFileReturn]
      result.isSuccess mustBe true
      val model = result.get
      model.attachmentUrl            mustBe "https://files.service/download/abc123"
      model.attachmentId             mustBe "abc123"
      model.attachmentSha256Checksum mustBe "deadbeefdeadbeefdeadbeefdeadbeefdeadbeefdeadbeefdeadbeefdeadbeef"
      model.attachmentContentType    mustBe "application/json"
      model.nrSubmissionId           mustBe "NR-0002"
    }

    "round-trip (model -> json -> model) for arbitrary values" in {
      forAll { (m: SdesFileReturn) =>
        val back = Json.toJson(m).as[SdesFileReturn]
        back mustBe m
      }
    }

    "fail to read when any required field is missing" in {
      val base = Json.obj(
        "attachmentUrl"            -> "https://example.com/x",
        "attachmentId"             -> "id1",
        "attachmentSha256Checksum" -> "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef",
        "attachmentContentType"    -> "application/pdf",
        "nrSubmissionId"           -> "NR-123"
      )

      val requiredFields = List(
        "attachmentUrl",
        "attachmentId",
        "attachmentSha256Checksum",
        "attachmentContentType",
        "nrSubmissionId"
      )

      requiredFields.foreach { field =>
        val bad = base - field
        val res = bad.validate[SdesFileReturn]
        withClue(s"Expected JsError when missing '$field'") {
          res.isError mustBe true
        }
      }
    }

    "ignore unknown fields when reading" in {
      val jsonWithUnknown = Json.obj(
        "attachmentUrl"            -> "https://example.com/x",
        "attachmentId"             -> "id2",
        "attachmentSha256Checksum" -> "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
        "attachmentContentType"    -> "text/plain",
        "nrSubmissionId"           -> "NR-999",
        "extraField"               -> "ignored"
      )

      val parsed = jsonWithUnknown.validate[SdesFileReturn]
      parsed.isSuccess mustBe true
      parsed.get mustBe SdesFileReturn(
        "https://example.com/x",
        "id2",
        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
        "text/plain",
        "NR-999"
      )
    }
  }
}


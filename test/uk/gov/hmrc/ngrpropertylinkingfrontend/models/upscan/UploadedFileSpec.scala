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

package uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json._

import java.time.ZonedDateTime
import org.scalacheck.{Arbitrary, Gen}

class UploadedFileSpec
  extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks {

  import UploadedFile.formats // bring Format[UploadedFile] into scope

  private val ts: ZonedDateTime = ZonedDateTime.parse("2026-01-12T10:30:00Z")

  "UploadedFile JSON format" - {

    "writes the expected JSON including optional fields when present" in {
      val model = UploadedFile(
        ref              = "ref-001",
        upscanReference  = "upscan-abc",
        downloadUrl      = "https://object-store/bucket/key",
        uploadTimestamp  = ts,
        fileSize         = 123456,
        cargo            = Some(Json.obj("meta" -> "value", "tags" -> Json.arr("a", "b"))),
        description      = Some("Sample file"),
        previewUrl       = Some("https://preview/service/123")
      )

      val json = Json.toJson(model)
      json mustBe Json.obj(
        "ref"             -> "ref-001",
        "upscanReference" -> "upscan-abc",
        "downloadUrl"     -> "https://object-store/bucket/key",
        "uploadTimestamp" -> ts, // JavaTimeFormats encodes ZonedDateTime as ISO‑8601
        "fileSize"        -> 123456,
        "cargo"           -> Json.obj("meta" -> "value", "tags" -> Json.arr("a", "b")),
        "description"     -> "Sample file",
        "previewUrl"      -> "https://preview/service/123"
      )
    }

    "writes the expected JSON omitting optional fields when None" in {
      val model = UploadedFile(
        ref              = "ref-002",
        upscanReference  = "upscan-def",
        downloadUrl      = "https://object-store/bucket/key2",
        uploadTimestamp  = ts,
        fileSize         = 42,
        cargo            = None,
        description      = None,
        previewUrl       = None
      )

      val json = Json.toJson(model).as[JsObject]
      json mustBe Json.obj(
        "ref"             -> "ref-002",
        "upscanReference" -> "upscan-def",
        "downloadUrl"     -> "https://object-store/bucket/key2",
        "uploadTimestamp" -> ts,
        "fileSize"        -> 42
      )

      // sanity: optional keys are absent
      json.keys must not contain ("cargo")
      json.keys must not contain ("description")
      json.keys must not contain ("previewUrl")
    }

    "reads valid JSON back to a model" in {
      val json = Json.obj(
        "ref"             -> "ref-003",
        "upscanReference" -> "upscan-ghi",
        "downloadUrl"     -> "https://object-store/bucket/key3",
        "uploadTimestamp" -> "2026-01-12T10:30:00Z",
        "fileSize"        -> 999,
        "description"     -> "Another file"
      )

      val parsed = json.validate[UploadedFile]
      parsed.isSuccess mustBe true
      val m = parsed.get
      m.ref             mustBe "ref-003"
      m.upscanReference mustBe "upscan-ghi"
      m.downloadUrl     mustBe "https://object-store/bucket/key3"
      m.uploadTimestamp mustBe ZonedDateTime.parse("2026-01-12T10:30:00Z")
      m.fileSize        mustBe 999
      m.description     mustBe Some("Another file")
      m.cargo           mustBe None
      m.previewUrl      mustBe None
    }

    "round-trip (model -> json -> model) for arbitrary instances" in {
      // Arbitrary generators (keep them simple & realistic)
      val nonEmptyStr: Gen[String] =
        Gen.nonEmptyListOf(Gen.alphaNumChar).map(_.mkString)

      val urlStr: Gen[String] =
        nonEmptyStr.map(s => s"https://example.com/$s")

      val descriptionGen: Gen[Option[String]] =
        Gen.option(nonEmptyStr)

      val previewUrlGen: Gen[Option[String]] =
        Gen.option(urlStr)

      val cargoGen: Gen[Option[JsValue]] = {
        val js =
          for {
            key <- nonEmptyStr
            valStr <- nonEmptyStr
          } yield Json.obj(key -> valStr)
        Gen.option(js)
      }

      val zonedGen: Gen[ZonedDateTime] = {
        // pick an ISO instant and force UTC zone
        val epochSecondsGen = Gen.choose(0L, 4102444800L) // up to year ~2100
        epochSecondsGen.map(sec => ZonedDateTime.ofInstant(java.time.Instant.ofEpochSecond(sec), java.time.ZoneOffset.UTC))
      }

      implicit val arb: Arbitrary[UploadedFile] = Arbitrary {
        for {
          ref       <- nonEmptyStr
          upRef     <- nonEmptyStr
          dl        <- urlStr
          ts        <- zonedGen
          size      <- Gen.choose(0, 100000000) // up to 100MB
          cargo     <- cargoGen
          desc      <- descriptionGen
          preview   <- previewUrlGen
        } yield UploadedFile(ref, upRef, dl, ts, size, cargo, desc, preview)
      }

      forAll { (m: UploadedFile) =>
        val back = Json.toJson(m).as[UploadedFile]
        back mustBe m
      }
    }

    "fails to read when any required field is missing" in {
      val base = Json.obj(
        "ref"             -> "ref-004",
        "upscanReference" -> "upscan-jkl",
        "downloadUrl"     -> "https://object-store/bucket/key4",
        "uploadTimestamp" -> "2026-01-12T10:30:00Z",
        "fileSize"        -> 2048
      )

      val required = List("ref", "upscanReference", "downloadUrl", "uploadTimestamp", "fileSize")
      required.foreach { k =>
        val bad = base - k
        val res = bad.validate[UploadedFile]
        withClue(s"Missing required field '$k' should yield JsError") {
          res.isError mustBe true
        }
      }
    }

    "ignores unknown fields on reads" in {
      val jsonWithUnknown = Json.obj(
        "ref"             -> "ref-005",
        "upscanReference" -> "upscan-mno",
        "downloadUrl"     -> "https://object-store/bucket/key5",
        "uploadTimestamp" -> "2026-01-12T10:30:00Z",
        "fileSize"        -> 1,
        "unknown"         -> "ignored",
        "anotherOne"      -> 123
      )

      jsonWithUnknown.as[UploadedFile] mustBe UploadedFile(
        ref             = "ref-005",
        upscanReference = "upscan-mno",
        downloadUrl     = "https://object-store/bucket/key5",
        uploadTimestamp = ZonedDateTime.parse("2026-01-12T10:30:00Z"),
        fileSize        = 1,
        cargo           = None,
        description     = None,
        previewUrl      = None
      )
    }

    "reads and writes cargo (arbitrary JsValue) correctly" in {
      val cargo = Json.obj("foo" -> "bar", "nested" -> Json.obj("x" -> 1))
      val m = UploadedFile(
        ref             = "ref-006",
        upscanReference = "upscan-pqr",
        downloadUrl     = "https://object-store/bucket/key6",
        uploadTimestamp = ts,
        fileSize        = 12,
        cargo           = Some(cargo),
        description     = None,
        previewUrl      = None
      )

      val json = Json.toJson(m)
      (json \ "cargo").asOpt[JsValue] mustBe Some(cargo)

      val back = json.as[UploadedFile]
      back mustBe m
    }
  }
}

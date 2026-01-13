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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.*




class FileNotificationRequestSpec extends AnyWordSpec with Matchers {

  "Algorithm.apply" should {
    "map known strings to the correct Algorithm values" in {
      Algorithm("md5")      shouldBe MD5
      Algorithm("SHA1")     shouldBe SHA1
      Algorithm("SHA2")     shouldBe SHA2
      Algorithm("SHA-256")  shouldBe SHA256
      Algorithm("SHA-512")  shouldBe SHA512
    }

    "throw IllegalArgumentException for unknown strings" in {
      intercept[IllegalArgumentException] {
        Algorithm("SHA3")
      }
      intercept[IllegalArgumentException] {
        Algorithm("sha-256")
      }
      intercept[IllegalArgumentException] {
        Algorithm("MD5")
      }
      intercept[IllegalArgumentException] {
        Algorithm("")
      }
      intercept[IllegalArgumentException] {
        Algorithm("   ")
      }
    }
  }

  "Algorithm JSON Writes" should {
    "write MD5/SHA1/SHA2/SHA-256/SHA-512 to the expected string values" in {
      Json.toJson(MD5)(Algorithm.writes)    shouldBe JsString("md5")
      Json.toJson(SHA1)(Algorithm.writes)   shouldBe JsString("SHA1")
      Json.toJson(SHA2)(Algorithm.writes)   shouldBe JsString("SHA2")
      Json.toJson(SHA256)(Algorithm.writes) shouldBe JsString("SHA-256")
      Json.toJson(SHA512)(Algorithm.writes) shouldBe JsString("SHA-512")
    }
  }

  "Algorithm JSON Reads" should {
    "read supported string values into Algorithm ADT" in {
      JsString("md5").validate[Algorithm]     shouldBe JsSuccess(MD5)
      JsString("SHA1").validate[Algorithm]    shouldBe JsSuccess(SHA1)
      JsString("SHA2").validate[Algorithm]    shouldBe JsSuccess(SHA2)
      JsString("SHA-256").validate[Algorithm] shouldBe JsSuccess(SHA256)
      JsString("SHA-512").validate[Algorithm] shouldBe JsSuccess(SHA512)
    }

    "fail for unexpected values (case or content mismatch)" in {
      JsString("MD5").validate[Algorithm].isError     shouldBe true
      JsString("sha1").validate[Algorithm].isError    shouldBe true
      JsString("SHA3").validate[Algorithm].isError    shouldBe true
      JsString("sha-256").validate[Algorithm].isError shouldBe true
      JsNull.validate[Algorithm].isError              shouldBe true
      JsNumber(123).validate[Algorithm].isError       shouldBe true
    }
  }

  "Checksum JSON format" should {
    "round-trip to/from JSON" in {
      val checksum = Checksum(algorithm = SHA256, value = "012345abcdef")
      val json     = Json.toJson(checksum)(Checksum.format)

      json shouldBe Json.obj(
        "algorithm" -> JsString("SHA-256"),
        "value"     -> JsString("012345abcdef")
      )

      json.validate[Checksum] shouldBe JsSuccess(checksum)
    }

    "fail validation if algorithm string is unexpected" in {
      val badJson = Json.obj(
        "algorithm" -> "SHA3",
        "value"     -> "abc"
      )
      badJson.validate[Checksum].isError shouldBe true
    }
  }

  "File JSON format" should {
    "round-trip optional fields and properties list correctly" in {
      val file = File(
        recipientOrSender = Some("SRN-00001"),
        name              = "evidence.pdf",
        location          = Some("https://object-store/bucket/key"),
        checksum          = Checksum(SHA512, "deadbeef"),
        size              = 1024,
        properties        = List(Property("key1", "value1"), Property("key2", "value2"))
      )

      val json = Json.toJson(file)(File.format)
      // Basic presence checks
      (json \ "recipientOrSender").asOpt[String] shouldBe Some("SRN-00001")
      (json \ "name").as[String]                 shouldBe "evidence.pdf"
      (json \ "location").asOpt[String]          shouldBe Some("https://object-store/bucket/key")
      (json \ "checksum" \ "algorithm").as[String] shouldBe "SHA-512"
      (json \ "checksum" \ "value").as[String]     shouldBe "deadbeef"
      (json \ "size").as[Int]                    shouldBe 1024
      (json \ "properties").as[List[JsValue]].size shouldBe 2

      json.validate[File] shouldBe JsSuccess(file)
    }

    "handle None for optional fields and empty properties list" in {
      val file = File(
        recipientOrSender = None,
        name              = "doc.txt",
        location          = None,
        checksum          = Checksum(MD5, "md5sum"),
        size              = 42,
        properties        = Nil
      )

      val json = Json.toJson(file)(File.format)

      (json \ "recipientOrSender").toOption shouldBe None
      (json \ "location").toOption          shouldBe None
      (json \ "name").as[String]            shouldBe "doc.txt"
      (json \ "checksum" \ "algorithm").as[String] shouldBe "md5"
      (json \ "size").as[Int]               shouldBe 42
      (json \ "properties").as[List[JsValue]] shouldBe Nil

      json.validate[File] shouldBe JsSuccess(file)
    }

    "fail if required fields are missing (e.g., name/size/checksum)" in {
      val missingNameJson = Json.obj(
        "recipientOrSender" -> "SRN-9",
        "location"          -> "https://obj/key",
        "checksum"          -> Json.obj("algorithm" -> "SHA-256", "value" -> "abc"),
        "size"              -> 1,
        "properties"        -> Json.arr()
      ) - "name"

      missingNameJson.validate[File].isError shouldBe true

      val missingChecksumJson = Json.obj(
        "recipientOrSender" -> "SRN-9",
        "name"              -> "x",
        "location"          -> "https://obj/key",
        "size"              -> 1,
        "properties"        -> Json.arr()
      )

      missingChecksumJson.validate[File].isError shouldBe true
    }
  }

  "Audit JSON format" should {
    "round-trip correlationID" in {
      val audit = Audit(correlationID = "corr-123")
      val json  = Json.toJson(audit)(Audit.format)

      (json \ "correlationID").as[String] shouldBe "corr-123"
      json.validate[Audit]  shouldBe JsSuccess(audit)
    }

    "fail if correlationID is missing" in {
      val badJson = Json.obj()
      badJson.validate[Audit].isError shouldBe true
    }
  }

  "FileTransferNotification JSON format" should {
    "round-trip full structure" in {
      val ft = FileTransferNotification(
        informationType = "NRS",
        file = File(
          recipientOrSender = Some("SRN-A"),
          name              = "submission.pdf",
          location          = Some("https://object-store/path"),
          checksum          = Checksum(SHA2, "beadfeed"),
          size              = 2048,
          properties        = List(Property("nrsSubmissionId", "sub-001"))
        ),
        audit = Audit(correlationID = "corr-987")
      )

      val json = Json.toJson(ft)(FileTransferNotification.format)

      (json \ "informationType").as[String] shouldBe "NRS"
      (json \ "file" \ "name").as[String]   shouldBe "submission.pdf"
      (json \ "audit" \ "correlationID").as[String] shouldBe "corr-987"

      json.validate[FileTransferNotification]  shouldBe JsSuccess(ft)
    }

    "fail if required nested objects are missing" in {
      val badJsonMissingAudit = Json.obj(
        "informationType" -> "NRS",
        "file" -> Json.obj(
          "recipientOrSender" -> "SRN-A",
          "name"              -> "submission.pdf",
          "location"          -> "https://object-store/path",
          "checksum"          -> Json.obj("algorithm" -> "SHA2", "value" -> "beadfeed"),
          "size"              -> 2048,
          "properties"        -> Json.arr(Json.obj("key" -> "nrsSubmissionId", "value" -> "sub-001"))
        )
      )

      badJsonMissingAudit.validate[FileTransferNotification].isError shouldBe true
    }
  }

  "Algorithm JSON round-trip" should {
    "serialize then deserialize to the same value (all variants)" in {
      val algos = List(MD5, SHA1, SHA2, SHA256, SHA512)
      algos.foreach { a =>
        val js = Json.toJson(a)(Algorithm.writes)
        js.validate[Algorithm]shouldBe JsSuccess(a)
      }
    }
  }
}


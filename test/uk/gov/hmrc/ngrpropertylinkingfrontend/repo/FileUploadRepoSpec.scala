/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.ngrpropertylinkingfrontend.repo

import org.bson.types.ObjectId
import uk.gov.hmrc.http.StringContextOps
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.{TestData, TestSupport}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.{Reference, UploadDetails, UploadId, UploadStatus}

class FileUploadRepoSpec extends TestSupport with TestData {

  "Serialization and deserialization of UploadDetails" should {

    "serialize and deserialize InProgress status" in {

      val input = UploadDetails(ObjectId.get(), UploadId.generate(), Reference("ABC"), UploadStatus.InProgress)

      val serialized = FileUploadRepo.mongoFormat.writes(input)
      val output = FileUploadRepo.mongoFormat.reads(serialized)

      output.get mustBe input

    }

    "serialize and deserialize Failed status" in {

      val input = UploadDetails(ObjectId.get(), UploadId.generate(), Reference("ABC"), UploadStatus.Failed)

      val serialized = FileUploadRepo.mongoFormat.writes(input)
      val output = FileUploadRepo.mongoFormat.reads(serialized)

      output.get mustBe input

    }

    "serialize and deserialize UploadedSuccessfully status when size is unknown" in {
      val input = UploadDetails(
        ObjectId.get(),
        UploadId.generate(),
        Reference("ABC"),
        UploadStatus.UploadedSuccessfully("foo.txt", "text/plain", url"http:localhost:8080", size = None)
      )

      val serialized = FileUploadRepo.mongoFormat.writes(input)
      val output = FileUploadRepo.mongoFormat.reads(serialized)
      
      output.get mustBe input
    }
    
    "serialize and deserialize UploadedSuccessfully status when size is known" in {
      val input = UploadDetails(
        ObjectId.get(),
        UploadId.generate(),
        Reference("ABC"),
        UploadStatus.UploadedSuccessfully("foo.txt", "text/plain", url"http:localhost:8080", size = Some(123456))
      )

      val serialized = FileUploadRepo.mongoFormat.writes(input)
      val output = FileUploadRepo.mongoFormat.reads(serialized)

      output.get mustBe input
    }
  }
}

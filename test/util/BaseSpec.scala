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

package util

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Span}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

abstract class BaseSpec extends AnyFreeSpec with MockFactory with Matchers with ScalaFutures with BeforeAndAfterEach with BeforeAndAfterAll {

  implicit val actorSystem: ActorSystem = ActorSystem("unit-tests")
  implicit val mat: Materializer        = Materializer.createMaterializer(actorSystem)

  override protected def afterAll(): Unit =
    actorSystem.terminate()

  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = Span(1000, Millis), interval = Span(50, Millis))
}

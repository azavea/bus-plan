package com.azavea.busplan.routing

import org.scalatest._

// class SerializationSpec extends FunSpec with Matchers {
//   describe("Serialization") {
//     it("should serialize and deserialize a target map type") {
//       val expected: Map[(String, Int), List[(String, Double)]] =
//         Map(
//           ("student1", 6) -> List(("stop1", 0.5), ("stop2", 0.6)),
//           ("student2", 6) -> List(("stop3", 1.6), ("stop1", 0.5))
//         )

//       val bytes = Serialization.serialize(expected)
//       val actual = Serialization.deserialize[Map[(String, String), List[(String, Double)]]](bytes)

//       actual(("student2", 6)).head should be (("stop3", 1.6))
//     }
//   }
// }

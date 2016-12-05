package justin.db.versioning

import justin.consistent_hashing.NodeId
import justin.db.versioning.DataVersioning.NodeIdVectorClock
import justin.vector_clocks.{Counter, VectorClock}
import org.scalatest.{FlatSpec, Matchers}

class NodeIdVectorClockBase64Test extends FlatSpec with Matchers {

  behavior of "Base64 decoder/encoder of Justin Vector Clock"

  it should "encode vector clock to string and decode it back to same init vector clock" in {
    // given
    val vcBase64 = new NodeIdVectorClockBase64

    val initVClock = VectorClock(Map(NodeId(1) -> Counter(1), NodeId(2) -> Counter(2), NodeId(3) -> Counter(9)))

    // when
    val encoded: String            = vcBase64.encode(initVClock).get
    val decoded: NodeIdVectorClock = vcBase64.decode(encoded).get

    // then
    decoded shouldBe initVClock
  }
}

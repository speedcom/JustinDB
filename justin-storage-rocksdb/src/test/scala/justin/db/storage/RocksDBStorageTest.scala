package justin.db.storage

import java.nio.file.Files
import java.util.UUID

import justin.db.storage.PluggableStorageProtocol.{Ack, DataOriginality, StorageGetData}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class RocksDBStorageTest extends FlatSpec with Matchers  with ScalaFutures {

  behavior of "RocksDBStorage"

  it should "save 3 payloads and read them" in {
    val journal = Files.createTempDirectory("rocksdb")
    val rocksdb = new RocksDBStorage(journal.toFile)
    val data1 = JustinData(
      id        = UUID.randomUUID,
      value     = "1",
      vclock    = "vclock-value",
      timestamp = 1234124L
    )
    val data2 = JustinData(
      id        = UUID.randomUUID,
      value     = "1",
      vclock    = "vclock-value",
      timestamp = 1234124L
    )
    val data3 = JustinData(
      id        = UUID.randomUUID,
      value     = "3",
      vclock    = "vclock-value",
      timestamp = 1234124L
    )
    val dataOriginality = DataOriginality.Primary(ringPartitionId = 1)

    // PUT
    rocksdb.put(data1)(_ => dataOriginality).futureValue shouldBe Ack
    rocksdb.put(data2)(_ => dataOriginality).futureValue shouldBe Ack
    rocksdb.put(data3)(_ => dataOriginality).futureValue shouldBe Ack

    // GET
    rocksdb.get(data3.id)(_ => dataOriginality).futureValue shouldBe StorageGetData.Single(data3)
    rocksdb.get(data2.id)(_ => dataOriginality).futureValue shouldBe StorageGetData.Single(data2)
    rocksdb.get(data1.id)(_ => dataOriginality).futureValue shouldBe StorageGetData.Single(data1)
  }

  override implicit def patienceConfig: PatienceConfig = PatienceConfig(10.seconds, 50.millis)
}

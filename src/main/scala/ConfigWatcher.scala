import java.io.File
import java.util.concurrent.TimeUnit

import com.twitter.util.{Timer, Duration}
import com.twitter.zk.ZkClient
import org.apache.zookeeper.{KeeperException, WatchedEvent, Watcher}

import Util._

/**
 * Created by aihe on 8/14/15.
 */
class ConfigWatcher(connection: String, dirStr: String) extends Watcher {

  val zkc: ZkClient = ZkClient(connection, Duration(5, TimeUnit.SECONDS))(Timer.Nil)
  val zk = zkc.apply()

  def run(): Unit = {
    val value = read(PATH, this)
    value.map(s => {
      val dir = new File(dirStr)
      if (!dir.exists()) {
        dir.mkdir()
      }
      for (f <- dir.listFiles()) {
        f.delete()
      }
      Util.unZip(s, dir)
    })
  }

  def read(path: String, watcher: Watcher) = {
    zk.map(z => z.getData(path, watcher, null))
  }

  override def process(event: WatchedEvent): Unit = {
    if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
      try {
        run()
      } catch {
        case e: InterruptedException => {
          println("Interrupted. Exiting")
          Thread.currentThread().interrupt()
        }
        case e: KeeperException => {
          println(s"KeeperException: $e Exiting")
        }
      }
    }
  }
}

object ConfigWatcher {
  def main(args: Array[String]) {
    require(args.size == 2)
    new ConfigWatcher(args.head, args(1)).run()

    Thread.sleep(Long.MaxValue)
  }
}

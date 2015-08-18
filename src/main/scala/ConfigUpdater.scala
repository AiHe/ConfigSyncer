import java.io.File
import java.util.concurrent.TimeUnit

import com.twitter.util.Duration
import net.contentobjects.jnotify.{JNotifyListener, JNotify}
import org.apache.zookeeper.{CreateMode, ZooDefs}

import com.twitter.zk.ZkClient

import com.twitter.util.Timer

import Util._


/**
 * Created by aihe on 8/14/15.
 */
class ConfigUpdater(connection: String, dirStr: String) {

  val zkc: ZkClient = ZkClient(connection, Duration(5, TimeUnit.SECONDS))(Timer.Nil)
  val zk = zkc.apply()

  def watch(): Unit = {
    JNotify.addWatch(dirStr, JNotify.FILE_ANY, false, Listener)
  }

  object Listener extends JNotifyListener {

    def fileRenamed(wd: Int, rootPath: String, oldName: String , newName: String ) {
      println("renamed " + rootPath + " : " + oldName + " -> " + newName)
      process()
    }

    def fileModified(wd: Int, rootPath: String, name: String ) {
      println("modified " + rootPath + " : " + name)
      process()
    }

    def fileDeleted(wd: Int, rootPath: String, name: String ) {
      println("deleted " + rootPath + " : " + name)
      process()
    }

    def fileCreated(wd: Int, rootPath: String, name: String ) {
      println("created " + rootPath + " : " + name)
      process()
    }
  }

  def process(): Unit = {
    val dir = new File(dirStr)
    require(dir.exists() && dir.isDirectory)
    val s = zip(dir.listFiles().filter(f => !f.getName.startsWith(".")))
    println(s)
    write(PATH, s)
  }

  def run(): Unit = {
    process()
    watch()
  }

  def write(path: String, value: Array[Byte]): Unit = {
    zk.map(z => {
      val stat = z.exists(path, false)
      if (stat == null) {
        z.create(path, value, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)
      } else {
        z.setData(path, value, -1)
      }
    })
  }
}

object ConfigUpdater {
  def main(args: Array[String]) {
    require(args.size == 3)
    new ConfigUpdater(args.head, args(1)).run()

    Thread.sleep(Long.MaxValue)
  }
}

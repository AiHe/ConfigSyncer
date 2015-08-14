import java.io._
import java.util.zip.{ZipInputStream, ZipEntry, ZipOutputStream}

/**
 * Created by aihe on 8/14/15.
 */
object Util {

  val PATH = "/config"

  def zip(files: Iterable[File]): Array[Byte] = {

    val baos = new ByteArrayOutputStream()
    val zip = new ZipOutputStream(baos)

    files.foreach { f =>
      zip.putNextEntry(new ZipEntry(f.getName))
      val in = new BufferedInputStream(new FileInputStream(f.getAbsolutePath))
      var b = in.read()
      while (b > -1) {
        zip.write(b)
        b = in.read()
      }
      in.close()
      zip.closeEntry()
    }
    zip.close()
    baos.toByteArray
  }

  def unZip(str: Array[Byte], dir: File): Unit = {

    val buffer = new Array[Byte](1024)
    try {
      val zis: ZipInputStream = new ZipInputStream(new ByteArrayInputStream(str))
      var ze: ZipEntry = zis.getNextEntry()

      while (ze != null) {
        val fileName = ze.getName()
        val newFile = new File(dir + File.separator + fileName)

        println("file unzip : " + newFile.getAbsoluteFile())

        new File(newFile.getParent()).mkdirs()
        val fos = new FileOutputStream(newFile)
        var len: Int = zis.read(buffer);

        while (len > 0) {
          fos.write(buffer, 0, len)
          len = zis.read(buffer)
        }
        fos.close()
        ze = zis.getNextEntry()
      }
      println("file unzip : " + ("-------"*10))
      zis.closeEntry()
      zis.close()
    } catch {
      case e: IOException => println("exception caught: " + e.getMessage)
    }

  }

  def main(args: Array[String]) {
//    zip(new File("/Users/aihe/Documents/spark-1.4.1-bin-hadoop2.4/conf").listFiles().filter(f =>
//      !f.getName.startsWith(".")))

//    val INPUT_ZIP_FILE: String = "try.zip"
//    val OUTPUT_FOLDER: String = "try"
//    unZip(INPUT_ZIP_FILE, OUTPUT_FOLDER)
  }
}


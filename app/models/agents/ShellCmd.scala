package models.agents
import java.io.File
import org.apache.commons.io.FileUtils
import play.api.Logger
import sys.process._

object ShellCmd extends App {

  def createDir(dirPath:String):Boolean = {

    val dir = new File(dirPath)
    if(dir.exists()){
      if(dir.isFile) {
        Logger.warn(s"Error while creating directory: $dir because a file already exists by same name")
        false
      } else {
        Logger.info(s"Dir $dir already exists. Will not try to re-create.")
        true
      }
    } else {
      try {
        dir.mkdirs()
        Logger.info(s"Successfully created dir $dir.")
        true
      } catch {
        case ex:Exception =>
          Logger.info(s"Exception while trying to create dir: $dir. " + ex.getMessage)
          ex.printStackTrace()
          false
      }
    }
  }

  def copyLcpLogs(logsTempLocation:String):Boolean = {
    if(createDir(logsTempLocation)) {
      val lcpLogsLocation = Constants.LCP_DIR + "/logs/"

      try {
        FileUtils.copyDirectory(new File(lcpLogsLocation), new File(logsTempLocation))
        true
      } catch {
        case ex:Exception =>
          println("Exception while copying lcp logs. " + ex.getMessage)
          ex.printStackTrace()
          false
      }
    } else {
      false
    }
  }

  def getPIDByProcessName(pname:String):List[Int] = {
    try {
      val ids = s"pgrep -f $pname".!!
      ids.trim.split("\n").map(x => x.trim.toInt).toList
    } catch {
      case ex:Exception =>
        println(s"Exception in getPIDByProcessName. Pname=$pname")
        List()
    }
  }

  def killProcessByName(pname:String) = {
    try {
      val pidList = getPIDByProcessName(pname)
      for(p <- pidList) {
        Logger.info(s"Trying to kill Killing process-id $p")
        if(ShellCmd.killProcess(p)) {
          Logger.info(s"Successfully killed process $pname, pid=$p")
        } else {
          Logger.error(s"Failed to kill process $pname, pid=$p")

        }
      }

    } catch {
      case ex:Exception =>
        println("Exception while getting lcp process id to generate heap dump")

    }
  }

  def killProcess(pid: Int):Boolean = {
    try {
      s"kill $pid".! == 0
    } catch {
      case ex:Exception =>
        println(s"Exception while trying to kill process $pid. " + ex.getMessage)
        ex.printStackTrace()
        false
    }
  }

}


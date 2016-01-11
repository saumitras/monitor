package object Constants {
  //TODO: move these to either app.conf or H2 conf

  val DEFAULT_MONITOR_DB = "jdbc:h2:tcp://localhost/monitordb"
  val DEFAULT_EMAIL_DB = "jdbc:h2:tcp://localhost/emaildb"
  val DEFAULT_LCP_DB = ""// "jdbc:h2:tcp://localhost/gbmonitor"
  val DEFAULT_ZK_HOST = "" //localhost:2181"
  val DEFAULT_L2_ESCALATION_TIME = "720"
  val DEFAULT_L2_MAIL_RECIPIENTS = "saumitra.srivastav@glassbeam.com"
  val DEFAULT_L3_ESCALATION_TIME = "240"
  val DEFAULT_L3_MAIL_RECIPIENTS = "saumitra.srivastav@glassbeam.com"
  val FEEDBACK_RECIPIENT = Seq("saumitra.srivastav@glassbeam.com", "saumitra.srivastav7@gmail.com")
  val FEEDBACK_SCREENSHOT_PATH = "/tmp/"
  val MAIL_PROVIDER = "aws" //can be GMAIL(for testing) or AWS-SES

  //info used by agents
  val NODE_IP = "127.0.0.1"
  val S3_BUCKET = "gb-s3-dev"
  val S3_BASE_PATH = "monitor/agents/data/"
  val monitorLogsPath = System.getProperty("user.dir") + "/upload/"
  val LCP_DIR = "/home/sam/projects/lcp/gbnewplatform/gbnewplatform-package/target/universal/gbnewplatform-package-4.7.0.57/"
  val LCP_PROCESS_NAME = "com.glassbeam.loader.Loader"

}

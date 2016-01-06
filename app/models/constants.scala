package object Constants {
  val DEFAULT_MONITOR_DB = "jdbc:h2:tcp://localhost/monitordb"
  val DEFAULT_EMAIL_DB = "jdbc:h2:tcp://localhost/emaildb"
  val DEFAULT_LCP_DB = ""// "jdbc:h2:tcp://localhost/gbmonitor"
  val DEFAULT_ZK_HOST = "" //localhost:2181"
  val DEFAULT_L2_ESCALATION_TIME = "240"
  val DEFAULT_L3_ESCALATION_TIME = "720"
  val FEEDBACK_RECIPIENT = Seq("saumitra.srivastav@glassbeam.com", "saumitra.srivastav7@gmail.com")
  val MAIL_PROVIDER = "AWS" //can be GMAIL or SES
}

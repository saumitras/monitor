package models.dao

import play.api.Logger
import com.mchange.v2.c3p0.ComboPooledDataSource
import org.apache.solr.client.solrj.impl.CloudSolrClient
import scala.slick.driver.H2Driver.backend.DatabaseDef

import scala.slick.driver.H2Driver.simple._

object Connections {
  private final val driver = "org.h2.Driver"
  private var databases = Map[String, DatabaseDef]()  //key is h2 url
  private var solr = Map[String,CloudSolrClient]()    //key is zkHost

  def getH2(h2Url: String):DatabaseDef = {
    Logger.info("Requesting connection for H2 " + h2Url)
    if(! databases.contains(h2Url))
      initH2(h2Url)

    databases(h2Url)
  }

  def getSolr(zkHost:String) = {

  }

  private def initH2(h2Url: String) = {
    val ds = new ComboPooledDataSource
    ds.setDriverClass(driver)
    ds.setJdbcUrl(h2Url)
    ds.setMinPoolSize(10)
    ds.setAcquireIncrement(5)
    ds.setMaxPoolSize(100)
    //ds.setMaxStatements(1000)
    //ds.setMaxStatementsPerConnection(100)
    // ds.setUser("")
    databases =  databases ++ Map(h2Url -> Database.forDataSource(ds))
  }
}



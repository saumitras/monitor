package models.solr

import models.solr.Messages.CollectionStat
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.framework.recipes.cache._
import org.apache.curator.retry.ExponentialBackoffRetry
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.JsonMethods.{parse => jparse}
import play.Logger
import play.api.Play

object ZkWatcher {

  import models.solr.Messages.SolrCollectionState._

  //implicit val formats = Serialization.formats(NoTypeHints)
  implicit val formats = DefaultFormats

  private val ZKHOSTS_EXPLORER = Play.current.configuration.getString("zk_host_explorer") match {case Some(x) => x case None => ""}
  private val ZKHOSTS_LOGVAULT = Play.current.configuration.getString("zk_host_logvault") match {case Some(x) => x case None => ""}
  private val ZK_CLUSTERSTATE_FILE = "/collections"
  private val retryPolicy = new ExponentialBackoffRetry(1000, 3)

  private val clientExplorer = CuratorFrameworkFactory.newClient(ZKHOSTS_EXPLORER, retryPolicy)
  private val clientLogvault = CuratorFrameworkFactory.newClient(ZKHOSTS_LOGVAULT, retryPolicy)
  private var explorerCollections = scala.collection.immutable.HashMap[String, CollectionStat]()
  private var logvaultCollections = scala.collection.immutable.HashMap[String, CollectionStat]()

  def initExplorerZkWatcher() = {
    Logger.info(s"Registering zookeeper watcher for explorer. ZKHOST = $ZKHOSTS_EXPLORER ")
    clientExplorer.start
    clientExplorer.getZookeeperClient.blockUntilConnectedOrTimedOut
    val app = "EXPLORER"

    val clusterStateData = clientExplorer.getData.forPath(ZK_CLUSTERSTATE_FILE)
    if (clusterStateData != null) {
      val data = new String(clusterStateData)
      if (data.length > 2) processZkClusterState(data,app) else processZkClusterState( """{}""",app)
    }

    val clusterStateCache:TreeCache = new TreeCache(clientExplorer,ZK_CLUSTERSTATE_FILE)
    clusterStateCache.getListenable.addListener(new TreeCacheListener {
      override def childEvent(client: CuratorFramework, event: TreeCacheEvent): Unit = {

        try {
          if(event.getData != null) {
            val path = event.getData.getPath
            if (path.matches(".*state.json")) {
              val data = new String(event.getData.getData)
              processZkClusterState(data,app)
            }
          }

        } catch {
          case ex: Exception =>
            ex.printStackTrace()
            Logger.debug(s"[SolrLog] Exception while watching change for $app zk node $ZK_CLUSTERSTATE_FILE. Reason: " + ex.getLocalizedMessage)
        }
      }
    })
    clusterStateCache.start()

  }



  def initLogvaultZkWatcher() = {
    val app = "LOGVAULT"
    Logger.info(s"Registering zookeeper watcher for logvault. ZKHOST = $ZKHOSTS_LOGVAULT")
    clientLogvault.start
    clientLogvault.getZookeeperClient.blockUntilConnectedOrTimedOut

    val clusterStateData = clientLogvault.getData.forPath(ZK_CLUSTERSTATE_FILE)
    if (clusterStateData != null) {
      val data = new String(clusterStateData)
      if (data.length > 2) processZkClusterState(data,app) else processZkClusterState( """{}""",app)
    }

    val clusterStateCache:TreeCache = new TreeCache(clientLogvault,ZK_CLUSTERSTATE_FILE)
    clusterStateCache.getListenable.addListener(new TreeCacheListener {
      override def childEvent(client: CuratorFramework, event: TreeCacheEvent): Unit = {

        try {
          if(event.getData != null) {
            val path = event.getData.getPath
            if (path.matches(".*state.json")) {
              val data = new String(event.getData.getData)
              processZkClusterState(data,app)
            }
          }
        } catch {
          case ex: Exception =>
            ex.printStackTrace()
            Logger.debug(s"[SolrLog] Exception while watching change for $app ZK node $ZK_CLUSTERSTATE_FILE. Reason: " + ex.getLocalizedMessage)
        }
      }
    })
    clusterStateCache.start()

  }


  private def processZkClusterState(clusterStateJson: String, app:String):Unit = {
    //println("[processZkClusterState] \n" + clusterStateJson)
    //Logger.debug(s"[SolrLog] Processing clusterstate.json")

    //var newState = scala.collection.immutable.HashMap[String, CollectionStat]()

    try {
      val cs = jparse(clusterStateJson)

      val collections = cs.extract[Map[String, JObject]].keys.toList

      for (collection <- collections) {
        //if(explorerCollections.contains(collection)) {
        //val numShards = explorerCollections(collection).numShards
        //val replicationFactor = explorerCollections(collection).replicationFactor
        val shardsJson = (cs \\ collection \\ "shards")
        val shards = (shardsJson.extract[Map[String, JObject]]).keys.toList
        var cores = scala.collection.immutable.HashMap[String, List[String]]()
        for (shard <- shards) {
          val replicas = ((shardsJson \\ shard \\ "replicas").extract[Map[String, JObject]]).keys.toList
          for (replica: String <- replicas) {
            val coreNode = (shardsJson \\ shard \\ "replicas" \\ replica).extract[Map[String, String]]
            val coreName = coreNode("core")
            val baseUrl = coreNode("base_url")
            val state = coreNode("state")
            cores += (coreName -> List(baseUrl, state))
          }
        }

        if (cores.size > 0) {
          val stats = CollectionStat(1, 1, LOADED, cores)
          if(app == "EXPLORER")
            explorerCollections += (collection -> stats)
          else
            logvaultCollections += (collection -> stats)
        }

      }

      //explorerCollections = newState
    } catch {
      case ex: com.fasterxml.jackson.core.JsonParseException =>
        Logger.debug("[SolrLog] Error in parsing " + ZK_CLUSTERSTATE_FILE)
    }

    //Logger.debug("SolrCollection = " + explorerCollections)

    //SolrCollections.performHealthCheck(explorerCollections)

  }

  def getExplorerCollections = explorerCollections
  def getLogvaultCollections = logvaultCollections

}

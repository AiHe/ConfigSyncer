# ConfigSyncer
Configuration Syncer of Distribution System like Hadoop or Spark using Zookeeper.

It is implemented in Scala and primarily uses the ZooKeeper wrapper developed by Twitter(twitter/util/[util-zk-common, util-zk]). 

It also supports directory monitoring function. When files in a directory are changed, including renaming, removing, modification and creation, an immediate action will be triggered to sync the newest version of files.

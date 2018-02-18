package com.org.strom;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;

 
class TweetSentimentAnalyzerTopology {

	static final String TOPOLOGY_NAME = "storm-customer-sentiment-analyzer";

	public static void main(String[] args) {
		Config config = new Config();
		config.setMessageTimeoutSecs(120);
		config.setFallBackOnJavaSerialization(false);
		config.setSkipMissingKryoRegistrations(true);
		 
		TopologyBuilder b = new TopologyBuilder();
		b.setSpout("TweetSpout", new TweetSpout());
        b.setBolt("TweetFilterBolt", new TweetFilterBolt()).shuffleGrouping("TweetSpout");
        b.setBolt("IgnoreWordsBolt", new IgnoreWordsBolt()).shuffleGrouping("TweetFilterBolt");
        b.setBolt("TweetSentimentAnalyzerBolt", new TweetSentimentAnalyzerBolt(10, 5 * 60, 50)).shuffleGrouping("IgnoreWordsBolt");

		final LocalCluster cluster = new LocalCluster();
		cluster.submitTopology(TOPOLOGY_NAME, config, b.createTopology());

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				cluster.killTopology(TOPOLOGY_NAME);
				cluster.shutdown();
			}
		});

	}

}

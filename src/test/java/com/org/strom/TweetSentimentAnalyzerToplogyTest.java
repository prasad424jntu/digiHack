package com.org.strom;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;

public class TweetSentimentAnalyzerToplogyTest {

	
	/*@Test
	  public void testMainFlow(){
	    TopologyBuilder builder = new TopologyBuilder();
	    builder.setSpout("testSpout", new TweetSentimentAnalyzerTopology());
	    builder.setBolt(........).shuffleGrouping("testSpout");
	    builder.setBolt(........).shuffleGrouping("testSpout");
	   

	    LocalCluster cluster = new LocalCluster();
	    Config config = new Config();

	    cluster.submitTopology("storm-customer-sentiment-analyzer", config, builder.createTopology());
	    Thread.sleep(4000); //Here is the sleep.
	    cluster.killTopology("storm-customer-sentiment-analyzer"); // After sleep ends, shut the cluster down.  

	    // On that part, I was reading the file and check the content of it whether my bolt is done very well or not. 
	  }*/

}

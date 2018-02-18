package com.org.strom;

import junit.framework.TestCase;

public class TweetSpoutTest extends TestCase {

	
	/*@Test
	  public void testMainFlow(){
	    TopologyBuilder builder = new TopologyBuilder();
	    builder.setSpout("testSpout", new TestSout());
	    builder.setBolt(........).shuffleGrouping("testSpout");
	    builder.setBolt(........).shuffleGrouping("testSpout");
	    .....;

	    LocalCluster cluster = new LocalCluster();
	    Config config = new Config();

	    cluster.submitTopology(TOPOLOGY_NAME, config, builder.createTopology());
	    Thread.sleep(4000); //Here is the sleep.
	    cluster.killTopology(TOPOLOGY_NAME); // After sleep ends, shut the cluster down.  

	    // On that part, I was reading the file and check the content of it whether my bolt is done very well or not. 
	  }*/

}

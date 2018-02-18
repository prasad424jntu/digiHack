package com.org.strom;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import twitter4j.Status;

 
public class TweetFilterBolt extends BaseRichBolt {
	
	private static final long serialVersionUID = 5151173513759399636L;
    private OutputCollector collector;
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
        this.collector = collector;
    }
   
    @Override
    public void execute(Tuple input) {
        Status tweet = (Status) input.getValueByField("tweet");
        String tweetText = "";
        if(tweet.isFavorited()){
        	 
        }
        	     
        if(tweet.isRetweet()){
        	tweetText = tweet.getRetweetedStatus().getText();
        }else{
        	tweetText = tweet.getText();
		 
        }
        List<String> CAR_DICTIONARY = Arrays.asList("station wagon","automobile","auto","limousine","motor","machine","wagon","van","roadster","sedan","coupe","buggy",
"motorcar","hatchback","subcompact","jalopy","buggy","motorcar");
    	 
    	for(String car:CAR_DICTIONARY){
    		 Pattern pattern = Pattern.compile(car);
    		 Matcher matcher = pattern.matcher(tweetText);
    		if(matcher.find()){
    			 collector.emit(new Values(tweet.getUser().getScreenName(), tweetText,tweet.getGeoLocation(),tweet.getUser().isGeoEnabled() ) );
    			 break;
    		}
       	}
		 
     
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("lang", "word","geo","geo_enabled"));
    }
}

/**
 * Taken from the storm-starter project on GitHub
 * https://github.com/nathanmarz/storm-starter/ 
 */
package com.org.strom;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import twitter4j.FilterQuery;
import twitter4j.IDs;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Reads Twitter's sample feed using the twitter4j library.
 * 
 * @author davidk
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class TweetSpout extends BaseRichSpout {

	private SpoutOutputCollector collector;
	private LinkedBlockingQueue<Status> queue;
	private TwitterStream twitterStream;

	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		queue = new LinkedBlockingQueue<Status>(1000);
		this.collector = collector;

		StatusListener listener = new StatusListener() {
			@Override
			public void onStatus(Status status) {			 
				
				System.out.println(status.getUser().isGeoEnabled());
				 
				queue.offer(status);
			}
			@Override
			public void onDeletionNotice(StatusDeletionNotice sdn) {
			}
			@Override
			public void onTrackLimitationNotice(int i) {
			}
			@Override
			public void onScrubGeo(long l, long l1) {
			}
			@Override
			public void onStallWarning(StallWarning stallWarning) {
			}
			@Override
			public void onException(Exception e) {
			}
		};
		ConfigurationBuilder cb = new ConfigurationBuilder();

		cb.setDebugEnabled(true).setOAuthConsumerKey("XXXX").setOAuthConsumerSecret("XXXX")
		.setOAuthAccessToken("265857263-XXXXX").setOAuthAccessTokenSecret("XXXXXX");
		Configuration config = cb.build();
		TwitterStreamFactory factory = new TwitterStreamFactory(config);
		twitterStream = factory.getInstance();
		twitterStream.addListener(listener);
		FilterQuery filterQuery = new FilterQuery();
	 
		// 133280821
		long[] userId = { QQQQQQ };
		filterQuery.follow(userId);
		// filterQuery.follow(getCustomersHandler(config));	 
		twitterStream.filter(filterQuery);
	}

	@Override
	public void nextTuple() {
		Status ret = queue.poll();
		if (ret == null) {
			Utils.sleep(50);
		} else {
			collector.emit(new Values(ret));
		}
	}

	@Override
	public void close() {
		twitterStream.shutdown();
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		Config ret = new Config();
		ret.setMaxTaskParallelism(1);
		return ret;
	}

	@Override
	public void ack(Object id) {
	}

	@Override
	public void fail(Object id) {
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tweet"));
	}

	public long[] getCustomersHandler(Configuration config) {
		TwitterFactory tf = new TwitterFactory(config);

		Twitter twitter = tf.getInstance();

		IDs followerIDs;
		long[] ids = null;
		try {
			followerIDs = twitter.getFriendsIDs("YYYYYYY", -1);
			ids = followerIDs.getIDs();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ids;
	}

}

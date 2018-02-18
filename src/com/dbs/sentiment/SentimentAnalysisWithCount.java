//package tweetSentiAnalyzer;
package com.dbs.sentiment;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import twitter4j.IDs;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/twitter")
public class SentimentAnalysisWithCount {

	DoccatModel model;

	static int positive = 0;

	static int negative = 0;

	//public static void main(String[] args) throws IOException, TwitterException {
	@GET
	@Path("/tweet")
    @Produces(MediaType.TEXT_PLAIN)
	public String getTweets(String handle){
		String line = "";
        return "Line linner";
       
		/*SentimentAnalysisWithCount twitterCategorizer = new SentimentAnalysisWithCount();

		twitterCategorizer.trainModel();

		ConfigurationBuilder cb = new ConfigurationBuilder();

		cb.setDebugEnabled(true)

				.setOAuthConsumerKey("3jmA1BqasLHfItBXj3KnAIGFB")

				.setOAuthConsumerSecret("imyEeVTctFZuK62QHmL1I0AUAMudg5HKJDfkx0oR7oFbFinbvA")

				.setOAuthAccessToken("265857263-pF1DRxgIcxUbxEEFtLwLODPzD3aMl6d4zOKlMnme")

				.setOAuthAccessTokenSecret("uUFoOOGeNJfOYD3atlcmPtaxxniXxQzAU4ESJLopA1lbC");

		TwitterFactory tf = new TwitterFactory(cb.build());

		Twitter twitter = tf.getInstance();

		Query query = new Query("chiki4uonline");
		ResponseList<Status> timelineTweets =twitter. getFavorites("chiki4uonline");
		for (Status stat:timelineTweets){
			System.out.println("Timelines***"+stat.getText());
		}
		ResponseList<Status> favtweets =twitter.getFavorites();
		for (Status stat:favtweets){
			System.out.println("***"+stat.getText());
		}
		ResponseList<Status> tweets =twitter.getRetweetsOfMe();
		for (Status stat:tweets){
			System.out.println(stat.getInReplyToScreenName());
		}
		 QueryResult result = twitter.search(query);

		int result1 = 0;
		List<String> google = Arrays.asList("home", "loan", "car", "house", "digihack", "villa");
		List<Status> googleTweets = result.getTweets().stream().filter(t -> (google.stream().anyMatch(t.getText()::contains))).collect(Collectors.toList());
		for (Status status : googleTweets) {

			result1 = twitterCategorizer.classifyNewTweet(status.getText());

			if (result1 == 1) {

				positive++;

			} else {

				negative++;

			}

		}

		BufferedWriter bw = new BufferedWriter(new FileWriter("D:\\work\\digihack\\TwitterSentiAnalyzer\\src\\main\\java\\com\\dbs\\hackathon\\TwitterSentiAnalyzer\\results.csv"));

		bw.write("Positive Tweets," + positive);

		bw.newLine();

		bw.write("Negative Tweets," + negative);

		bw.close();*/
		//return "Hello";
	}

	private void trainModel() {

		InputStream dataIn = null;

		try {

			dataIn = new FileInputStream(
					"D:\\work\\digihack\\TwitterSentiAnalyzer\\src\\main\\java\\com\\dbs\\hackathon\\TwitterSentiAnalyzer\\input.txt");

			ObjectStream lineStream = new PlainTextByLineStream(dataIn, "UTF-8");

			ObjectStream sampleStream = new DocumentSampleStream(lineStream);

			// Specifies the minimum number of times a feature must be seen

			int cutoff = 2;

			int trainingIterations = 30;

			model = DocumentCategorizerME.train("en", sampleStream, cutoff,

					trainingIterations);

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			if (dataIn != null) {

				try {

					dataIn.close();

				} catch (IOException e) {

					e.printStackTrace();

				}

			}

		}

	}

	private int classifyNewTweet(String tweet) throws IOException {

		DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);

		double[] outcomes = myCategorizer.categorize(tweet);

		String category = myCategorizer.getBestCategory(outcomes);

		System.out.print("-----------------------------------------------------\nTWEET :" + tweet + " ===> ");

		if (category.equalsIgnoreCase("1")) {

			System.out.println(" POSITIVE ");

			return 1;

		} else {

			System.out.println(" NEGATIVE ");

			return 0;

		}

	}
	
	private static List<String> getFriendsList(Twitter twitter)
			throws TwitterException {
		List<String> followersScreen = new ArrayList<String>();
		IDs followerIDs = twitter.getFriendsIDs("chiki4uonline", -1);
		long[] ids = followerIDs.getIDs();
		for (long id : ids) {
			//System.out.println("SentimentAnalysisWithCount.getFriendsList():id:"+id);
		   twitter4j.User user = twitter.showUser(id);
		   followersScreen.add(user.getScreenName());
		   if(133280821 == id) {
			   System.out.println("Handle name :"+user.getScreenName());
		   }
		}
		return followersScreen;
	}
	private Map<String, List<Status>> getFriendsTweets(Twitter twitter,List<String> friendsScreen) throws TwitterException{
		List<Status> statusList;
		Map<String, List<Status>> filtersTweetsMap=new HashMap<>();
		for(String screeName:friendsScreen){
			statusList= new ArrayList<>();
			System.out.println("Following TweetsscreeName***"+screeName);
			//Start of Tweets
			List<String> google = Arrays.asList("digiHack", "Worst", "Workers");
		    
			Query query = new Query(screeName);
			 QueryResult result = twitter.search(query);
			 for (Status stat:result.getTweets()){
				 statusList.add(stat);
				System.out.println("Following Tweets***"+stat.getText());
			}
			 filtersTweetsMap.put(screeName, statusList);
			//End of Tweets
		}
		return filtersTweetsMap;
	}
	
	private void storeSentiment(String tweet,String screeName) throws TwitterException, IOException{
		SentimentAnalysisWithCount twitterCategorizer = new SentimentAnalysisWithCount();
		int result1=0;
		List<String> google = Arrays.asList("home", "loan", "car", "house", "digihack", "villa");
		if(google.contains(tweet)) {
		result1 = twitterCategorizer.classifyNewTweet(tweet);

		if (result1 == 1) {

			
		BufferedWriter bw = new BufferedWriter(new FileWriter("D:\\work\\digihack\\TwitterSentiAnalyzer\\src\\main\\java\\com\\dbs\\hackathon\\TwitterSentiAnalyzer\\results.csv"));

		bw.write("Positive Tweets,"+screeName+"," + positive);
		/*positive++;

		} else {

			negative++;
*/
		}
		}
		/*bw.newLine();

		bw.write("Negative Tweets," + negative);

		bw.close();*/
	}
}

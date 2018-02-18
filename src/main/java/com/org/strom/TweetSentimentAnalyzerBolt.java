package com.org.strom;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import twitter4j.GeoLocation;
import twitter4j.JSONArray;

 
public class TweetSentimentAnalyzerBolt extends BaseRichBolt {

	private static final long serialVersionUID = 2706047697068872387L;
	
	private static final Logger logger = LoggerFactory.getLogger(TweetSentimentAnalyzerBolt.class);
    
	/** Number of seconds before the top list will be logged to stdout. */
    private final long logIntervalSec;
    
    /** Number of seconds before the top list will be cleared. */
    private final long clearIntervalSec;
    
    /** Number of top words to store in stats. */
    private final int topListSize;

    private Map<String, Long> counter;
    private long lastLogTime;
    private long lastClearTime;
    DoccatModel model = null;
    public TweetSentimentAnalyzerBolt(long logIntervalSec, long clearIntervalSec, int topListSize) {
        this.logIntervalSec = logIntervalSec;
        this.clearIntervalSec = clearIntervalSec;
        this.topListSize = topListSize;
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
        counter = new HashMap<String, Long>();
        lastLogTime = System.currentTimeMillis();
        lastClearTime = System.currentTimeMillis();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
    }

    @Override
	public void execute(Tuple input) {
		String tweet = (String) input.getValueByField("word");
		String userScreenName = (String) input.getValueByField("lang");
		GeoLocation geoLocation = (GeoLocation)input.getValueByField("geo");
		Boolean geo_enabled = (Boolean) input.getValueByField("geo_enabled");
		
		logger.info("User Tweeted: " + tweet);

		int result1;
		try {
			result1 = classifyNewTweet(tweet);
			if (result1 == 1) {

				BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Users\\pc\\Desktop\\results.csv"));
				bw.write("User Id" + ", Elgible for Promotions " + ",Send GeoLocation Based Promotions\n");
				bw.write(userScreenName + ", positive for Promotions on UnSecured loans" +"," + geo_enabled);
				bw.flush();
				bw.close();
				if (geoLocation != null) {
					double latitude = geoLocation.getLatitude();
					double longitude = geoLocation.getLongitude();
					String userLocation = null;
					try {
						userLocation = getUserLocation(String.valueOf(latitude), String.valueOf(longitude));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

    public static String getUserLocation(String lat, String lon) throws IOException {

		String userLocation = null;

		String readUserFeed = readUserLocationFeed(lat.trim() + "," + lon.trim());
		try {
			JSONObject strJson = (JSONObject) JSONValue.parse(readUserFeed);
			JSONArray jsonArray = (JSONArray) strJson.get("results");
			JSONObject jsonAddressComp = (JSONObject) jsonArray.get(1);
			userLocation = jsonAddressComp.get("formatted_address").toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userLocation;

	}

	public static String readUserLocationFeed(String address) throws IOException {

		StringBuilder builder = new StringBuilder();

		HttpClient client = new DefaultHttpClient();

		HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng=" + address + "&sensor=false");

		try {

			HttpResponse response = client.execute(httpGet);

			StatusLine statusLine = response.getStatusLine();

			int statusCode = statusLine.getStatusCode();

			if (statusCode == 200) {

				HttpEntity entity = response.getEntity();

				InputStream content = entity.getContent();

				BufferedReader reader = new BufferedReader(new InputStreamReader(

						content));

				String line;

				while ((line = reader.readLine()) != null) {

					builder.append(line);

				}

			} else {

				System.err.println("Failed to download file");

			}

		} catch (ClientProtocolException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

		return builder.toString();

	}

    @SuppressWarnings("finally")
	public void trainModel() {
    	
		InputStream dataIn = null;
		  OutputStream onlpModelOutput = null;
		try {

			dataIn = new FileInputStream(
					"D:\\Srini_Scratchpad\\DBS_DIGIHackathon\\TwitterSentiAnalyzer\\src\\main\\java\\com\\dbs\\hackathon\\TwitterSentiAnalyzer\\input.txt");

			ObjectStream lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
			ObjectStream sampleStream = new DocumentSampleStream(lineStream);	 

			int cutoff = 2;

			int trainingIterations = 30;

			  model = DocumentCategorizerME.train("en", sampleStream, cutoff,trainingIterations);
			  
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
    
    public int classifyNewTweet(String tweet) throws IOException {

        trainModel();
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

}

package barry.twittersource;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import barry.twittersource.TwitterStreamListener.TwitterSourceChannel;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

@Component
@EnableBinding(TwitterSourceChannel.class)
public class TwitterStreamListener {
	
	@Value( "${twitter.consumerkey}" )
	private String consumerKey;

	@Value( "${twitter.consumersecret}" )
	private String consumerSecret;

	@Value( "${twitter.accesstoken}" )
	private String accessToken;

	@Value( "${twitter.tokensecret}" )
	private String tokenSecret;

	@Value( "${twitter.search.term:vmware}" )
	private String searchTerm;
	
	public interface TwitterSourceChannel {
		 
	    @Output("TwitterSourceChannel")
	    MessageChannel twitterSource();
	 
	}
	
	private Twitter twitter = null;

	@Autowired
    TwitterSourceChannel source;
	
	@Autowired
    public TwitterStreamListener(TwitterSourceChannel source) {
        this.source = source;
    }
	
	@EventListener
    public void onApplicationEvent( ContextRefreshedEvent event) {
		
		System.out.println("started!");
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey(consumerKey)
		  .setOAuthConsumerSecret(consumerSecret)
		  .setOAuthAccessToken(accessToken)
		  .setOAuthAccessTokenSecret(tokenSecret);
		
		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		
		StatusListener listener = new StatusListener() {
	          @Override
	          public void onStatus(Status status) {
	        	  JsonObject tweet = new JsonObject();
	        	  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	        	  sdf.setTimeZone(TimeZone.getTimeZone("EST"));
	        	  String dateText = sdf.format(status.getCreatedAt());
	        	  tweet.addProperty("date", dateText );
	        	  tweet.addProperty("user", status.getUser().getScreenName());
	        	  tweet.addProperty("text", status.getText());
	        	  
	              System.out.println(tweet.toString());
	              source.twitterSource()
	              	.send(
              			MessageBuilder.withPayload(
              					tweet.toString())
              						.build());
	          }
	
	          @Override
	          public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
	              System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
	          }
	
	          @Override
	          public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
	              System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
	          }
	
	          @Override
	          public void onScrubGeo(long userId, long upToStatusId) {
	              System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
	          }
	
	          @Override
	          public void onStallWarning(StallWarning warning) {
	              System.out.println("Got stall warning:" + warning);
	          }
	
	          @Override
	          public void onException(Exception ex) {
	              ex.printStackTrace();
	          }
	      };
	      twitterStream.addListener(listener);
	      FilterQuery filtre = new FilterQuery();
	      System.out.println("Searching for ...."+searchTerm);
	      String[] keywordsArray = { searchTerm };
	      filtre.track(keywordsArray);
	      twitterStream.filter(filtre);
    }

}

package barry.sentimentprocessor;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Transformer;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
@EnableBinding(Processor.class)
public class SentimentProcessorApplication {

	@Value( "${sentiment.url}" )
	private String sentimentUrl;

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
	
	@Autowired
	private RestTemplate template;
	
    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    public Object transform(String jsonTweet) {

    	System.out.println("json in:"+jsonTweet);
    	JSONObject objectFromString = new JSONObject(jsonTweet);
    	
    	String sentiment = template.postForObject(sentimentUrl, objectFromString.get("text"), String.class );
    	JSONObject sentimentJson = new JSONObject(sentiment);
    	System.out.println("Sentiment:"+sentiment);
    	objectFromString.put("sentiment", sentimentJson);
    	
    	return objectFromString.toString();
    }

	public static void main(String[] args) {
		SpringApplication.run(SentimentProcessorApplication.class, args);
	}

}

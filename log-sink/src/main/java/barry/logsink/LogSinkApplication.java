package barry.logsink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.MessageChannel;


// sink
//spring.cloud.stream.bindings.input.destination=foodOrdersChannel
//spring.cloud.stream.bindings.input.group=foodOrdersIntakeGroup


@EnableBinding(Sink.class)
@SpringBootApplication
public class LogSinkApplication {
	
	
    @StreamListener(Sink.INPUT)
    public void loggerSink(String date) {

        System.out.println("Received: " + date);
    }


	public static void main(String[] args) {
		SpringApplication.run(LogSinkApplication.class, args);
	}

}

# scdf-sentiment-example
An example application utilizing Spring Stream for use in Spring Cloud Data Flow

## Components

**twitter-source** a spring stream *source* application that does a live search of twitter and sends matching tweets as a json document.

**sentiment-processor** a spring stream *processor* application that receives a json document and calls the sentiment analysis service with the json documents *text* attribute. It adds the resulting sentiment score to the document and returns it for processing by the sink (or another processor)

**log-sink** a simple spring stream *sink* application that prints the received json document with sentiment score to standard out.

**sentiment** a python web service that implements a sentiment analysis using the nltk library. 


## Running standalone

In separate console windows follow these steps ...

**1. Run rabbitmq broker using docker**
```
docker run -it -p 5672:5672 -p 15672:15672 --hostname my-rabbit --name some-rabbit rabbitmq:3-management
```
**2. Push the sentiment analysis service**
```
cd sentiment
cf push
```
Note the service endpoint eg. *https://sentiment-brash-wallaby.apps.stonington.stream/sentiment*

or if you want to run locally (requires python and pip are installed)
```
cd sentiment
pip install -r requirements.txt
gunicorn main:app
```

**3. Run the log-sink application**
```
cd log-sink
mvn clean package -DskipTests=true
mvn spring-boot:run
```

**4. Run the sentiment-processor application**
edit the src/main/resources/application.properties file and set the sentiment.url variable from 1. eg.
```
sentiment.url=https://sentiment-brash-wallaby.apps.stonington.stream/sentiment
```
build and run the server
```
cd sentiment-processor
mvn clean package -DskipTests=true
mvn spring-boot:run --server.port=8086
```

**5. Run the twitter-source application**

This application uses the twitter api and requires a twitter developer account. Go to https://developer.twitter.com/en/apps to apply for an account and create an application. Once created edit the application.properties file with your credentials.
```
twitter.consumerkey=
twitter.consumersecret=
twitter.accesstoken=
twitter.tokensecret=
```

Build and run the application
```
cd twitter-source
mvn clean package -DskipTests=true
mvn spring-boot:run --server.port=8087 --twitter.search.term=<some search term> [defaults to vmware]
```


## Running in Spring Cloud Data Flow

<todo>
  



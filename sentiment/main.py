from flask import request, Response
from flask import Flask
import nltk
import os
nltk.download('vader_lexicon')
from nltk.sentiment.vader import SentimentIntensityAnalyzer
sid = SentimentIntensityAnalyzer()
# sid.polarity_scores(sentence)
# metrics
# cf install-plugin -r CF-Community "log-cache"
# cf install-plugin -r CF-Community "metric-registrar"

# cf register-log-format predict json
# 

app = Flask(__name__)

# set the port dynamically with a default of 3000 for local development
cf_port = int(os.getenv('PORT', '3000'))

print("Starting application .................................................!\n")

print("port:",cf_port)

@app.route('/isAlive')
def index():
    return "true"

@app.route('/sentiment', methods=['POST'])
def predict():
    print ("request: ", str(request.data) )
    return str(sid.polarity_scores(str(request.data)))

# app.run(host='0.0.0.0', port=port)
app.debug=True

if __name__ == '__main__':
  app.run(port=cf_port,host='0.0.0.0')

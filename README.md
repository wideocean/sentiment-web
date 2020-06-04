# Sentiment Analysis
Sentiment Analysis using a lexicon-based approach for user reviews in the domain of lodging & travel.

This was initially created as part of my bachelor thesis with a main focus on AirBnb reviews, however I did some refactoring several years later.

In the following, there is a short summary of the features & things worth to be mentioned.

## Lexicon
Lexicons are available in German and English. For both languages, a custom lexicon was each created with `SentiStrength` as a base. Each lexicon consists of multiple word lists:
- sentiment words
- booster
- negation
- emoticons

### German 
The german custom lexicon was created incorporating the lexicons from `SentiStrength`, `Clematide & Kenner`, `GermanPolarityClues` as well as several word additions and changes to their sentiment manually. 

### English
The english custom lexicon was created incorporating the lexicons from `SentiStrength` & `Taboada et al.` as well as several word additions and changes to their sentiment manually.

## Sentiment Algorithm
In general, for each sentence all positive & negative values are summed up to a sentence score. The sentiment of a text is the average of all sentence scores.

### Sentiment words
Each sentiment word has a numeric sentiment value assigned, ranging from -5 (very negative) to +5 (very positive). In practice, a sentiment value is basically a tuple holding a positive and a negative value where the value of the opposite sentiment is either +1 or -1.

Example:
- Positive: The food was good(+2). -> Score [2,-1]
- Negative: The food was bad(-2). -> Score [1,-2]
- Neutral: Let's go tonight. -> Score [1,-1]

### Booster
A booster is a word which enhances the value of sentiment words by +1 or -1, depending on whether it is positive or negative.

- Only applies to the very next word. If the next word after the booster is not a sentiment word, it has no effect
- If there are multiple booster for one sentiment word, only the last booster applies. (really very good = very good)

### Negation
- Negation positive sentiment: Apply -4 to the sentiment value ( Example: good [2,-1] -> not good [1,-2] )
- Negation negative sentiment: Neutralize to [1,-1]

### Miscellaneous
- Smileys / Emoticons: The emoticon list contains textual emoticons (such as `:) , :D , :( etc.`) where each has a value of +1 or -1
- Exclamation Marks: If an exclamation mark is used at the end of a sentence, the last detected sentiment will be boosted by +1 or -1
- Maximum values: Each sentiment word can have a maximum value of +5 or -5, boosting it further will have no effect

## How to use it
This project is declared as a `Dynamic Web Project`.

### Example Deployment
(1) Import the project into Eclipse IDE and export it as a `WAR file`.

(2) Put the WAR file into the `webapps` folder of your Apache Tomcat Webserver and start the server.

(3) The webservice will be accessible at `localhost:8080/_NAME_OF_WAR_FILE`, in this case `localhost:8080/sentiment-web` (Port might vary).

(4) Calling `localhost:8080/sentiment-web` in your browser will redirect to a HTML page with a demo on how to use the webservice and with more information about the endpoints.

### Example Requests & Response
#### Simple Request:

`http://localhost:8080/sentiment-web/api/sentiment?text=The+food+was+very+nice`

Response:
```
[
  {
    "lineNumber": 1,
    "sentiment": "POS",
    "lang": "en"
  }
]
```

#### Request with more details:

`http://localhost:8080/sentiment-web/api/sentiment/keywords?text=The+food+was+very+nice`

Response:
```
[
  {
    "lineNumber": 1,
    "sentiment": "POS",
    "lang": "en",
    "keywords": [
      {
        "word": "very",
        "boosterScore": 1
      },
      {
        "word": "nice",
        "score": {
          "positive": 2,
          "negative": -1
        }
      }
    ]
  }
]
```

## Dependencies
- Jersey Framework (https://eclipse-ee4j.github.io/jersey/) - for developing RESTful Webservice
- Apache Tika (https://tika.apache.org/) - for language detection
- juniversalchardet (https://github.com/albfernandez/juniversalchardet) - for encoding detection

## References
`Clematide & Klenner` Clematide, S., Klenner, M. 2010, ‘Evaluation and extension of a polarity lexicon for German’, In Workshop on Computational Approaches to Subjectivity and Sentiment Analysis (WASSA); Held in conjunction to ECAI 2010 Portugal, Lisbon, Portugal, 17 August 2010 - 17 August 2010, 7-13. Available at: <http://dx.doi.org/10.5167/uzh-45506>

`GermanPolarityClues` Waltinger, U. 2010, ‘GermanPolarityClues: A Lexical Resource for German Sentiment Analysis’, In Proceedings of the Seventh conference on International Language Resources and Evaluation (LREC'10). Valletta, Malta: European Language Resources Association (ELRA). Available at: <http://www.ulliwaltinger.de/pdf/91_Paper.pdf>

`SentiStrength` Thelwall, M., Buckley, K., & Paltoglou, G. 2012, ‘Sentiment strength detection for the social Web’, Journal of the American Society for Information Science and Technology, 63(1), 163-173. Available at: <http://dx.doi.org/10.1002/asi.21662>

`Taboada et al.` Taboada, M., Brooke, J., Tofiloski, M., Voll, K. & Stede, M. 2011, ‘Lexicon-based methods for sentiment analysis’, Comput. Linguistics vol. 37, no. 2 (June 2011), 267-307. Available at: <http://dx.doi.org/10.1162/COLI_a_00049>

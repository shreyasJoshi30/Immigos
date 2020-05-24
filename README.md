# Immigos
The application will act as an emotional assistant which will engage in simple communications and allow the users to express their concerns. The application will recommend various activities based on user interests and track their emotions to show a progress and little victories to motivate the user.

#### Application Dependencies and API Keys

The application will require the user to sign up for following API Keys

1- Google maps API key (https://developers.google.com/maps/documentation/javascript/get-api-key)

2- Dialogflow API key (https://dialogflow.com/docs/reference/v2-auth-setup)

3- Eventfinda API key (https://www.eventfinda.com.au/api/v2/index)

4- NewsAPI API key (https://newsapi.org/)

5- Firebase The application will require google-services.JSON under the root(app) folder

Create a strings.xml file under res/values/  and add all the api keys as shown below.

```xml
    <string name="dialogflow_key">Your_key</string>
    <string name="newsAPI_key">Your_key</string>
    <string name="eventBriteAPI_key">Your_key</string>
    <string name="googleMaps_key">Your_key</string>
```

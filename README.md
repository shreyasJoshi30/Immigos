# Immigos
The application will act as an emotional assistant which will engage in simple communications and allow the users to express their concerns. The application will recommend various activities based on user interests and track their emotions to show a progress and little victories to motivate the user.

### Firebase setup

The application must have a google-services.json file which can be downloaded from your firebase project. The file can be found from the project settings page. Place the file under the root(app) folder of the project.

https://support.google.com/firebase/answer/7015592?hl=en


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
    <string name="placesAPI_key">Your key</string>
    <string name="eventfinda_username">your username</string>
    <string name="eventfinda_password">your password</string>
```

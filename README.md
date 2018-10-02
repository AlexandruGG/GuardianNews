# GuardianNews
**Use**: A news app that displays stories from The Guardian's Open Platform.<br/>
**Developing Purpose**: Learning about networking elements - the app makes a HTTP request to The Guardian's API and receives a JSON response which is then parsed and selected information is displayed in a ListView (story title, section, author, date of publication). This operation is handled by a Loader in a thread separate from the UI thread. The app also features a Preference Screen through which the user can specify the *topic* to search and whether to *order* news by relevance or date of publication (most recent first).<br/>
**Context**: Project created as part of the Udacity Android Basics Nanodegree (https://eu.udacity.com/course/android-basics-nanodegree-by-google--nd803)

package com.example.android.guardiannews;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving news data through the Guardian API.
 */
public final class NewsQueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = NewsQueryUtils.class.getSimpleName();

    private static String RESPONSE;
    private static String RESULTS;
    private static String SECTION;
    private static String DATE;
    private static String TITLE;
    private static String URL;
    private static String TAGS;
    private static String CONTRIBUTOR;

    public static void myContext(Context context) {

        //Below are keys to be used for JSON Parsing from The Guardian's API
        RESPONSE = context.getString(R.string.key_01_response);
        RESULTS = context.getString(R.string.key_02_results);
        SECTION = context.getString(R.string.key_03_section);
        DATE = context.getString(R.string.key_04_date);
        TITLE = context.getString(R.string.key_05_title);
        URL = context.getString(R.string.key_06_url);
        TAGS = context.getString(R.string.key_07_tags);
        CONTRIBUTOR = context.getString(R.string.key_08_contributor);
    }

    /**
     * Create a private constructor because no one should ever create a {@link NewsQueryUtils} object. This class is only meant to hold static
     * variables and methods, which can be accessed directly from the class name NewsQueryUtils
     */
    private NewsQueryUtils() {
    }

    /**
     * Query the Guardian API and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {

        // Create URL object.
        URL url = createUrl(requestUrl);

        // HTTP request with JSON response.
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response, create a list of News objects and return it
        return extractFeatureFromJson(jsonResponse);
    }

    /**
     * Return a list of {@link News} objects that has been built up from parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList to add news stories to.
        List<News> newsList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON is formatted, a JSONException exception object will be
        // thrown. Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string.
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            // Extract the first JSONObject associated with the key called "response".
            JSONObject newsJsonResponse = baseJsonResponse.getJSONObject(RESPONSE);

            // Extract the JSONArray associated with the key called "results".
            JSONArray newsArray = newsJsonResponse.getJSONArray(RESULTS);

            // For each news story in the newsArray, create a News object.
            for (int i = 0; i < newsArray.length(); i++) {

                // Get a single news story at position i within the array and start extracting data.
                JSONObject currentNews = newsArray.getJSONObject(i);

                // Extract the section the story belongs to.
                String newsSection = currentNews.optString(SECTION);

                // Extract the publication date.
                String newsDate = currentNews.optString(DATE);

                // Extract the title of the story.
                String newsTitle = currentNews.optString(TITLE);

                // Extract the web url.
                String newsUrl = currentNews.optString(URL);

                // Extract the JSONArray associated with the key "tags" - needed to get the author/contributor of the article.
                JSONArray newsTags = currentNews.getJSONArray(TAGS);

                String newsContributor = "";

                // Check if the JSON result has "tags" and extract the author name.
                if (newsTags.length() != 0) {
                    newsContributor = newsTags.getJSONObject(0).optString(CONTRIBUTOR);
                }

                // Create a new News object with the title, section, publication date, contributor, and url from the JSON response
                News newsStory = new News(newsTitle, newsSection, newsContributor, newsUrl, newsDate);

                // Add the new News story to the list of news.
                newsList.add(newsStory);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block, catch the exception here, so the app doesn't
            // crash. Print a log message accordingly.
            Log.e("NewsQueryUtils", "Error parsing the news JSON results", e);
        }

        // Return the list of news stories.
        return newsList;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;

        try {
            url = new URL(stringUrl);

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }

        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000); // 10 seconds
            urlConnection.setConnectTimeout(15000); // 15 seconds
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results from The Guardian API.", e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
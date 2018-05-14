package com.example.jonathan.newsapp1;

import android.content.Context;
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
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    //Tag for log messages
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    //Keys for the JSON response
    private static final String RESPONSE = "response";
    private static final String RESULTS = "results";
    private static final String SECTION = "sectionName";
    private static final String DATE = "webPublicationDate";
    private static final String ARTICLE_NAME = "webTitle";
    private static final String URL = "webUrl";
    private static final String TAGS = "tags";
    private static final String AUTHOR = "webTitle";

    //Blank constructor
    private QueryUtils() {
    }

    //Query the URL and return a list of {@link News} objects
    public static List<News> fetchNewsItems(String requestUrl){

        //Create URL object
        URL url = createUrl(requestUrl);

        //Perform HTTP request to the URL and receive a JSON response and return response
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        //Extract relevant fields from the JSON response and create a list of News Articles
        List<News> articles = extractFeatureFromJson(jsonResponse);

        //Return the list of {@link News}
        return articles;
    }

     //Returns new URL object from the given string URL.
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL.", e);
        }
        return url;
    }


     //Make an HTTP request to the given URL and return a String as the response.
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            //Send a request to connect
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            //If the connection was not established, print it to the log
            Log.e(LOG_TAG, "Problem retrieving News Articles from JSON results.", e);
        } finally {

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

     //Convert the {@link InputStream} into a String which contains the
     //whole JSON response from the server.
     private static String readFromStream(InputStream inputStream) throws IOException {
        //Create a new StringBuilder
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

     //Return a list of {@link News} objects that have been built up from
     //parsing a JSON response.
     private static List<News> extractFeatureFromJson(String newsArticlesJSON) {
        //If the JSON string is empty or null, then return early
        if (TextUtils.isEmpty(newsArticlesJSON)){
            return null;
        }
        //Create an empty ArrayList that we can start adding news articles to
        List<News> articles = new ArrayList<>();

        //Try to parse the JSON response. If there's a problem with the way the JSON
        //is formatted, a JSONException exception object will be thrown.
        //Will catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            //Create the root JSONobject by calling the constructor and store in variable
            //with the name baseJsonResponse
            JSONObject baseJsonResponse = new JSONObject(newsArticlesJSON);

            //Get the JSONObject associated with the key "RESPONSE"
            JSONObject responseNewsJson = baseJsonResponse.getJSONObject(RESPONSE);

            //Get the instance of JSONArray that contains JSONObjects
            JSONArray articleArray = responseNewsJson.getJSONArray(RESULTS);

            //Iterate the articleArray and print the info of JSONObjects
            for (int i = 0; i < articleArray.length(); i++) {

                //Pull out the JSONObject@ the specified position 0 (i)
                JSONObject currentNewsArticle = articleArray.getJSONObject(i);

                //Extract the value for the key called "webTitle" (ARTICLE_NAME)
                String newsArticleString = currentNewsArticle.getString(ARTICLE_NAME);

                //Extract the value for the key called "sectionName" (SECTION)
                String newsSection = currentNewsArticle.getString(SECTION);

                //Extract the value for the key called "webPublicationDate" (DATE)
                String newsDate = currentNewsArticle.getString(DATE);

                //Extract the value for the key called "webUrl" (URL)
                String newsArticleUrl = currentNewsArticle.getString(URL);


                //Iterate authorArray to narrow down to author's name by calling the "TAGS" key
                JSONArray authorArray = currentNewsArticle.getJSONArray(TAGS);
                String newsAuthor = " ";
                if (currentNewsArticle.has(TAGS))
                for (int j = 0; j < authorArray.length(); j++) {
                    //Get the JSONObject associated with the key "TAGS"
                    JSONObject authorsName = authorArray.getJSONObject(0);
                    //Extract the value for the key called "webTitle" (AUTHOR) under "authorArray"
                    String nameOfAuthor = "by " + authorsName.getString(AUTHOR);
                    newsAuthor = nameOfAuthor;
                }

                //Create a new News object from the above 5 strings
                News article = new News(newsArticleString, newsSection, newsDate, newsArticleUrl, newsAuthor);
                //Add the new article to articleS
                articles.add(article);
            }


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the articles from JSON results.", e);
        }

        // Return the list of news articles
        return articles;
    }

}

package com.example.jonathan.newsapp1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;


public class ArticleAdapter extends ArrayAdapter<News> {

    //The part of the webPublicationDate string from the Guardian API that we use to
    //determine whether or not we use an easyReadDate or the original fullOnDateAndTime
    private static final String DATE_SEPARATOR = "T";

    //Construct a new {@link ArticleAdapter}
    //@param context of the app
    //@param articles if the list of news articles, which is the data source of the adapter
    public ArticleAdapter(Context context, ArrayList<News> articles) {
        super(context, 0, articles);
    }
    //Returns a list item view that displays information about a given news article at the position
    //in the list of articles
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Check if the existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list, parent, false);
        }
        //Find the article at the given position in the list of articles
        News currentNewsArticle = getItem(position);

        //Find the TextView with the view ID articleHeadline
        TextView articleHeadlineTextView = convertView.findViewById(R.id.article_headline);
        articleHeadlineTextView.setText(currentNewsArticle.getmHeadline());

        //Find the TextView with the view ID newsSection
        TextView newsSectionTextView = convertView.findViewById(R.id.news_section);
        newsSectionTextView.setText(currentNewsArticle.getmNewsSection());

        //This method takes the originalDate parsed and converts it into a more readable format
        String originalDate = currentNewsArticle.getmArticlePublishedDate();
            String easyReadDate;
            String fullOnDateAndTime;
            if(originalDate.contains(DATE_SEPARATOR)) {
                String[] parts = originalDate.split(DATE_SEPARATOR);
                easyReadDate = parts[0];
                fullOnDateAndTime = parts[1] + DATE_SEPARATOR;
            } else {
                //If there is no "T" text in the original webPublicationDate string
                //we will use the originalDate/fullOnDateAndTime
                easyReadDate = getContext().getString(R.string.at_time);
                fullOnDateAndTime = originalDate;
            }

        //Find the TextView with the view ID newsSection
        TextView articleAuthorTextView = convertView.findViewById(R.id.article_author);
        articleAuthorTextView.setText(currentNewsArticle.getmAuthor());


        //Find the TextView with the view ID publishedDate. This will be used if
        //there is no "T" value in the original string for webPublicationDate
        TextView publishedDateTextView = convertView.findViewById(R.id.published_date);
        publishedDateTextView.setText(fullOnDateAndTime);

        //Find the TextView with the view ID publishedDate. This version is used when a "T"
        //is found in the original string. Formatted as easy to read.
        TextView easyReadDateToTextView = convertView.findViewById(R.id.published_date);
        easyReadDateToTextView.setText(easyReadDate);

        //Return the listItemView with data from above
        return convertView;
    }
}

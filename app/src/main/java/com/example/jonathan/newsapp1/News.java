package com.example.jonathan.newsapp1;

public class News {


    //News Headline
    private String mHeadline;

    //News Section
    private String mNewsSection;

    //Article Published on Date
    private String mArticlePublishedDate;

    //Article URL
    private String mArticleUrl;

    //Article author
    private String mAuthor;


    //Create a new News object
    //@param mHeadline is the title/headline of the article
    //@param mNewsSection is the section the article is found under
    //@param mArticlePublishedDate is the date the article was published
    //@param mArticleURL is the URL for that specific article
    //@param mAuthor is the name of the author for specific article
    public News(String headline, String section, String publishedOn,
                String articleUrl, String author) {
        mHeadline = headline;
        mNewsSection = section;
        mArticlePublishedDate = publishedOn;
        mArticleUrl = articleUrl;
        mAuthor = author;
    }

    //Get the headline/title
    public String getmHeadline() {
        return mHeadline;
    }
    //Get the section of the article
    public String getmNewsSection() {
        return mNewsSection;
    }
    //Get the date the article was published on
    public String getmArticlePublishedDate() {
        return mArticlePublishedDate;
    }
    //Get the URL for the given article
    public String getmArticleUrl() {
        return mArticleUrl;
    }
    //Get the name of the author for article
    public String getmAuthor(){
        return mAuthor;
    }
}
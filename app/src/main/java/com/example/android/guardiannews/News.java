package com.example.android.guardiannews;

/**
 * An {@link News} object contains information related to a single news story.
 */
public class News {

    private String newsTitle;
    private String newsSection;
    private String newsContributor;
    private String newsUrl;
    private String newsDate;

    /**
     * Constructs a new {@link News} object.
     *
     * @param newsTitle       is the title of the news story
     * @param newsSection     is the category the story belongs to
     * @param newsContributor is the contributor/author of the story
     * @param newsUrl         is the web url to view the full story
     * @param newsDate        is the news publishing date
     */
    public News(String newsTitle, String newsSection, String newsContributor, String newsUrl, String newsDate) {
        this.newsTitle = newsTitle;
        this.newsSection = newsSection;
        this.newsContributor = newsContributor;
        this.newsUrl = newsUrl;
        this.newsDate = newsDate;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getNewsSection() {
        return newsSection;
    }

    public String getNewsContributor() {
        return newsContributor;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public String getNewsDate() {
        return newsDate;
    }

}
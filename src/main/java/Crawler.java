

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Crawler {

    private static final int timeoutMillis = 10000; //timeout when fetching html


    //the css rel attribute value used to identify the link to the next document
    private String nextRel = "next";

    private URL startURL;
    //url substring used to identify which image is the comic, usually "/comics/"
    private String comicSubstring;

    private ImageProcessor imageProcessor;




    public Crawler(URL startURL, String comicSubstring) {
        this.startURL = startURL;
        this.comicSubstring = comicSubstring;
        this.imageProcessor = new ImageProcessor();
    }

    public void startReading() throws IOException {

        URL nextURL = startURL;

        while (nextURL!=null) {
            nextURL = readPage(nextURL);
        }

    }

    /**
     *
     * @param url the URL of the page
     * @return the URL of the next page, or null if there is no next page
     * @throws IOException
     */
    private URL readPage(URL url) throws IOException {
        System.out.println("Reading from " + url.toString());

        //get html document
        Document doc = Jsoup.parse(url, timeoutMillis);


        //find image
        Elements media = doc.select("[src]");
        String imageSrc = null;

        for (Element element : media) {
            if (element.attr("abs:src").contains(comicSubstring)) {
                imageSrc = element.attr("abs:src");
                break;
            }
        }

        if (imageSrc==null) {
            System.out.println("No valid comic found");
        } else {
            System.out.println("Comic src: " + imageSrc);
            imageProcessor.saveImage(new URL(imageSrc));
        }


        //find link to next page
        Elements links = doc.select("a[href]");
        String nextURLString = "";

        for (Element link : links) {
            if(link.attr("rel").equals(nextRel)) {
                nextURLString = link.attr("abs:href");
                break;
            }
        }

        if (nextURLString.equals("")) {
            System.out.println("No 'next' link found. Exiting crawler loop.");
            return null;
        }

        URL nextURL = new URL(nextURLString);

        if (nextURL.equals(url)) {
            System.out.println("Next link leads back to the same page. Exiting crawler loop.");
            return null;
        }

        return nextURL;
    }
}

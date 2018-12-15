import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting crawler");

            //URL url = new URL("http://egscomics.com/comic/2002-01-21");
            URL url = new URL("https://xkcd.com/1325/");


            String comicSubstring = "/comics/";


            Crawler crawler = new Crawler(url, comicSubstring);
            crawler.startReading();




    }
}

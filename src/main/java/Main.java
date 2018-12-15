import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws MalformedURLException {
        System.out.println("Starting crawler");

            //URL url = new URL("http://egscomics.com/comic/2002-01-21");
            URL url = new URL("https://xkcd.com/2000/");
            //URL url = new URL("http://www.goodbyetohalos.com/comic/prologue-1");
            //URL url = new URL("https://questionablecontent.net/view.php?comic=2245");

            String comicSubstring = "/comics/";

            Crawler crawler = new Crawler(url, comicSubstring);
            crawler.startReading();

    }
}

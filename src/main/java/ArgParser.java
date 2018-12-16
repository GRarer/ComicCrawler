import com.beust.jcommander.Parameter;

import java.net.MalformedURLException;
import java.net.URL;

public class ArgParser {

    @Parameter(description = "Start URL")
    private String inputStartURL = null;

    @Parameter(names = {"-s", "-substring"}, description = "URL substring used to identify comic images")
    private String comicSubstring = "/comics/";

    @Parameter(names = {"-d", "-directory", "-folder"}, description = "URL substring used to identify comic images")
    private String directory = System.getProperty("user.home") + "/Desktop/Comic_Output/";

    public void run() {


        //validate url input
        if(inputStartURL==null) {
            System.out.println("You must provide a first-page URL.");
            System.exit(0);
        }
        String http = "http://";
        String https = "https://";
        if ((inputStartURL.length() < 6)
                ||((!inputStartURL.substring(0,7).equals(http)
                &&(!inputStartURL.substring(0,8).equals(https))))) {
            inputStartURL = http + inputStartURL;
        }

        //add slash to directory
        String slash = "\\";

        if (!directory.substring(directory.length()-1, directory.length()).equals(slash) && !directory.substring(directory.length()-1, directory.length()).equals("/")) {
            directory = directory + slash;
        }


        System.out.println();
        System.out.println("Starting from url: " + inputStartURL);
        System.out.println("Using comic url substring: " + comicSubstring);
        System.out.println("Outputting to: " + directory);
        System.out.println();


        try {
            URL startURL = new URL(inputStartURL);
            Crawler crawler = new Crawler(startURL, comicSubstring, directory);
            crawler.startReading();
        } catch (MalformedURLException ex) {
            System.out.println("Malformed start url: " + inputStartURL);
            System.exit(0);
        }

    }
}
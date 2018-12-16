import com.beust.jcommander.JCommander;

public class Crawler {
    public static void main(String[] args) {

        if(args.length==0 || args[0].equals("help")) {
            printHelpText();
            System.exit(0);
        }

        ArgParser parser = new ArgParser();
        JCommander.newBuilder()
                .addObject(parser)
                .build()
                .parse(args);

        parser.run();
    }

    static void printHelpText() {
        //TODO help text
        String[] help = {
                "This is a utility for making offline archives of webcomics.",
                "Usage: Crawler <first_page_url> [-d <directory_name>] [-p <file_prefix>] [-s <comic_url_substring>]",
                "",
                "-d or -directory or -folder : the directory where images will be saved.",
                "The default is <user_name>/Desktop/comics_output",
                "",
                "-p or -prefix : optional prefix for output file names",
                "",
                "-s or -substring : the URL substring used to identify comic images.",
                "The crawler looks for this substring to determine which element in the page is the comic image.",
                "The default is '/comics/', which is used on most webcomic sites.",
                "",
                "The offline archives created by this software are intended for personal and archival use only.",
                "Please do not use this software to violate copyright laws."
        };

        for (String s : help) {
            System.out.println(s);
        }
    }
}

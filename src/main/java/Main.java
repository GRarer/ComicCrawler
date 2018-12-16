import com.beust.jcommander.JCommander;

public class Main {
    public static void main(String[] args) {

        if(args.length==0 || args[0].equals("help")) {
            //TODO print help information
            System.out.println("help info goes here");
            System.exit(0);
        }

        ArgParser parser = new ArgParser();
        JCommander.newBuilder()
                .addObject(parser)
                .build()
                .parse(args);

        parser.run();
    }
}

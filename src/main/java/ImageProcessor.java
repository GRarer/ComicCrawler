import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class ImageProcessor {

    private int counter = 0;
    private String destinationFolder;

    public ImageProcessor(String directory) {
        this.destinationFolder = directory;
        this.counter = 0;

        //TODO create directory if it doesnt already exist

        File folder = new File(destinationFolder);
        folder.mkdirs();

    }

    public ImageProcessor() {
        this(System.getProperty("user.home") + "/Desktop/Comic_Output/");
    }



    public  void saveImage(URL imgURL) throws IOException{

        counter++;

        //images are converted to PNG unless they are GIFs (since GIF is sometimes used for animated panels)
        if (isGif(imgURL)) {
            //TODO gifs

            String path = destinationFolder + Integer.toString(counter) + ".gif";

            ReadableByteChannel readChannel = Channels.newChannel(imgURL.openStream());
            FileOutputStream outputStream = new FileOutputStream(path);
            FileChannel writeChannel = outputStream.getChannel();

            writeChannel.transferFrom(readChannel,0,Long.MAX_VALUE);

        } else {
            RenderedImage img = ImageIO.read(imgURL);
            String path = destinationFolder + Integer.toString(counter) + ".png";
            File outputFile = new File(path);
            outputFile.createNewFile(); //creates a file if it doesn't exist


            try {
                ImageIO.write(img, "png", outputFile);
            } catch (IllegalArgumentException ex) {
                System.out.println("Could not parse image data.");
                return;
            }

        }



    }


    private boolean isGif(URL url) {
        String urlString = url.toString();

        if (urlString.length()<4 || !urlString.contains(".")) {
            return false;
        }

        String extension = urlString.substring(urlString.lastIndexOf('.'));
        return extension.equals(".gif");
    }
}

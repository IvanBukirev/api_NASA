import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Utils.useClient(httpClient -> {
            try {
                final var url = Utils.getUrl(httpClient);
                final var fileName = url.substring(url.lastIndexOf('/') + 1);
                Utils.saveImage(httpClient, url, fileName);
            } catch (final IOException ex) {
                System.err.println(ex);
            }
        });
    }

}


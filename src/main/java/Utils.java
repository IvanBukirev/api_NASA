import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

public class Utils {

    public static void useClient(Consumer<CloseableHttpClient> block) {
        try (var httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build()
        ) {
            block.accept(httpClient);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getUrl(CloseableHttpClient httpClient) throws IOException {
        HttpGet request = new HttpGet(
                "https://api.nasa.gov/planetary/apod?api_key=kvJS1OrhtdiOzuM0LbwRuQtBRXlzTNgZd3HCIHZ9");
        CloseableHttpResponse response = httpClient.execute(request);
        final ObjectMapper mapper = new ObjectMapper();
        NasaObject nasaObject = mapper.readValue(response.getEntity().getContent(), NasaObject.class);
        return nasaObject.getUrl();
    }

    public static void saveImage(
            CloseableHttpClient httpClient,
            String url,
            String targetFile
    ) throws IOException {
        final var request = new HttpGet(url);
        final var response = httpClient.execute(request);
        final var bytes = response.getEntity().getContent().readAllBytes();
        try (OutputStream outStream = new FileOutputStream(targetFile)) {
            outStream.write(bytes);
        }
    }
}
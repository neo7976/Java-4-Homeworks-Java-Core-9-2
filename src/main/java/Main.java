import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class Main {
    public static final String REMOTE_SERVICE_URL = "https://api.nasa.gov/planetary/apod?api_key=OdiKbbxuZGLe7n97EU4GtWY90uNCaKL3EdUnxMiy";
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setUserAgent("My test service")
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet(REMOTE_SERVICE_URL);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        try {
            CloseableHttpResponse response = httpClient.execute(request);
//            String body = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
//            System.out.println(body);
            mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY); //Когда требуется создать массив = 1
            List<Nasa> nasaList = mapper.readValue(response.getEntity().getContent(),
                    new TypeReference<>() {
                    });

            String url = null;
            for (Nasa nasa : nasaList) {
                if (nasa.getMedia_type().equals("image")) {
                    url = nasa.getHdurl();
                    System.out.println("Hdurl: " + url);
                } else
                    System.out.println("По ссылке нет изображения!");
            }
//            HttpUriRequest request2 = new HttpGet(url);
//            CloseableHttpResponse response2 = httpClient.execute(request2);

            String[] fileName = url.split("/");
            URL connection = new URL(url);
            BufferedImage img = ImageIO.read(connection);
            File bw = new File("src/main/resources/" + fileName[fileName.length - 1]);
            ImageIO.write(img, "jpg", bw);

            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

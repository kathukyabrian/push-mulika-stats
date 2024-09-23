package tech.kitucode;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * @author kamochu
 */
public class HTTPClient {

    public static HTTPResponse send(String url, String request, String method, String contentType, Map<String, String> headers, int connectTimeout, int readTimeout) throws IOException {

        URL obj = new URL(url);

        StringBuilder response;

        //read the response
        int responseCode;

//        https = (url != null && url.contains("https"));
        if (url.contains("https")) {
            // https

            HttpsURLConnection connection;

            connection = (HttpsURLConnection) obj.openConnection();

            //add reuqest header
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", contentType);
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);

            if (headers != null) {
                headers.forEach(connection::setRequestProperty);
            }

            // Send post request
            connection.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(request);
                wr.flush();
            }

            responseCode = connection.getResponseCode();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
        } else {
            // https

            HttpURLConnection connection;

            connection = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", contentType);
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);

            if (headers != null) {
                headers.forEach(connection::setRequestProperty);
            }

            // Send post request
            connection.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(request);
                wr.flush();
            }

            responseCode = connection.getResponseCode();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
        }

        return new HTTPResponse(responseCode, response.toString());

    }

    public static HTTPResponse sendGet(String url) throws Exception {
        HttpURLConnection httpClient =
            (HttpURLConnection) new URL(url).openConnection();

        // optional default is GET
        httpClient.setRequestMethod("GET");

        //add request header
        httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = httpClient.getResponseCode();

        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(
            new InputStreamReader(httpClient.getInputStream()))) {
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }
        }

        return new HTTPResponse(responseCode, response.toString());
    }
}

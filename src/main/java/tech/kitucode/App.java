package tech.kitucode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tech.kitucode.constants.ServiceConstants;
import tech.kitucode.domain.HTTPResponse;
import tech.kitucode.util.HTTPClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Hello world!
 */
public class App {
    private static Thread mulikaThread;
    private static String app = "delay";
    private static String module = "delay-1";
    private static int reportInterval = 60000;
    private static int SERVICE_COUNT = 1;
    private static String SERVICE_PREFIX = "delay";
    private static String mulikaUrl = "https://mulika.natujenge.ke/api/statistics/report-list";
    private static String mulikaAPIKey = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwbXV0aXN5YUBtZWxpb3JhLnRlY2giLCJhdXRoIjoiUk9MRV9BUEkiLCJvaWQiOjIsIm90eXBlIjoiQ0xJRU5UIiwiZXhwIjoxOTAxNzgwNDQzfQ.2LdI9Rpu-tu4sN9h3KIGoq61ILdgvj7xQ9Dh_2L3Fei0VutF_JF7UIob5z_OKHV8XWFfW6P1aa0DB2gZH3_iow";

    public static void main(String[] args) {
        loadConfig();

        System.out.println("About to start stats push thread");
        mulikaThread = new Thread(() -> {
            while (true) {
                try {
                    try {
                        System.out.println("About to sleep for " + reportInterval + " milliseconds");
                        Thread.sleep(reportInterval);
                    } catch (InterruptedException ex) {
                        System.out.println("Thread could not sleep. trying again " + ex);
                        Thread.sleep(reportInterval);
                    }

                    reportStats();

                } catch (InterruptedException e) {
                    System.out.println("received an interrupt signal " + e);
                    break;
                } catch (Exception ex) {
                    System.out.println("Encountered exception. Proceeding " + ex);
                }
            }
        }, "mulika-thread");

        System.out.println("Successfully initialized mulika thread");

        mulikaThread.start();


        System.out.println("Successfully started mulika thread. mulikaUrl = " + mulikaUrl + ", mulikaAPIKey = " + mulikaAPIKey);
    }

    private static void loadConfig() {
        // load from environment variable

        Properties properties = new Properties();
        String fileName = System.getenv().get("MULIKA_REPORTING_CONFIG");
        if (fileName != null) {
            System.out.println("Environment variable is set to " + fileName + "|about to load properties");
            try {
                properties.load(Files.newInputStream(Paths.get(fileName)));
            } catch (IOException ex) {
                System.out.println("encountered an error when loading properties " + ex);
            }
        } else {
            System.out.println("Environment variable is not set. Loading from " + ServiceConstants.DEFAULT_CONFIG_LOCATION + "/" + ServiceConstants.DEFAULT_CONFIG_FILE_NAME);
            try {
                properties.load(Files.newInputStream(Paths.get(ServiceConstants.DEFAULT_CONFIG_LOCATION + "/" + ServiceConstants.DEFAULT_CONFIG_FILE_NAME)));
            } catch (IOException ex) {
                System.out.println("encountered an error when loading properties from " + ServiceConstants.DEFAULT_CONFIG_LOCATION + "/" + ServiceConstants.DEFAULT_CONFIG_FILE_NAME + "|about to load from class path");
                try {
                    properties.load(App.class.getClassLoader().getResourceAsStream(ServiceConstants.DEFAULT_CONFIG_FILE_NAME));
                } catch (IOException e) {
                    System.exit(0);
                }
            }
        }

        app = properties.getProperty(ServiceConstants.APP_CONFIG_KEY);
        module = properties.getProperty(ServiceConstants.MODULE_CONFIG_KEY);
        reportInterval = Integer.parseInt(properties.getProperty(ServiceConstants.REPORT_INTERVAL_CONFIG_KEY));
        SERVICE_COUNT = Integer.parseInt(properties.getProperty(ServiceConstants.SERVICE_COUNT_CONFIG_KEY));
        SERVICE_PREFIX = properties.getProperty(ServiceConstants.SERVICE_PREFIX_CONFIG_KEY);
        mulikaUrl = properties.getProperty(ServiceConstants.URL_CONFIG_KEY);
        mulikaAPIKey = properties.getProperty(ServiceConstants.API_KEY_CONFIG_KEY);

        System.out.println("app = " + app + "|module = " + module + "|reportInterval = " + reportInterval + "|serviceCount = " + SERVICE_COUNT + "" +
                "|servicePrefix = "+ SERVICE_PREFIX + "|mulikaUrl = " + mulikaUrl + "|mulikaAPIKey = " + mulikaAPIKey + "|loaded properties");
    }

    private static void reportStats() {
        try {
            String jsonRequest = getRequests();

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + mulikaAPIKey.trim());
            headers.put("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

            HTTPResponse response = HTTPClient.send(mulikaUrl, jsonRequest, "POST", "application/json", headers, 5000, 120000);

            System.out.println("mulika|" + "|request :" + jsonRequest + "|response : " + response + "|stats sent");
        } catch (IOException e) {
            System.out.println("mulika|Encountered exception" + e);
        }
    }

    private static String getRequests() throws JsonProcessingException {
        List<Map<String, Object>> mapList = new ArrayList<>();

        for (int i = 0; i <= SERVICE_COUNT; i++) {
            Map<String, Object> requestMap = new HashMap<>();
            String serviceName = SERVICE_PREFIX + "-" + i;
            requestMap.put("id", serviceName);
            requestMap.put("name", serviceName);
            requestMap.put("type", "SERVICE");
            requestMap.put("applicationName", app);
            requestMap.put("moduleName", module);
            requestMap.put("transactionTime", getNumberBetweenAnd(10, 10));
//            requestMap.put("totalDeliveries", getNumberBetweenAnd(600, 900));
            requestMap.put("totalRequests", getNumberBetweenAnd(3200, 3200));
            requestMap.put("totalDeliveries", requestMap.get("totalRequests"));
            requestMap.put("successTotal", requestMap.get("totalRequests"));
//            requestMap.put("successTotal", requestMap.get("totalRequests"));
//            requestMap.put("queueSize", getNumberBetweenAnd(100, 200));
            requestMap.put("queueSize", 0);
            requestMap.put("balance", getNumberBetweenAnd(2000, 2000));
            requestMap.put("amount", getNumberBetweenAnd(500, 1000));
            requestMap.put("rejectedMessages", 0);
            mapList.add(requestMap);
        }


        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.writeValueAsString(mapList);
    }

    private static int getNumberBetweenAnd(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }


}

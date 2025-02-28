package tech.kitucode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.kitucode.constants.ServiceConstants;
import tech.kitucode.domain.HTTPResponse;
import tech.kitucode.domain.KPIConfig;
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
    private static String app = "default";
    private static String module = "default";
    private static int reportInterval = 60000;
    private static int serviceCount = 1;
    private static String servicePrefix = "default";
    private static String mulikaUrl = "https://mulika.natujenge.ke/api/statistics/report-list";
    private static String mulikaAPIKey = null;
    private static KPIConfig kpiConfig;
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        loadConfig();

        logger.info("About to start stats push thread");
        mulikaThread = new Thread(() -> {
            while (true) {
                try {
                    try {
                        logger.info("About to sleep for {} milliseconds", reportInterval);
                        Thread.sleep(reportInterval);
                    } catch (InterruptedException ex) {
                        logger.error("Thread could not sleep, trying again", ex);
                        Thread.sleep(reportInterval);
                    }

                    reportStats();

                } catch (InterruptedException e) {
                    logger.error("Received an interrupt signal", e);
                    break;
                } catch (Exception ex) {
                    logger.error("Encountered exception. Proceeding", ex);
                }
            }
        }, "mulika-thread");

        logger.info("Successfully initialized mulika thread");

        mulikaThread.start();

        logger.info("Successfully started mulika thread. mulikaUrl =  {}, mulikaAPIKey = {}", mulikaUrl, mulikaAPIKey);
    }

    private static void loadConfig() {
        // load from environment variable

        Properties properties = new Properties();
        String fileName = System.getenv().get("MULIKA_REPORTING_CONFIG");
        if (fileName != null) {
            logger.info("Environment variable is set to {}|about to load properties", fileName);
            try {
                properties.load(Files.newInputStream(Paths.get(fileName)));
            } catch (IOException ex) {
                logger.error("Encountered an error when loading properties", ex);
            }
        } else {
            logger.info("Environment variable is not set. Loading from {}/{}", ServiceConstants.DEFAULT_CONFIG_LOCATION, ServiceConstants.DEFAULT_CONFIG_FILE_NAME);
            try {
                properties.load(Files.newInputStream(Paths.get(ServiceConstants.DEFAULT_CONFIG_LOCATION + "/" + ServiceConstants.DEFAULT_CONFIG_FILE_NAME)));
            } catch (IOException ex) {
                logger.error("encountered an error when loading properties from {}/{}|about to load from class path", ServiceConstants.DEFAULT_CONFIG_LOCATION, ServiceConstants.DEFAULT_CONFIG_FILE_NAME, ex);
                try {
                    properties.load(App.class.getClassLoader().getResourceAsStream(ServiceConstants.DEFAULT_CONFIG_FILE_NAME));
                } catch (IOException e) {
                    System.exit(0);
                }
            }
        }

        readSystemConfigs(properties);

        readKPIConfigs(properties);

        System.out.println("app = " + app + "|module = " + module + "|reportInterval = " + reportInterval + "|serviceCount = " + serviceCount +
                "|servicePrefix = " + servicePrefix + "|mulikaUrl = " + mulikaUrl + "|mulikaAPIKey = " + mulikaAPIKey + "|loaded properties");
    }

    private static void readKPIConfigs(Properties properties) {
        kpiConfig = new KPIConfig();
        kpiConfig.setTotalDeliveries(getRangeForKPI(properties, ServiceConstants.TOTAL_DELIVERIES_KPI_CONFIG));
        kpiConfig.setTotalRequests(getRangeForKPI(properties, ServiceConstants.TOTAL_REQUESTS_KPI_CONFIG));
        kpiConfig.setSuccessTotal(getRangeForKPI(properties, ServiceConstants.SUCCESS_TOTAL_KPI_CONFIG));
        kpiConfig.setTransactionTime(getRangeForKPI(properties, ServiceConstants.TRANSACTION_TIME_KPI_CONFIG));
        kpiConfig.setTotalDeliveries(getRangeForKPI(properties, ServiceConstants.TOTAL_DELIVERIES_KPI_CONFIG));
        kpiConfig.setQueueSize(getRangeForKPI(properties, ServiceConstants.QUEUE_SIZE_KPI_CONFIG));
        kpiConfig.setAmount(getRangeForKPI(properties, ServiceConstants.AMOUNT_KPI_CONFIG));
        kpiConfig.setRejectedMessages(getRangeForKPI(properties, ServiceConstants.REJECTED_MESSAGES_KPI_CONFIG));
        kpiConfig.setBalance(getRangeForKPI(properties, ServiceConstants.BALANCE_KPI_CONFIG));

        System.out.println("loaded kpi config = " + kpiConfig);
    }

    private static void readSystemConfigs(Properties properties) {
        app = properties.getProperty(ServiceConstants.APP_CONFIG_KEY, app);
        module = properties.getProperty(ServiceConstants.MODULE_CONFIG_KEY, module);
        reportInterval = Integer.parseInt(properties.getProperty(ServiceConstants.REPORT_INTERVAL_CONFIG_KEY, String.valueOf(reportInterval)));
        serviceCount = Integer.parseInt(properties.getProperty(ServiceConstants.SERVICE_COUNT_CONFIG_KEY, String.valueOf(serviceCount)));
        servicePrefix = properties.getProperty(ServiceConstants.SERVICE_PREFIX_CONFIG_KEY, servicePrefix);
        mulikaUrl = properties.getProperty(ServiceConstants.URL_CONFIG_KEY, mulikaUrl);
        mulikaAPIKey = properties.getProperty(ServiceConstants.API_KEY_CONFIG_KEY);
    }

    private static void reportStats() {
        try {
            String jsonRequest = getRequests();

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + mulikaAPIKey.trim());
            headers.put("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

            HTTPResponse response = HTTPClient.send(mulikaUrl, jsonRequest, "POST", "application/json", headers, 5000, 120000);

            logger.info("mulika|request : {}|response : {}|stats sent", jsonRequest, response);
        } catch (IOException e) {
            logger.error("mulika|encountered exception", e);
        }
    }

    private static String getRequests() throws JsonProcessingException {
        List<Map<String, Object>> mapList = new ArrayList<>();

        for (int i = 0; i <= serviceCount; i++) {
            Map<String, Object> requestMap = new HashMap<>();
            String serviceName = servicePrefix + "-" + i;
            requestMap.put("id", serviceName);
            requestMap.put("name", serviceName);
            requestMap.put("type", "SERVICE");
            requestMap.put("applicationName", app);
            requestMap.put("moduleName", module);
            requestMap.put(ServiceConstants.TRANSACTION_TIME_KPI, getValueFromKPIConfig(kpiConfig.getTransactionTime()));
            requestMap.put(ServiceConstants.TOTAL_DELIVERIES_KPI, getValueFromKPIConfig(kpiConfig.getTotalDeliveries()));
            requestMap.put(ServiceConstants.TOTAL_REQUESTS_KPI, getValueFromKPIConfig(kpiConfig.getTotalRequests()));
            requestMap.put(ServiceConstants.SUCCESS_TOTAL_KPI, getValueFromKPIConfig(kpiConfig.getSuccessTotal()));
            requestMap.put(ServiceConstants.QUEUE_SIZE_KPI, getValueFromKPIConfig(kpiConfig.getQueueSize()));
            requestMap.put(ServiceConstants.BALANCE_KPI, getValueFromKPIConfig(kpiConfig.getBalance()));
            requestMap.put(ServiceConstants.AMOUNT_KPI, getValueFromKPIConfig(kpiConfig.getAmount()));
            requestMap.put(ServiceConstants.REJECTED_MESSAGES_KPI, getValueFromKPIConfig(kpiConfig.getRejectedMessages()));
            mapList.add(requestMap);
        }


        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.writeValueAsString(mapList);
    }


    /**
     * gets a value from a kpi config
     * this is a list with 1 or 2 items
     * if the list has 2 items we get a value between the 2 values
     * if the list has 1 item we get the single value
     * if the list is empty return null
     *
     * @param valueList - range list for a given kpi
     * @return Integer
     */
    private static Integer getValueFromKPIConfig(List<Integer> valueList) {
        if (valueList == null) {
            return null;
        }

        if (valueList.size() > 1) {
            return (int) (Math.random() * (valueList.get(1) - valueList.get(0)) + valueList.get(0));
        } else if (valueList.size() == 1) {
            return valueList.get(0);
        } else {
            return null;
        }
    }


    /**
     * given properties and a kpi config key, get a list with the range of values set
     * returns a list with 0 or more items
     *
     * @param properties - configured application properties
     * @param kpi
     * @return List<Integer>
     */
    private static List<Integer> getRangeForKPI(Properties properties, String kpi) {
        return Arrays.stream(properties.getProperty(kpi).split(",")).map(value -> value.trim()).map(Integer::parseInt).toList();
    }

}

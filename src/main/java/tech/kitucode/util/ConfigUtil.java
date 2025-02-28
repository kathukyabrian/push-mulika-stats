package tech.kitucode.util;

import tech.kitucode.App;
import tech.kitucode.constants.ServiceConstants;
import tech.kitucode.domain.KPIConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ConfigUtil {
    private static String app = "default";
    private static String module = "default";
    private static int reportInterval = 60000;
    private static int serviceCount = 1;
    private static String servicePrefix = "default";
    private static String mulikaUrl = "https://mulika.natujenge.ke/api/statistics/report-list";
    private static String mulikaAPIKey = null;
    private static KPIConfig kpiConfig;

    public static void loadConfig() {
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

        readSystemConfigs(properties);

        readKPIConfigs(properties);

        System.out.println("app = " + app + "|module = " + module + "|reportInterval = " + reportInterval + "|serviceCount = " + serviceCount + "" +
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
        reportInterval = Integer.parseInt(properties.getProperty(ServiceConstants.REPORT_INTERVAL_CONFIG_KEY), reportInterval);
        serviceCount = Integer.parseInt(properties.getProperty(ServiceConstants.SERVICE_COUNT_CONFIG_KEY), serviceCount);
        servicePrefix = properties.getProperty(ServiceConstants.SERVICE_PREFIX_CONFIG_KEY, servicePrefix);
        mulikaUrl = properties.getProperty(ServiceConstants.URL_CONFIG_KEY, mulikaUrl);
        mulikaAPIKey = properties.getProperty(ServiceConstants.API_KEY_CONFIG_KEY, mulikaAPIKey);
    }

    private static int getNumberBetweenAnd(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    /**
     * gets a value from a kpi config
     * this is a list with 1 or 2 items
     * if the list has 2 items we get a value between the 2 values
     * if the list has 1 item we get the single value
     * if the list is empty return null
     *
     * @param valueList
     * @return
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

    private static List<Integer> getRangeForKPI(Properties properties, String kpi) {
        return Arrays.stream(properties.getProperty(kpi).split(",")).map(value -> value.trim()).map(Integer::parseInt).toList();
    }

    // getters

    public static String getApp() {
        return app;
    }

    public static String getModule() {
        return module;
    }

    public static int getReportInterval() {
        return reportInterval;
    }

    public static int getServiceCount() {
        return serviceCount;
    }

    public static String getServicePrefix() {
        return servicePrefix;
    }

    public static String getMulikaUrl() {
        return mulikaUrl;
    }

    public static String getMulikaAPIKey() {
        return mulikaAPIKey;
    }

    public static KPIConfig getKpiConfig() {
        return kpiConfig;
    }
}

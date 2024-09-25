package tech.kitucode.domain;


import tech.kitucode.domain.enumerations.ServiceType;

public class MulikaServiceDTO {
    private ServiceType type;

    private String name;

    private long totalRequests;

    private long successTotal;

    private long rejectedMessages;

    private int queueSize;

    private int transactionTime;

    public MulikaServiceDTO() {
    }

    public MulikaServiceDTO(ServiceType type, String name, long totalRequests, long successTotal, long rejectedMessages, int queueSize, int transactionTime) {
        this.type = type;
        this.name = name;
        this.totalRequests = totalRequests;
        this.successTotal = successTotal;
        this.rejectedMessages = rejectedMessages;
        this.queueSize = queueSize;
        this.transactionTime = transactionTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public long getSuccessTotal() {
        return successTotal;
    }

    public void setSuccessTotal(long successTotal) {
        this.successTotal = successTotal;
    }

    public long getRejectedMessages() {
        return rejectedMessages;
    }

    public void setRejectedMessages(long rejectedMessages) {
        this.rejectedMessages = rejectedMessages;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(int transactionTime) {
        this.transactionTime = transactionTime;
    }

    public int getAvgTransactionTime() {
        if (totalRequests == 0) {
            return 0;
        }
        return (int) (transactionTime / totalRequests);
    }

    public void addRequest(boolean successful, int transactionTime) {
        this.totalRequests++;
        if (successful) {
            this.successTotal++;
        }
        this.transactionTime += transactionTime;
    }

    public void resetCounters() {
        this.totalRequests = 0;
        this.successTotal = 0;
        this.rejectedMessages = 0;
        this.queueSize = 0;
        this.transactionTime = 0;
    }

    public ServiceType getType() {
        return type;
    }

    public void setType(ServiceType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MulikaServiceDTO{" +
            "name='" + name + '\'' +
            ", totalRequests=" + totalRequests +
            ", successTotal=" + successTotal +
            ", rejectedMessages=" + rejectedMessages +
            ", queueSize=" + queueSize +
            ", transactionTime=" + transactionTime +
            '}';
    }
}

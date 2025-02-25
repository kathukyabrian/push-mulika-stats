package tech.kitucode.domain;

import java.util.List;

public class KPIConfig {
    private List<Integer> totalRequests;
    private List<Integer> successTotal;
    private List<Integer> transactionTime;
    private List<Integer> totalDeliveries;
    private List<Integer> queueSize;
    private List<Integer> amount;
    private List<Integer> rejectedMessages;
    private List<Integer> balance;

    public List<Integer> getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(List<Integer> totalRequests) {
        this.totalRequests = totalRequests;
    }

    public List<Integer> getSuccessTotal() {
        return successTotal;
    }

    public void setSuccessTotal(List<Integer> successTotal) {
        this.successTotal = successTotal;
    }

    public List<Integer> getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(List<Integer> transactionTime) {
        this.transactionTime = transactionTime;
    }

    public List<Integer> getTotalDeliveries() {
        return totalDeliveries;
    }

    public void setTotalDeliveries(List<Integer> totalDeliveries) {
        this.totalDeliveries = totalDeliveries;
    }

    public List<Integer> getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(List<Integer> queueSize) {
        this.queueSize = queueSize;
    }

    public List<Integer> getAmount() {
        return amount;
    }

    public void setAmount(List<Integer> amount) {
        this.amount = amount;
    }

    public List<Integer> getRejectedMessages() {
        return rejectedMessages;
    }

    public void setRejectedMessages(List<Integer> rejectedMessages) {
        this.rejectedMessages = rejectedMessages;
    }

    public List<Integer> getBalance() {
        return balance;
    }

    public void setBalance(List<Integer> balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "KPIConfig{" +
                "totalRequests=" + totalRequests +
                ", successTotal=" + successTotal +
                ", transactionTime=" + transactionTime +
                ", totalDeliveries=" + totalDeliveries +
                ", queueSize=" + queueSize +
                ", amount=" + amount +
                ", rejectedMessages=" + rejectedMessages +
                ", balance=" + balance +
                '}';
    }
}

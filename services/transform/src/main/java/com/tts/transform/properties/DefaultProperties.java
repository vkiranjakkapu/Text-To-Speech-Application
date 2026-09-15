package com.tts.transform.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class DefaultProperties {

    private IncomingRequest request;

    private LimitEnforcements limits;

    public IncomingRequest getRequest() {
        return request;
    }

    public void setRequest(IncomingRequest request) {
        this.request = request;
    }

    public LimitEnforcements getLimits() {
        return limits;
    }

    public void setLimits(LimitEnforcements limits) {
        this.limits = limits;
    }

    public static class LimitEnforcements {

        private long maxMonthlyLimit = 10000l;
        private Double maxOverdraftLimit = 25.0;

        public long getMaxMonthlyLimit() {
            return maxMonthlyLimit;
        }

        public void setMaxMonthlyLimit(long maxMonthlyLimit) {
            this.maxMonthlyLimit = maxMonthlyLimit;
        }

        public Double getMaxOverdraftLimit() {
            return maxOverdraftLimit;
        }

        public void setMaxOverdraftLimit(Double maxOverdraftLimit) {
            this.maxOverdraftLimit = maxOverdraftLimit;
        }

    }

    public static class IncomingRequest {

        private long minTextLength = 2l;

        private long maxTextLength = 200l;

        private long maxDoctextLength = 500l;

        public long getMaxTextLength() {
            return maxTextLength;
        }

        public void setMaxTextLength(long maxCharLength) {
            this.maxTextLength = maxCharLength;
        }

        public long getMinTextLength() {
            return minTextLength;
        }

        public void setMinTextLength(long minCharLength) {
            this.minTextLength = minCharLength;
        }

        public long getMaxDoctextLength() {
            return maxDoctextLength;
        }

        public void setMaxDoctextLength(long maxDoctextLength) {
            this.maxDoctextLength = maxDoctextLength;
        }

    }

}

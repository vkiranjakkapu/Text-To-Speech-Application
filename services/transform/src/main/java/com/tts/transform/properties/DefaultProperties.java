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

        public long getMaxMonthlyLimit() {
            return maxMonthlyLimit;
        }

        public void setMaxMonthlyLimit(long maxMonthlyLimit) {
            this.maxMonthlyLimit = maxMonthlyLimit;
        }

    }

    public static class IncomingRequest {

        private long maxCharLength = 55l;

        public long getMaxCharLength() {
            return maxCharLength;
        }

        public void setMaxCharLength(long maxCharLength) {
            this.maxCharLength = maxCharLength;
        }

    }

}

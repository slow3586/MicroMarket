package com.slow3586.micromarket.api.delivery;

public final class DeliveryTopics {
    private static final String BALANCE = "balance";

    public static class Status {
        private static final String STATUS = ".status";
        public final static String AWAITING = ".awaiting";
        public final static String SENT = ".sent";
        public final static String RECEIVED = ".received";
    }
}

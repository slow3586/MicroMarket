package com.slow3586.micromarket.api.balance;

public final class BalanceTopics {
    private static final String BALANCE = "balance";
    private static final String STATUS = ".status";

    public static class Replenish {
        private final static String REPLENISH = ".replenish";
        public final static String NEW = BALANCE + REPLENISH + STATUS + ".new";
    }

    public static class Transfer {
        private final static String TRANSFER = ".transfer";
        public final static String AWAITING = BALANCE + TRANSFER + STATUS + ".awaiting";
        public final static String RESERVED = BALANCE + TRANSFER + STATUS + ".reserved";
        public final static String COMPLETED = BALANCE + TRANSFER + STATUS + ".completed";
    }
}

package com.slow3586.micromarket.api.balance;

public final class BalanceTopics {
    private static final String NAME = "balance";
    private static final String STATUS = ".status";

    public class Replenish {
        public final static String NEW = NAME + STATUS + ".new";
    }

    public class Transfer {
        public final static String AWAITING = NAME + STATUS + ".awaiting";
        public final static String RESERVED = NAME + STATUS + ".reserved";
        public final static String COMPLETED = NAME + STATUS + ".completed";
    }
}

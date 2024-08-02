package com.slow3586.micromarket.api.order;

public final class OrderTopics {
    private static final String NAME = "order";

    public static final class Transaction {
        private static final String TRANSACTION = ".transaction";
        public final static String NEW = NAME + TRANSACTION + ".new";
        public final static String PRODUCT = NAME + TRANSACTION + ".product";
        public final static String USER = NAME + TRANSACTION + ".user";
        public final static String STOCK = NAME + TRANSACTION + ".stock";
        public final static String PAYMENT = NAME + TRANSACTION + ".payment";
        public final static String DELIVERY = NAME + TRANSACTION + ".delivery";
        public final static String COMPLETED = NAME + TRANSACTION + ".completed";
        public final static String ERROR = NAME + TRANSACTION + ".error";
        public static final class Awaiting {
            private static final String AWAITING = ".awaiting";

            public final static String PAYMENT = Transaction.PAYMENT + AWAITING;
            public final static String DELIVERY = Transaction.DELIVERY + AWAITING;
        }
    }

    public static final class Status {
        private static final String STATUS = ".status";
        public final static String PAID = NAME + STATUS + ".paid";
        public final static String CANCELLED = NAME + STATUS + ".cancelled";
        public final static String COMPLETED = NAME + STATUS + ".completed";
    }
}

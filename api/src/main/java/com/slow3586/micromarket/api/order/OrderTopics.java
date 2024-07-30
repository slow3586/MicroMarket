package com.slow3586.micromarket.api.order;

public final class OrderTopics {
    private static final String NAME = "order";

    public static final class Transaction {
        private static final String TRANSACTION = ".transaction";
        public final static String NEW = NAME + TRANSACTION + ".new";
        public final static String PRODUCT = NAME + TRANSACTION + ".product";
        public final static String USER = NAME + TRANSACTION + ".user";
        public final static String STOCK = NAME + TRANSACTION + ".stock";
        public final static String BALANCE = NAME + TRANSACTION + ".balance";
        public final static String CONFIRMATION = NAME + TRANSACTION + ".confirmation";
        public final static String PUBLISH = NAME + TRANSACTION + ".publish";
        public final static String COMPLETED = NAME + TRANSACTION + ".completed";
        public final static String ERROR = NAME + TRANSACTION + ".error";
        public static final class Awaiting {
            private static final String AWAITING = ".awaiting";

            public final static String BALANCE = Transaction.BALANCE + AWAITING;
            public final static String CONFIRMATION = Transaction.CONFIRMATION + AWAITING;
        }
    }

    public static final class Request {
        private static final String REQUEST = ".request";
        private static final String RESPONSE = ".response";
        public final static String REQUEST_CREATE = NAME + REQUEST + ".create";
        public final static String REQUEST_CREATE_RESPONSE = REQUEST_CREATE + RESPONSE;
        public final static String REQUEST_COMPLETED = NAME + REQUEST + ".completed";
        public final static String REQUEST_COMPLETED_RESPONSE = REQUEST_COMPLETED + RESPONSE;
        public final static String REQUEST_CANCEL = NAME + REQUEST + ".cancel";
        public final static String REQUEST_CANCEL_RESPONSE = REQUEST_CANCEL + RESPONSE;
    }

    public static final class Status {
        private static final String STATUS = ".status";
        public final static String PAID = NAME + STATUS + ".paid";
        public final static String CANCELLED = NAME + STATUS + ".cancelled";
        public final static String COMPLETED = NAME + STATUS + ".completed";
    }
}

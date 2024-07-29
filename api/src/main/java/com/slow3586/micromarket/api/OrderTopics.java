package com.slow3586.micromarket.api;

public final class OrderTopics {
    private static final String ERROR = ".error";
    private static final String NAME = "order";
    private static final String TRANSACTION = ".transaction";
    private static final String REQUEST = ".request";
    private static final String RESPONSE = ".response";
    private static final String STATUS = ".status";

    public static final class Transaction {
        public final static String CREATED = NAME + TRANSACTION + ".created";
        public final static String PRODUCT = NAME + TRANSACTION + ".product";
        public final static String SHOP = NAME + TRANSACTION + ".shop";
        public final static String CUSTOMER = NAME + TRANSACTION + ".customer";
        public final static String STOCK = NAME + TRANSACTION + ".stock";
        public final static String BALANCE = NAME + TRANSACTION + ".balance";
        public final static String INVENTORY = NAME + TRANSACTION + ".inventory";
        public final static String PAYMENT = NAME + TRANSACTION + ".payment";
        public final static String PUBLISH = NAME + TRANSACTION + ".publish";
        public final static String PAID = NAME + TRANSACTION + ".paid";
        public final static String COMPLETED = NAME + TRANSACTION + ".completed";
        public final static String ERROR = NAME + TRANSACTION + OrderTopics.ERROR;
    }

    public static final class Request {
        public final static String REQUEST_CREATE = NAME + REQUEST + ".create";
        public final static String REQUEST_CREATE_RESPONSE = REQUEST_CREATE + RESPONSE;
        public final static String REQUEST_COMPLETED = NAME + REQUEST + ".completed";
        public final static String REQUEST_COMPLETED_RESPONSE = REQUEST_COMPLETED + RESPONSE;
        public final static String REQUEST_CANCEL = NAME + REQUEST + ".cancel";
        public final static String REQUEST_CANCEL_RESPONSE = REQUEST_CANCEL + RESPONSE;
    }

    public static final class Status {
        public final static String STATUS_CANCELLED = NAME + STATUS + ".cancelled";
        public final static String STATUS_COMPLETED = NAME + STATUS + ".completed";
    }
}

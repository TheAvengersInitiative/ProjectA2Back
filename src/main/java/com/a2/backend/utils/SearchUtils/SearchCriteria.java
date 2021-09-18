package com.a2.backend.utils.SearchUtils;

public class SearchCriteria {
    private String key;
    private String value;
    private String operation;

    public SearchCriteria(String key, String operation, String value) {
        this.key = key;
        this.value = value;
        this.operation = operation;
    }

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public String getOperation() {
        return operation;
    }
}

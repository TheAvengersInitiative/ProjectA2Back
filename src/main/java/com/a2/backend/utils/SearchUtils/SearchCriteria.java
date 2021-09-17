package com.a2.backend.utils.SearchUtils;

public class SearchCriteria {
    private String key;

    private String value;

    public SearchCriteria(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }
}

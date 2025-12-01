package com.example.bajaj.dto;

public class SubmitRequest {
    // The task PDF indicates the body should be:
    // { "finalQuery": "YOUR_SQL_QUERY_HERE" }
    // Some PDFs show keys with spaces in the sample; ensure you match the exact key expected by the API.

    private String finalQuery;

    public SubmitRequest() {}

    public SubmitRequest(String finalQuery) {
        this.finalQuery = finalQuery;
    }

    public String getFinalQuery() {
        return finalQuery;
    }

    public void setFinalQuery(String finalQuery) {
        this.finalQuery = finalQuery;
    }
}

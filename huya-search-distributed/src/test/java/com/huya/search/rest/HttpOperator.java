package com.huya.search.rest;

import okhttp3.*;

import java.io.IOException;

public class HttpOperator {

    private static final OkHttpClient client = new OkHttpClient();

    private String url;

    public HttpOperator(String url) {
        this.url = url;
    }

    public void insert(String json) throws IOException {
        ignoreResponse("/insert/insert", json);
    }

    public String sql(String sql) throws IOException {
        return ResponseToString("/search/sql", sql);
    }

    public void create(String json) throws IOException {
        ignoreResponse("/meta/create", json);
    }

    public void remove(String table) throws IOException {
        ignoreResponse("/meta/remove", table);
    }

    public void update(String json) throws IOException {
        ignoreResponse("/meta/update", json);
    }

    public String view(String table) throws IOException {
        return ResponseToString("/meta/view", table);
    }

    public void open(String table) throws IOException {
        ignoreResponse("/meta/open", table);
    }

    public void close(String table) throws IOException {
        ignoreResponse("/meta/close", table);
    }

    public void refresh(String refreshJson) throws IOException {
        ignoreResponse("/insert/refresh", refreshJson);
    }

    private Response requestGetResponse(String path, String queryContext) throws IOException {
        Request request = new Request.Builder()
                .url(url + path)
                .post(RequestBody.create(MediaType.parse("raw"), queryContext))
                .build();
        return client.newCall(request).execute();
    }

    private void ignoreResponse(String path, String queryContext) throws IOException {
        Response response = requestGetResponse(path, queryContext);
        assert response.code() == 200;
        response.body().close();
    }

    private String ResponseToString(String path, String queryContext) throws IOException {
        Response response = requestGetResponse(path, queryContext);
        assert response.code() == 200;
        try (ResponseBody responseBody = response.body()) {
            String str = responseBody.string();
            responseBody.close();
            return str;
        }
    }

}

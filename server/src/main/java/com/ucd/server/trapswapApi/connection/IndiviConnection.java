package com.ucd.server.trapswapApi.connection;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

/**
 * Created by JLcan on 2016/6/3.
 */
public class IndiviConnection {


    private String baseUrl;
    private CloseableHttpClient client;
    private static RequestConfig defaultRequestConfig = RequestConfig.custom()
            .setConnectTimeout(10000).setConnectionRequestTimeout(10000)
            .setSocketTimeout(30000).build();


    public IndiviConnection(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = HttpClients.createDefault();
    }

    /**
     * HTTP GET
     *
     * @param path relative path
     * @return HttpResponse
     * @throws Exception
     */

    public static CloseableHttpResponse get(String path, IndiviConnection connection) throws Exception {
        String fullUrl = connection.baseUrl + path;
        HttpGet getRequest = new HttpGet(fullUrl);
        getRequest.setConfig(defaultRequestConfig);
        CloseableHttpResponse response = connection.client.execute(getRequest);
        getRequest.abort();
        return response;
    }

    public static CloseableHttpResponse getNOAbort(String path, IndiviConnection connection) throws Exception {
        String fullUrl = connection.baseUrl + path;
        HttpGet getRequest = new HttpGet(fullUrl);
        getRequest.setConfig(defaultRequestConfig);
        CloseableHttpResponse response = connection.client.execute(getRequest);
        return response;
    }

    /**
     * HTTP POST
     *
     * @param path    relative path
     * @param content post content
     * @return HttpResponse
     * @throws Exception
     */
    public static CloseableHttpResponse post1(String path, String content, IndiviConnection connection) throws Exception {
        String fullUrl = connection.baseUrl + path;
        HttpPost postRequest = new HttpPost(fullUrl);
        postRequest.setConfig(defaultRequestConfig);
        postRequest.setEntity(new StringEntity(content));
        CloseableHttpResponse response = connection.client.execute(postRequest);
        postRequest.abort();
        return response;
    }

    /**
     * HTTP POST(without content)
     *
     * @param path relative path
     * @return HttpResponse
     * @throws Exception
     */
    public static CloseableHttpResponse post1(String path, IndiviConnection connection) throws Exception {
        // JSONObject postContent = new JSONObject();
        String fullUrl = connection.baseUrl + path;
        HttpPost postRequest = new HttpPost(fullUrl);
        postRequest.setConfig(defaultRequestConfig);
        // postRequest.setEntity(new StringEntity(postContent.toString()));
        CloseableHttpResponse response = connection.client.execute(postRequest);
        postRequest.abort();
        return response;
    }


    /**
     * HTTP DELETE
     *
     * @param path    relative path
     * @param content delete content
     * @return HttpResponse
     * @throws Exception
     */
    public static CloseableHttpResponse delete(String path, String content, IndiviConnection connection) throws Exception {
        String fullUrl = connection.baseUrl + path;
        MyHttpDelete deleteRequest = new MyHttpDelete(fullUrl);
        if (content != null) {
            deleteRequest.setEntity(new StringEntity(content));
        }
        CloseableHttpResponse response = connection.client.execute(deleteRequest);
        deleteRequest.abort();
        return response;
    }

    public static CloseableHttpResponse delete(String path, IndiviConnection connection) throws Exception {
        String fullUrl = connection.baseUrl + path;
        MyHttpDelete deleteRequest = new MyHttpDelete(fullUrl);
        // deleteRequest.addHeader("Authorization", "Basic YWRtaW46YWRtaW4=");
        // deleteRequest.addHeader("Content-Type", "application/json");
        // deleteRequest.addHeader("User-Agent", "EliteTagger_v3.0.5-BETA");
        CloseableHttpResponse response = connection.client.execute(deleteRequest);
        deleteRequest.abort();
        return response;
    }


    /**
     * HTTP PUT
     *
     * @param path    relative address
     * @param content put content
     * @return HttpResponse
     * @throws Exception
     */
    public static CloseableHttpResponse put(String path, String content, IndiviConnection connection) throws Exception {
        String fullUrl = connection.baseUrl + path;
        HttpPut putRequest = new HttpPut(fullUrl);
        putRequest.setConfig(defaultRequestConfig);
        if (content != null) {
            putRequest.setEntity(new StringEntity(content));
        }
        CloseableHttpResponse response = connection.client.execute(putRequest);
        putRequest.abort();
        return response;
    }


    public void close() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void printLineAndTitle(String funcName) throws Exception {
        System.out.println("\n\n ----------------------------------------------------");
        System.out.println("\n The return of " + funcName + ": \n");
    }
}

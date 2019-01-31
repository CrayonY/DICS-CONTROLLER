package com.ucd.server.trapswapApi.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Created by JLcan on 2016/6/3.
 */
public class Connection {

    private static String baseUrl;
    private String baseUrl1;
    private static CloseableHttpClient client;
    private CloseableHttpClient client1;
    private static RequestConfig defaultRequestConfig = RequestConfig.custom()
			.setConnectTimeout(10000).setConnectionRequestTimeout(10000)
			.setSocketTimeout(30000).build();

    public Connection(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = HttpClients.createDefault();
    }
    public Connection(String baseUrl1,CloseableHttpClient client1) {
        this.baseUrl1 = baseUrl1;
        //this.client = HttpClients.createDefault();
        this.client1 = client1;
    }

    public Connection(String baseUrl1,String type) {
        this.baseUrl1 = baseUrl1;
        this.client1 = HttpClients.createDefault();
    }

    /**
     * HTTP GET
     *
     * @param path relative path
     * @return HttpResponse
     * @throws Exception
     */

    public static CloseableHttpResponse get(String path) throws Exception {
        String fullUrl = baseUrl + path;
        HttpGet getRequest = new HttpGet(fullUrl);
        getRequest.setConfig(defaultRequestConfig);
        CloseableHttpResponse response = client.execute(getRequest);
        getRequest.abort();
        return response;
    }

    public static CloseableHttpResponse getNOAbort(String path) throws Exception {
        String fullUrl = baseUrl + path;
        HttpGet getRequest = new HttpGet(fullUrl);
        getRequest.setConfig(defaultRequestConfig);
        CloseableHttpResponse response = client.execute(getRequest);
        return response;
    }

    public static CloseableHttpResponse get1(String path,Connection connection) throws Exception {
        String fullUrl = connection.baseUrl1 + path;
        HttpGet getRequest = new HttpGet(fullUrl);
        getRequest.setConfig(defaultRequestConfig);
        CloseableHttpResponse response = connection.client1.execute(getRequest);
        getRequest.abort();
        return response;
    }

    public static CloseableHttpResponse getNOAbort1(String path,Connection connection) throws Exception {
        String fullUrl = connection.baseUrl1 + path;
        HttpGet getRequest = new HttpGet(fullUrl);
        getRequest.setConfig(defaultRequestConfig);
        CloseableHttpResponse response = connection.client1.execute(getRequest);
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
    public static CloseableHttpResponse post(String path, String content) throws Exception {
        String fullUrl = baseUrl + path;
        HttpPost postRequest = new HttpPost(fullUrl);
        postRequest.setConfig(defaultRequestConfig);
        postRequest.setEntity(new StringEntity(content));
        CloseableHttpResponse response = client.execute(postRequest);
        postRequest.abort();
        return response;
    }

    /**
     * HTTP POST(without content)
     *
     * @param path    relative path
     * @return HttpResponse
     * @throws Exception
     */
    public static CloseableHttpResponse post(String path) throws Exception {
        // JSONObject postContent = new JSONObject();
        String fullUrl = baseUrl + path;
        HttpPost postRequest = new HttpPost(fullUrl);
        postRequest.setConfig(defaultRequestConfig);
        // postRequest.setEntity(new StringEntity(postContent.toString()));
        CloseableHttpResponse response = client.execute(postRequest);
        postRequest.abort();
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
    public static CloseableHttpResponse post1(String path, String content, Connection connection) throws Exception {
        String fullUrl = connection.baseUrl1 + path;
        HttpPost postRequest = new HttpPost(fullUrl);
        postRequest.setConfig(defaultRequestConfig);
        postRequest.setEntity(new StringEntity(content));
        CloseableHttpResponse response = connection.client1.execute(postRequest);
        postRequest.abort();
        return response;
    }

    /**
     * HTTP POST(without content)
     *
     * @param path    relative path
     * @return HttpResponse
     * @throws Exception
     */
    public static CloseableHttpResponse post1(String path,Connection connection) throws Exception {
        // JSONObject postContent = new JSONObject();
        String fullUrl = connection.baseUrl1 + path;
        HttpPost postRequest = new HttpPost(fullUrl);
        postRequest.setConfig(defaultRequestConfig);
        // postRequest.setEntity(new StringEntity(postContent.toString()));
        CloseableHttpResponse response = connection.client1.execute(postRequest);
        postRequest.abort();
        return response;
    }


    public static String post2(String url, String jsonString) {
        CloseableHttpResponse response = null;
        BufferedReader in = null;
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(30000).setSocketTimeout(30000).build();
            httpPost.setConfig(requestConfig);
            httpPost.setConfig(requestConfig);
            httpPost.addHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setEntity(new StringEntity(jsonString, Charset.forName("UTF-8")));
            response = client.execute(httpPost);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            result = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }




    /**
     * HTTP DELETE
     *
     * @param path    relative path
     * @param content delete content
     * @return HttpResponse
     * @throws Exception
     */
    public static CloseableHttpResponse delete(String path, String content) throws Exception {
        String fullUrl = baseUrl + path;
        MyHttpDelete deleteRequest = new MyHttpDelete(fullUrl);
        if (content != null) {
            deleteRequest.setEntity(new StringEntity(content));
        }
        CloseableHttpResponse response = client.execute(deleteRequest);
        deleteRequest.abort();
        return response;
    }
    public static CloseableHttpResponse delete(String path) throws Exception {
        String fullUrl = baseUrl + path;
        MyHttpDelete deleteRequest = new MyHttpDelete(fullUrl);
        // deleteRequest.addHeader("Authorization", "Basic YWRtaW46YWRtaW4=");
        // deleteRequest.addHeader("Content-Type", "application/json");
        // deleteRequest.addHeader("User-Agent", "EliteTagger_v3.0.5-BETA");
        CloseableHttpResponse response = client.execute(deleteRequest);
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
    public static CloseableHttpResponse put(String path, String content) throws Exception {
        String fullUrl = baseUrl + path;
        HttpPut putRequest = new HttpPut(fullUrl);
        putRequest.setConfig(defaultRequestConfig);
        if (content != null) {
            putRequest.setEntity(new StringEntity(content));
        }
        CloseableHttpResponse response = client.execute(putRequest);
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

    public void close1() {
        if (client1 != null) {
            try {
                client1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void printLineAndTitle( String funcName) throws Exception{
        System.out.println("\n\n ----------------------------------------------------");
        System.out.println("\n The return of " + funcName + ": \n");
    }
}

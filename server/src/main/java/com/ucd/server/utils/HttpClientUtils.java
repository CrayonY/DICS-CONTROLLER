package com.ucd.server.utils;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtils {

    private static final Logger log = LoggerFactory.getLogger(HttpClientUtils.class);

    private static RequestConfig defaultRequestConfig = RequestConfig.custom()
            .setConnectTimeout(10000).setConnectionRequestTimeout(10000)
            .setSocketTimeout(30000).build();

    /**
     * 方法功能说明：POST数据
     * 创建：2017年3月9日 by admin
     * 修改：日期 by 修改者
     * 修改内容：
     *
     * @return String
     * @参数： @param url
     * @参数： @param content
     * @参数： @param contentType
     * @参数： @return
     * @参数： @throws Exception
     */
    public static String postString(String url, String content, String contentType, Map<String, String> headers) throws Exception {

        if (StringUtils.isEmpty(url)) {
            throw new NullPointerException("URL为空");
        }

        log.debug("[HTTP]发送数据: url={}, content={}", url, content);

        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpPost method = new HttpPost(url);

        if (MapUtils.isNotEmpty(headers)) {
            for (String key : headers.keySet()) {
                method.addHeader(key, headers.get(key));
            }
        }

        method.setConfig(defaultRequestConfig);

        method.addHeader("Content-Type", contentType);

        method.setEntity(new StringEntity(content, "utf-8"));

        String responseContent = null;

        try {
            // 执行请求
            HttpResponse response = httpclient.execute(method);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                // 成功
                responseContent = EntityUtils.toString(response.getEntity(), "utf-8");

                log.debug("[HTTP]接收数据: {}, {}", statusCode, responseContent);

                return responseContent;

            } else {
                // 失败
                responseContent = EntityUtils.toString(response.getEntity(), "UTF-8");

                log.error("[HTTP]接收数据: {}, {}", statusCode, responseContent);

                throw new Exception("[HTTP]异常响应状态:" + statusCode);
            }
        } catch (Exception ex) {

            log.error("***发送消息时出现异常: " + url, ex);

            throw ex;

        } finally {
            if (method != null) {
                // 释放连接
                method.releaseConnection();
            }
            if (httpclient != null) {
                // 关闭实例
                httpclient.close();
            }
        }
    }

    /**
     * 方法功能说明：PUT数据
     * 创建：2017年3月9日 by admin
     * 修改：日期 by 修改者
     * 修改内容：
     *
     * @return String
     * @参数： @param url
     * @参数： @param content
     * @参数： @param contentType
     * @参数： @return
     * @参数： @throws Exception
     */
    public static String putString(String url, String content, String contentType, Map<String, String> headers) throws Exception {

        if (StringUtils.isEmpty(url)) {
            throw new NullPointerException("URL为空");
        }

        log.debug("[HTTP]发送数据: url={}, content={}", url, content);

        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpPut method = new HttpPut(url);

        for (String key : headers.keySet()) {
            method.addHeader(key, headers.get(key));
        }

        method.setConfig(defaultRequestConfig);

        method.addHeader("Content-Type", contentType);

        method.setEntity(new StringEntity(content, "utf-8"));

        String responseContent = null;

        try {
            // 执行请求
            HttpResponse response = httpclient.execute(method);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                // 成功
                responseContent = EntityUtils.toString(response.getEntity(), "utf-8");

                log.debug("[HTTP]接收数据: {}, {}", statusCode, responseContent);

                return responseContent;

            } else {
                // 失败
                responseContent = EntityUtils.toString(response.getEntity(), "UTF-8");

                log.error("[HTTP]接收数据: {}, {}", statusCode, responseContent);

                throw new Exception("[HTTP]异常响应状态:" + statusCode);
            }
        } catch (Exception ex) {

            log.error("***发送消息时出现异常: " + url, ex);

            throw ex;

        } finally {
            if (method != null) {
                // 释放连接
                method.releaseConnection();
            }
            if (httpclient != null) {
                // 关闭实例
                httpclient.close();
            }
        }
    }

    /**
     * 方法功能说明：以GET的方式获取数据
     * 创建：2017年3月15日 by Liu.Wen
     * 修改：日期 by 修改者
     * 修改内容：
     *
     * @return String
     * @参数： @param url
     * @参数： @param content
     * @参数： @param contentType
     * @参数： @param headers
     * @参数： @return
     */
    public static final String getString(String url, String contentType, Map<String, String> headers) throws Exception {

        if (StringUtils.isEmpty(url)) {
            throw new NullPointerException("URL为空");
        }

        log.debug("[HTTP]发送数据: url={}", url);

        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet method = new HttpGet(url);

        for (String key : headers.keySet()) {
            method.addHeader(key, headers.get(key));
        }

        method.setConfig(defaultRequestConfig);

        method.addHeader("Content-Type", contentType);

        String responseContent = null;

        try {
            // 执行请求
            HttpResponse response = httpclient.execute(method);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                // 成功
                responseContent = EntityUtils.toString(response.getEntity(), "utf-8");

                log.debug("[HTTP]响应数据: {}, {}", statusCode, responseContent);

                return responseContent;

            } else {
                // 失败
                responseContent = EntityUtils.toString(response.getEntity(), "UTF-8");

                log.error("[HTTP]响应数据: {}, {}", statusCode, responseContent);

                throw new Exception("[HTTP]异常响应状态:" + statusCode);
            }
        } catch (Exception ex) {

            log.error("***HTTP请求调用出现异常: " + url, ex);

            throw ex;

        } finally {
            if (method != null) {
                // 释放连接
                method.releaseConnection();
            }
            if (httpclient != null) {
                // 关闭实例
                httpclient.close();
            }
        }
    }

}

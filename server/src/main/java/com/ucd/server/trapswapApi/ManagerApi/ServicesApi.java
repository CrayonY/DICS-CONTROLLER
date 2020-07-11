package com.ucd.server.trapswapApi.ManagerApi;

import com.ucd.server.trapswapApi.connection.Connection;
import com.ucd.server.utils.HttpClientUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by JLcan on 2016/6/13.
 */
public class ServicesApi {

    private static final Logger log = LoggerFactory.getLogger(ServicesApi.class);

    /**
     * 2.11.1 所有服务查询
     * //     * @param clusterId 待查询集群的id;可选参数
     * //     * @param global global=true只返回全局服务；global=false只返回非全局服务;可选参数
     * //     * @param type 指定查询服务类型;可选参数
     * //       * @param endTimeStamp 用于限制查找范围的终止时间戳;
     *
     * @return * @throws Exception;
     */
    public static String getAllServices(Connection conObject) throws Exception {
        String responseContent = null;
        //String url = "/services?clusterId="+clusterId+"&type="+type;
        String url = "/services";
        try {
            // 执行请求
            //CloseableHttpResponse response = conObject.getNOAbort(url);
            CloseableHttpResponse response = conObject.getNOAbort1(url, conObject);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                // 成功
                responseContent = EntityUtils.toString(response.getEntity(), "utf-8");

                log.debug("[HTTP]响应数据: {}, {}", statusCode/*, responseContent*/);

                return responseContent;

            } else {
                // 失败
                responseContent = EntityUtils.toString(response.getEntity(), "UTF-8");

                log.error("[HTTP]响应数据: {}, {}", statusCode/*, responseContent*/);

                throw new Exception("[HTTP]异常响应状态:" + statusCode);
            }
        } catch (Exception ex) {

            log.error("***HTTP请求调用出现异常: " + url, ex);

            throw ex;
        }
    }


}

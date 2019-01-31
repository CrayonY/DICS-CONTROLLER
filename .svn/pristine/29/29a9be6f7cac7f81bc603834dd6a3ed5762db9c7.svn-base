package com.ucd.server.trapswapApi.ManagerApi;

import com.ucd.server.trapswapApi.connection.Connection;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Created by JLcan on 2016/6/13.
 */
public class MetricApi {
	
	private static final Logger log = LoggerFactory.getLogger(MetricApi.class);
    /**
     * 2.11.1 集群指标查询
     * @param clusterId 待查询集群的id;
     * @param metricsName 查询指标名称;
     * @param startTimeStamp 用于限制查找范围的起始时间戳;
     * @param endTimeStamp 用于限制查找范围的终止时间戳;
     * @return 是否修改成功;
     * * @throws Exception;
     */
    public static String getClusterMetric(Connection conObject, int clusterId, String metricsName, String startTimeStamp, String endTimeStamp ) throws Exception{
    	String responseContent = null;
        String url = "/clusters/" + clusterId + "/metric?metricnames=" + metricsName + "&start=" + startTimeStamp + "&end=" + endTimeStamp;
        try {
			// 执行请求
        	CloseableHttpResponse response = conObject.get(url);

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
			
		} 
    }

    /**
     * 2.11.2 服务指标查询
     * @param serviceId 待查询服务的id;
     * @param metricsName 查询指标名称;
     * @param startTimeStamp 用于限制查找范围的起始时间戳;
     * @param endTimeStamp 用于限制查找范围的终止时间戳;
     * @return 是否修改成功;
     * * @throws Exception;
     */
    public static String getServiceMetric( Connection conObject, int serviceId, String metricsName, String startTimeStamp, String endTimeStamp ) throws Exception{
    	String responseContent = null;
        String url = "/services/" + serviceId + "/metric?metricnames=" + metricsName + "&start=" + startTimeStamp + "&end=" + endTimeStamp;
        try {
			// 执行请求
        	CloseableHttpResponse response = conObject.get(url);

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
			
		} 
    }

    /**
     * 2.11.3 节点指标查询
     * @param nodeId 待查询节点的id;
     * @param metricsName 查询指标名称;
     * @param startTimeStamp 用于限制查找范围的起始时间戳;
     * @param endTimeStamp 用于限制查找范围的终止时间戳;
     * @return 是否修改成功;
     * * @throws Exception;
     */
    public static String getNodeMetric( Connection conObject, int nodeId, String metricsName, String startTimeStamp, String endTimeStamp ) throws Exception{
    	String responseContent = null;
        String url = "/nodes/" + nodeId + "/metric?metricnames=" + metricsName + "&start=" + startTimeStamp + "&end=" + endTimeStamp;
        try {
			// 执行请求
        	CloseableHttpResponse response = conObject.get(url);

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
			
		} 
    }

    /**
     * 2.11.4 服务角色指标查询
     * @param serviceRoleId 待查询节点的id;
     * @param metricsName 查询指标名称;
     * @param startTimeStamp 用于限制查找范围的起始时间戳;
     * @param endTimeStamp 用于限制查找范围的终止时间戳;
     * @return 是否修改成功;
     * * @throws Exception;
     */
    public static String getServiceRoleMetric( Connection conObject, int serviceRoleId, String metricsName, String startTimeStamp, String endTimeStamp ) throws Exception{
    	String responseContent=null;
        String url = "/roles/" + serviceRoleId + "/metric?metricnames=" + metricsName + "&start=" + startTimeStamp + "&end=" + endTimeStamp;
        try {
			// 执行请求
        	CloseableHttpResponse response = conObject.get(url);

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
			conObject.close();
		}
    }
}

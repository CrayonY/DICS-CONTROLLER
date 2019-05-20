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
public class InceptorApi {
	
	private static final Logger log = LoggerFactory.getLogger(InceptorApi.class);
    /**
     * 2.11.1 用于获取Spark Job相关的详细信息
//     * @param userid：用户名，是可选值，默认为hive
//     * @param status：待查询Job的状态属性，有running、succeeded、failed、unknown四种选项，分别表示查询正在执行的Jobs、成功的Jobs、失败的Jobs、未知状态的Jobs。是可选值，默认返回所有状态的Jobs。
//     * @param jobid：获取特定Job的信息。是可选值，默认返回所有Jobs信息
//     * @param timestamp：查询该时间点之后的所有查询记录。为可选值，默认返回所有查询记录。
	 Java Date格式，共支持六种不同的输入：
	 格式	                          例子
	 yyyy                             2016
	 yyyyMM                           201601
	 yyyyMMdd                         20160101
	 yyyyMMddHH                       2016010114
	 yyyyMMddHHmm                     201601011430
	 yyyyMMddHHmmss                   20160101143020

	 Unix Epoch格式，即距离Unix 1979-01-01 00:00:00.00的毫秒数，如1451629820000L等价于20160101143020。
     * @return
     * * @throws Exception;
     */
    public static String getjobs(Connection conObject, int jobid, String userid, String status, String timestamp ) throws Exception{
    	String responseContent = null;
        //String url = "/jobs";
		String url = "/jobs?status="+status;
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

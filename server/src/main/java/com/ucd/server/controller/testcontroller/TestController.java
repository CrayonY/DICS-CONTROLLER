package com.ucd.server.controller.testcontroller;

import com.ucd.common.VO.ResultVO;
import com.ucd.common.utils.ResultVOUtil;
import com.ucd.common.utils.Tools;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareDTO;
import com.ucd.server.enums.TdhServicesReturnEnum;
import com.ucd.server.service.operationloginfoservice.OperationLogInfoService;
import com.ucd.server.utils.ForFile;
import com.ucd.server.utils.HttpClientUtils;
import com.ucd.zabbixtestcommon.ZabbixProblemDTO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@CrossOrigin
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private OperationLogInfoService operationLogInfoService;

	/**
	 * 引入日志，注意都是"org.slf4j"包下
	 */
	private final static Logger logger = LoggerFactory.getLogger(TestController.class);


    @GetMapping(value = "/softwareDs/getThdServicesDsInfo")
    public ResultVO getThdServicesDsInfo(){
        ResultVO resultVO = new ResultVO();
        String content1 = "currentpage="+"1"+"&"+"maxresult="+"2"+"&"+/*"cpu="+"1000"+"&"+"vcpu="+"200"+*/"&"+"startdownTimems="+"2018-12-21 16:53:49"+"&"+"startupTimems="+"2018-12-21 16:59:59"+"&"+"tableName="+"2"+"&"+"centre="+"B";
        try {
            String result1 = HttpClientUtils.postString("http://10.66.1.192:28070/softwareDs/getThdServicesDsInfo",content1,
                    "application/x-www-form-urlencoded",null);
            resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(),TdhServicesReturnEnum.SUCCESS.getMessage(),result1);
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = ResultVOUtil.error(e);
        }
        return resultVO;
    }

    @GetMapping(value = "/softwareDs/getTdhDsMonthsInfo")
    public ResultVO getTdhDsMonthsInfo(){
        ResultVO resultVO = new ResultVO();
        String content1 = "currentpage="+"1"+"&"+"maxresult="+"2"+"&"+"startdownTime="+"2018-12"+"&"+"tableName="+"2"+"&"+"centre="+"A"+"&"+"auditStatus="+"0";
        try {
            String result1 = HttpClientUtils.postString("http://10.66.1.192:28070/softwareDs/getTdhDsMonthsInfo",content1,
                    "application/x-www-form-urlencoded",null);
            resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(),TdhServicesReturnEnum.SUCCESS.getMessage(),result1);
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = ResultVOUtil.error(e);
        }
        return resultVO;
    }

    /**
     * 向对端发送要进行数据同步的审核
     *
     * @return
     */
//    @GetMapping(value = "/softwareDs/auditThdDsListData")
//    public ResultVO auditThdDsListData(){
//        ResultVO resultVO = new ResultVO();
//        List<TdhDsMonthsDTO> tdhDsMonthsDTOS = new ArrayList<TdhDsMonthsDTO>();
//        for () {
//            TdhDsMonthsDTO tdhDsMonthsDTO = new TdhDsMonthsDTO();
//            tdhDsMonthsDTO.setTableName("syc_a_");
//            tdhDsMonthsDTO.setStartdownTime("2018-12");
//            tdhDsMonthsDTO.setCentre("A");
//            tdhDsMonthsDTO.setTableNameTotal("syc_a_2018-12");
//        }
//        try {
//            String result = HttpClientUtils.postString("http://10.66.1.192:28070/hardware/saveHardWareInfo",content,"application/json",null);
////			String result = HttpClientUtils.postString("http://10.66.1.192:28070/hardware/saveHardWareInfo",Tools.toJson(hardwareDTO), "application/json",null);
//            resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(),TdhServicesReturnEnum.SUCCESS.getMessage(),result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            resultVO = ResultVOUtil.error(e);
//        }
//        return resultVO;
//    }



	@GetMapping(value = "/hardware/gethardwareInfo")
	public ResultVO gethardwareInfo(){
		ResultVO resultVO = new ResultVO();
		String content1 = "currentpage="+"1"+"&"+"maxresult="+"2"+"&"+/*"cpu="+"1000"+"&"+"vcpu="+"200"+*/"&"+"startTimems="+"2019-01-12 20:51:03"+"&"+"endTimems="+"2019-01-15 20:51:04"+"&"+"Nip="+"ce";
		try {
			String result1 = HttpClientUtils.postString("http://10.66.1.192:28070/hardware/gethardwareInfo",content1,
					"application/x-www-form-urlencoded",null);
			resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(),TdhServicesReturnEnum.SUCCESS.getMessage(),result1);
		} catch (Exception e) {
			e.printStackTrace();
			resultVO = ResultVOUtil.error(e);
		}
		return resultVO;
	}



	@GetMapping(value = "/hardware/saveHardWareInfo")
	public ResultVO saveHardWareInfo(){
		ResultVO resultVO = new ResultVO();
		String content = "{\"cpu\":1000,\"vcpu\":200,\"host\":\"111000\",\"diskstatus\":[1.2,3.5],\"Nip\":\"ceshi2\",\"intime\":\"1547540793427\"}";
		logger.info("content:"+content);
		HardwareDTO hardwareDTO = new HardwareDTO();
		hardwareDTO.setCpu(5000.0);
		hardwareDTO.setHost("12345687");
		hardwareDTO.setVcpu(499.0);
		List<Double> doubleList = new ArrayList<Double>();
		doubleList.add(10.90);
		doubleList.add(67.99);
		hardwareDTO.setDiskstatus(doubleList);
		hardwareDTO.setNip("ceshi");
		Date now = new Date();
		hardwareDTO.setIntime(String.valueOf(now.getTime()));
		logger.info(Tools.toJson(hardwareDTO));
		try {
			String result = HttpClientUtils.postString("http://10.66.1.192:28070/hardware/saveHardWareInfo",content,"application/json",null);
//			String result = HttpClientUtils.postString("http://10.66.1.192:28070/hardware/saveHardWareInfo",Tools.toJson(hardwareDTO), "application/json",null);
			resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(),TdhServicesReturnEnum.SUCCESS.getMessage(),result);
		} catch (Exception e) {
			e.printStackTrace();
			resultVO = ResultVOUtil.error(e);
		}
		return resultVO;
	}

    @GetMapping(value = "/hardware/test1")
    public ResultVO test1(){


	    return null;
    }

    @GetMapping(value = "/test")
    public void test(HttpServletRequest req){
        try {
            DateFormat format2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String testTimeStr = "2017-02-03 08:25:37";
            Date testTime = format2.parse(testTimeStr);
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(now);
            calendar.setTime(testTime);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        System.out.println("year:"+year+",month:"+month+",day:"+day);
        // 设置日期为本月最大日期
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        System.out.println("Datetime:"+calendar.getTime());
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String str=format.format(calendar.getTime());
        System.out.println("str:"+str);
        Date d2= null;
        d2 = format.parse(str);
        System.out.println("d2:"+d2);
        int dayMis=1000*60*60*24;
        System.out.println("一天的毫秒-1:"+dayMis);
            //返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。
            long curMillisecond=d2.getTime();//当天的毫秒
            System.out.println("curMillisecond:"+new Date(curMillisecond));
            long resultMis=curMillisecond+(dayMis-1); //当天最后一秒
            System.out.println("resultMis:"+resultMis);
            //得到我需要的时间    当天最后一秒
            Date resultDate=new Date(resultMis);
            System.out.println("resultDate:"+resultDate);
            System.out.println("FormatResult:"+format2.format(resultDate));
            Date dBegin= format.parse(testTimeStr);
            String dEndString = "2017-02-06 12:25:37";
            Date dEnd = format.parse(dEndString);
            List<Date> lDate = new ArrayList<Date>();
            lDate.add(dBegin);
            Calendar calBegin = Calendar.getInstance();
            // 使用给定的 Date 设置此 Calendar 的时间
            calBegin.setTime(dBegin);
            Calendar calEnd = Calendar.getInstance();
            // 使用给定的 Date 设置此 Calendar 的时间
            calEnd.setTime(dEnd);
            // 测试此日期是否在指定日期之后
            while (dEnd.after(calBegin.getTime())){
                // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
                calBegin.add(Calendar.DAY_OF_MONTH, 4);
                lDate.add(calBegin.getTime());
            }
            for (Date date1 : lDate){
                System.out.println(format2.format(date1));
            }
//            List<String> uuidlists = new ArrayList<String>();
//            for (int i = 0;i < 1000; i++){
//                uuidlists.add(UUID.randomUUID().toString());
//            }
//            while(true){
//                uuidlists.remove((int)Math.random()*100+300);
//                uuidlists.add(UUID.randomUUID().toString());
//                uuidlists.remove((int)Math.random()*100+300);
//                uuidlists.add(UUID.randomUUID().toString());
//            }


//            req.setCharacterEncoding("utf-8");
//            //获取cookie
//            Cookie cookies[] = req.getCookies();
//            if(cookies==null || cookies.length == 0){
//                System.out.println("没有cookie");
//            }else{
//                for (Cookie cookie : cookies){
//
//                    //获取cookie的解释内容
//                    String comment = cookie.getComment();
//                    System.out.println("comment:"+comment);
//                    //获取cookie的键
//                    String key = cookie.getName();
//                    System.out.println("key:"+key);
//
//                    //获取cookie的值
//                    String value = cookie.getValue();
//                    System.out.println("value:"+value);
//
//                    //获取cookie的有效时间。
//                    int time = cookie.getMaxAge();
//                    System.out.println("time:"+time);
//
//                    //获取服务器的IP对应的域名
//                    String domain = cookie.getDomain();
//                    System.out.println("domain:"+ domain);
//
//                    //获取有效路径
//                    String path = cookie.getPath();
//                    System.out.println("path:"+ path);
//
//                }
//            }
////            logger.info("进入testList");
////            List<String> list = new ArrayList<String>();
////            for (int j = 0;j <1000;j++){
////            for(int i = 0;i < 100000;i++){
////                UUID aa = UUID.randomUUID();
////                list.add(aa.toString());
////            }
////            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @GetMapping(value = "/filetestcreate")
    public void filetestcreate(HttpServletRequest req){
        String url = "http://10.28.3.48:14000/webhdfs/v1/tmp/ccc?op=CREATE&data=TRUE&user.name=hdfs";
//        String url = "hdfs://10.28.3.45:8020/tmp/ccc?op=CREATE&data=TRUE&guardian_access_token=CMjaaNFrKDATxzowF1mY-880TDCA.TDH";
        HttpClient client = new HttpClient();
        int status = -1;
        PutMethod method = new PutMethod(url);
        method.setRequestHeader("Content-Type","application/octet-stream");
        try {
            // 设置上传文件
            File targetFile  = new File("D:\\new\\testFile.txt");
           FileInputStream in =new FileInputStream(targetFile);
			method.setRequestBody(in);
			status = client.executeMethod(method);
			System.out.println(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        method.releaseConnection();

    }

    @GetMapping(value = "/filetestdelete")
    public void filetestdelete(HttpServletRequest req){
        String url = "http://10.28.3.48:14000/webhdfs/v1/tmp/ccc?op=DELETE&user.name=hdfs";
//        String url = "hdfs://10.28.3.45:8020/tmp/ccc?op=CREATE&data=TRUE&guardian_access_token=CMjaaNFrKDATxzowF1mY-880TDCA.TDH";
        HttpClient client = new HttpClient();
        int status = -1;
        DeleteMethod method = new DeleteMethod(url);
//        method.setRequestHeader("Content-Type","application/octet-stream");
        try {

            status = client.executeMethod(method);
            System.out.println(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        method.releaseConnection();

    }

    @GetMapping(value = "/test1")
    @Async("transwarpExecutor")
    public void test1(HttpServletRequest req){
        List<String> uuidlists = new ArrayList<String>();
        for (int i = 0;i < 50000; i++){
            uuidlists.add(UUID.randomUUID().toString());
        }
        while(true){
            uuidlists.remove((int)Math.random()*1000+3000);
            uuidlists.add(UUID.randomUUID().toString());
            uuidlists.remove((int)Math.random()*1000+10000);
            uuidlists.add(UUID.randomUUID().toString());
            uuidlists.remove((int)Math.random()*100+15000);
            uuidlists.add(UUID.randomUUID().toString());
            uuidlists.remove((int)Math.random()*1000+5000);
            uuidlists.add(UUID.randomUUID().toString());
            uuidlists.remove((int)Math.random()*1000+20000);
            uuidlists.add(UUID.randomUUID().toString());
            uuidlists.remove((int)Math.random()*100+25000);
            uuidlists.add(UUID.randomUUID().toString());
            uuidlists.remove((int)Math.random()*1000+6000);
            uuidlists.add(UUID.randomUUID().toString());
            uuidlists.remove((int)Math.random()*1000+30000);
            uuidlists.add(UUID.randomUUID().toString());
            uuidlists.remove((int)Math.random()*100+35000);
            uuidlists.add(UUID.randomUUID().toString());
            uuidlists.remove((int)Math.random()*1000+7000);
            uuidlists.add(UUID.randomUUID().toString());
        }
    }


    @GetMapping(value = "/zabbixtestget")
    public String zabbixtest(String a){
        System.out.println("a:"+a);
        return "a:"+a;
    }

    @PostMapping(value = "/zabbixtestpost")
    public String zabbixtestpost(@RequestParam("a") String a){
        System.out.println("a:"+a);
//        a = "Problem#2019.09.18 18:43:20#SQLServer:Access Methods Page Splits / Sec#SQL - 10.166.50.102#Average#130532# ";
        List<String> list= Arrays.asList(a .split("#")).stream().map(s -> (s.trim())).collect(Collectors.toList());
        ZabbixProblemDTO zabbixProblemDTO = new ZabbixProblemDTO();
        zabbixProblemDTO.setStartTime(list.get(1));
        zabbixProblemDTO.setName(list.get(2));
        zabbixProblemDTO.setHost(list.get(3));
        zabbixProblemDTO.setSeverity(list.get(4));
        zabbixProblemDTO.setOriginalproblemid(list.get(5));
        zabbixProblemDTO.setUrl(list.get(6));
        System.out.println(String.join(",", list));
        System.out.println(zabbixProblemDTO);
        return "a:"+a;
    }

    @PostMapping(value = "/zabbixtestpost1")
    public String zabbixtestpost1(@RequestBody ZabbixProblemDTO zabbixProblemDTO){
        System.out.println("zabbixProblemDTO:"+zabbixProblemDTO);
        return "zabbixProblemDTO:"+zabbixProblemDTO;
    }

}

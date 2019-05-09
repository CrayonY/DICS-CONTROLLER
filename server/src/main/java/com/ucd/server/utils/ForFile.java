package com.ucd.server.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.UUID;

/**
 * @author gwm
 * @version 2016年7月8日 上午10:38:49
 */
public class ForFile {
    private final static Logger logger = LoggerFactory.getLogger(ForFile.class);
    //生成文件路径
    private static String path = "D:\\new\\";

    //文件路径+名称
    private static String filenameTemp;
    /**
     * 创建文件
     * @param fileName  文件名称
     * @param filecontent   文件内容
     * @return  是否创建成功，成功则返回true
     */
    public static boolean createFile(String fileName,String filecontent){
        Boolean bool = false;
        filenameTemp = path+fileName+".txt";//文件路径+名称+文件类型
        File file = new File(filenameTemp);
        try {
            //如果文件不存在，则创建新的文件,反之删除源文件，再创建新的文件
            if(!file.exists()){
                file.createNewFile();
                bool = true;
                System.out.println("success create file,the file is "+filenameTemp);
                //创建文件成功后，写入内容到文件里
                writeFileContent(filenameTemp, filecontent);
            }else{
                delFile(fileName);
                file.createNewFile();
                bool = true;
                System.out.println("success create file,the file is "+filenameTemp);
                //创建文件成功后，写入内容到文件里
                writeFileContent(filenameTemp, filecontent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bool;
    }

    /**
     * 向文件中写入内容
     * @param filepath 文件路径与名称
     * @param newstr  写入的内容
     * @return
     * @throws IOException
     */
    public static boolean writeFileContent(String filepath,String newstr) throws IOException{
        Boolean bool = false;
        String filein = newstr+"\r\n";//新写入的行，换行
        String temp  = "";

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        FileOutputStream fos  = null;
        PrintWriter pw = null;
        try {
            File file = new File(filepath);//文件路径(包括文件名称)
            //将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buffer = new StringBuffer();

            //文件原有内容
            for(int i=0;(temp =br.readLine())!=null;i++){
                buffer.append(temp);
                // 行与行之间的分隔符 相当于“\n”
                buffer = buffer.append(System.getProperty("line.separator"));
            }
            buffer.append(filein);

            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buffer.toString().toCharArray());
            pw.flush();
            bool = true;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }finally {
            //不要忘记关闭
            if (pw != null) {
                pw.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
        return bool;
    }

    /**
     * 删除文件
     * @param fileName 文件名称
     * @return
     */
    public static boolean delFile(String fileName){
        Boolean bool = false;
        filenameTemp = path+fileName+".txt";
        File file  = new File(filenameTemp);
        try {
            if(file.exists()){
                file.delete();
                bool = true;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bool;
    }
/**
 * @author gongweimin
 * @Description 将文件上传到星环服务器
 * @date 2019/5/8 17:27
 * @params [fileName]
 * @exception
 * @return boolean
 */
    public static boolean TDHcreate(String fileName){
        Boolean bool = false;
        filenameTemp = path+fileName+".txt";
        String url = "http://10.28.3.48:14000/webhdfs/v1/tmp/"+fileName+"?op=CREATE&data=TRUE&user.name=hdfs";
//        String url = "hdfs://10.28.3.45:8020/tmp/ccc?op=CREATE&data=TRUE&guardian_access_token=CMjaaNFrKDATxzowF1mY-880TDCA.TDH";
        HttpClient client = new HttpClient();
        int status = -1;
        PutMethod method = new PutMethod(url);
        method.setRequestHeader("Content-Type","application/octet-stream");
        try {
            // 设置上传文件
            File targetFile  = new File(filenameTemp);
            FileInputStream in =new FileInputStream(targetFile);
            method.setRequestBody(in);
            status = client.executeMethod(method);
            if (status == 0){
                bool = false;
            }
            logger.info(String.valueOf(status));
        } catch (Exception e) {
            e.printStackTrace();
        }
        method.releaseConnection();
        if (String.valueOf(status).startsWith("20")){
            bool = true;
        }
        return bool;
    }


    public static boolean TDHdelete(String fileName){
        Boolean bool = false;
        filenameTemp = path+fileName+".txt";
        String url = "http://10.28.3.48:14000/webhdfs/v1/tmp/"+fileName+"?op=DELETE&user.name=hdfs";
        HttpClient client = new HttpClient();
        int status = -1;
        DeleteMethod method = new DeleteMethod(url);
        try {
            status = client.executeMethod(method);
            System.out.println(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        method.releaseConnection();
        if (String.valueOf(status).startsWith("20")){
            bool = true;
        }
        return bool;
    }
    public static void main(String[] args) {
        //UUID uuid = UUID.randomUUID();
        createFile("myfile", "我的梦说别停留等待,就让光芒折射泪湿的瞳孔\r\n"+"映出心中最想拥有的彩虹,带我奔向那片有你的天空,因为你是我的梦 我的梦");
    }


















}

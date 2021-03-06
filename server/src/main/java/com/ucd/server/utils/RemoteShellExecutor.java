package com.ucd.server.utils;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @ClassName: RemoteShellExecutor
 * @Description: 执行远程机器上的Shell脚本
 * @Author: gongweimin
 * @CreateDate: 2019/5/9 17:38
 * @Version 1.0
 * @Copyright: Copyright2018-2019 BJCJ Inc. All rights reserved.
 **/
public class RemoteShellExecutor {
    private Connection conn;
    /**
     * 远程机器IP
     */
    private String ip;
    /**
     * 用户名
     */
    private String osUsername;
    /**
     * 密码
     */
    private String password;
    private String charset = Charset.defaultCharset().toString();

    private static final int TIME_OUT = 1000 * 5 * 60;

    /**
     * 构造函数
     *
     * @param ip
     * @param usr
     * @param pasword
     */
    public RemoteShellExecutor(String ip, String usr, String pasword) {
        this.ip = ip;
        this.osUsername = usr;
        this.password = pasword;
    }


    /**
     * 登录
     *
     * @return
     * @throws IOException
     */
    private boolean login() throws IOException {
        conn = new Connection(ip);
        conn.connect();
        return conn.authenticateWithPassword(osUsername, password);
    }

    /**
     * 执行脚本
     *
     * @param cmds
     * @return
     * @throws Exception
     */
    public int exec(String cmds) throws Exception {
        InputStream stdOut = null;
        InputStream stdErr = null;
        String outStr = "";
        String outErr = "";
        int ret = -1;
        try {
            if (login()) {
                // Open a new {@link Session} on this connection
                Session session = conn.openSession();
                // Execute a command on the remote machine.
                session.execCommand(cmds);

                stdOut = new StreamGobbler(session.getStdout());
                outStr = processStream(stdOut, charset);

                stdErr = new StreamGobbler(session.getStderr());
                outErr = processStream(stdErr, charset);

                session.waitForCondition(ChannelCondition.EXIT_STATUS, TIME_OUT);

                System.out.println("outStr=" + outStr);
                System.out.println("outErr=" + outErr);
                ret = session.getExitStatus();
            } else {
                throw new Exception("登录远程机器失败" + ip); // 自定义异常类 实现略
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (stdOut != null) {
                stdOut.close();
            }
            if (stdErr != null) {
                stdErr.close();
            }
//                          IOUtils.closeQuietly(stdOut);
//                          IOUtils.closeQuietly(stdErr);
        }
        return ret;
    }

    /**
     * @param in
     * @param charset
     * @return
     * @throws IOException
     * @throws
     */
    private String processStream(InputStream in, String charset) throws Exception {
        byte[] buf = new byte[1024];
        StringBuilder sb = new StringBuilder();
        while (in.read(buf) != -1) {
            sb.append(new String(buf, charset));
        }
        return sb.toString();
    }

    public static void main(String args[]) throws Exception {
        System.out.println("开始运行");
//                 数据同步脚本
        RemoteShellExecutor executor = new RemoteShellExecutor("10.28.3.48", "root", "bjcj@123456");
        int in = executor.exec("/root/datasync/sync.sh");
        // 执行myTest.sh 参数为java Know dummy
//                 //关闭数据同步
//                 RemoteShellExecutor executor = new RemoteShellExecutor("10.28.3.48", "root", "bjcj@123456");
//                 int in = executor.exec("/root/datasync/yarnkiller.sh");
        System.out.println("继续运行" + in);

        for (int i = 1; i < 20; ) {
            System.out.println("i:" + i);
            Thread.sleep(1000);
        }
    }
}

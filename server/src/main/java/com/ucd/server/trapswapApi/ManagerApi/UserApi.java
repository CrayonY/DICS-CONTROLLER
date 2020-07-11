package com.ucd.server.trapswapApi.ManagerApi;

import com.ucd.server.trapswapApi.connection.Connection;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;


/**
 * Created by JLcan on 2016/6/3.
 */

/*###################################
    用户相关
    ###################################*/
public class UserApi {
    public final static String LOGIN_URL = "/users/login";
    public final static String LOGOUT_URL = "/users/logout";
    public final static String USER_URL = "/users";

    private static final Logger log = LoggerFactory.getLogger(UserApi.class);

    /**
     * 2.1.1 用户登录
     *
     * @param userName 用户名
     * @param password 密码
     * @return 是否登录成功
     */
    public static boolean login(Connection conObject, String userName, String password) throws Exception {
        boolean result;
        JSONObject postContent = new JSONObject();
        postContent.put("userName", userName);
        postContent.put("userPassword", password);
        try (
                //CloseableHttpResponse response = conObject.post(LOGIN_URL, postContent.toString());
                CloseableHttpResponse response = conObject.post1(LOGIN_URL, postContent.toString(), conObject);
        ) {
            result = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
            conObject.printLineAndTitle("login");
            //System.out.println(EntityUtils.toString(response.getEntity()));
        }
        return result;
    }

    /**
     * 2.1.2 用户登出
     *
     * @return 是否登出成功
     */
    public static boolean logout(Connection conObject) throws Exception {
        boolean result;
        try (
                //CloseableHttpResponse response = conObject.get(LOGOUT_URL);
                CloseableHttpResponse response = conObject.get1(LOGOUT_URL, conObject);
        ) {
            result = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
            conObject.printLineAndTitle("logout");
            //System.out.println(EntityUtils.toString(response.getEntity()));
        }
        return result;
    }

    /**
     * 2.1.3 查看用户
     *
     * @return 是否查询成功
     * @throws Exception
     */
    public static boolean getAllUsers(Connection conObject) throws Exception {
        try (
                CloseableHttpResponse response = conObject.get(USER_URL)
        ) {
            String content = EntityUtils.toString(response.getEntity());
            JSONArray userArray = new JSONArray(content);
            conObject.printLineAndTitle("getAllUsers");
            for (int i = 0; i < userArray.length(); i++) {
                JSONObject user = userArray.getJSONObject(i);
                System.out.println("userName: " + user.getString("userName"));
                JSONArray groupArray = user.getJSONArray("groups");
                for (int j = 0; j < groupArray.length(); j++) {
                    JSONObject group = groupArray.getJSONObject(j);
                    System.out.println("groupName: " + group.getString("groupName"));
                }
            }
            boolean result = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
            return result;
        }
    }

    /**
     * 2.1.3.1 查看用户
     *
     * @return 是否查询成功
     * @throws Exception
     */
    public static String getAllUsers1(Connection conObject) throws Exception {
        String responseContent = null;
        try {
            CloseableHttpResponse response = conObject.getNOAbort1(USER_URL, conObject);

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

            log.error("***HTTP请求调用出现异常: " + USER_URL, ex);

            throw ex;
        }
    }

    /**
     * 2.1.4 添加用户
     *
     * @param userName        用户名
     * @param userFullName    用户全名
     * @param userEmail       用户邮箱
     * @param userDepart      用户所属部门
     * @param userDescription 用户描述
     * @param password        密码
     * @param groupNamesList  用户组列表
     * @param roleNamesList   用户角色列表
     * @param userType        用户类型
     * @return 是否创建成功
     * @throws Exception
     */
    public static boolean addUser(Connection conObject, String userName, String userFullName, String userEmail, String userDepart, String userDescription, String password,
                                  List<String> groupNamesList, List<String> roleNamesList, String userType) throws Exception {
        JSONObject user = new JSONObject();
        user.put("userName", userName);
        if (userFullName == null || userFullName == "") {
            user.put("fullName", "");
        } else {
            user.put("fullName", userFullName);
        }
        if (userEmail == null || userEmail == "") {
            user.put("userEmail", "");
        } else {
            user.put("userEmail", userEmail);
        }
        if (userDepart == null || userDepart == "") {
            user.put("userDept", "");
        } else {
            user.put("userDept", userDepart);
        }
        if (userDescription == null || userDescription == "") {
            user.put("userDescription", "");
        } else {
            user.put("userDescription", userDescription);
        }
        user.put("userPassword", password);
        JSONArray groups = new JSONArray();
        if (groupNamesList != null) {
            for (String groupName : groupNamesList) {
                JSONObject group = new JSONObject();
                group.put("groupName", groupName);
                groups.put(group);
            }
        }
        user.put("groups", groups);
        JSONArray roles = new JSONArray();
        if (roleNamesList != null) {
            for (String roleName : roleNamesList) {
                JSONObject role = new JSONObject();
                role.put("roleName", roleName);
                roles.put(role);
            }
        }
        user.put("roles", roles);
        user.put("type", userType);
        try (
                CloseableHttpResponse response = conObject.post(USER_URL, user.toString())
        ) {
            conObject.printLineAndTitle("addUser");
            // System.out.println(USER_URL);
            // System.out.println(user.toString());
            System.out.println(EntityUtils.toString(response.getEntity()));
            boolean result = response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED;
            return result;
        }
    }

    /**
     * 2.1.5 删除用户
     *
     * @param userName 用户名
     * @param type     用户类型
     * @return 是否删除成功
     */
    public static boolean deleteUser(Connection conObject, List<String> userName, List<String> type) throws Exception {
        String path = USER_URL;
        JSONArray content = new JSONArray();
        if (userName == null || type == null) {
            System.out.println("\n Wrong parameter(s)! \n");
        } else {
            for (int i = 0; i < userName.size(); i++) {
                JSONObject userUnit = new JSONObject();
                userUnit.put("userName", userName.get(i));
                userUnit.put("type", type.get(i));
                content.put(userUnit);
            }
        }
        try (
                CloseableHttpResponse response = conObject.delete(path, content.toString())
        ) {
            conObject.printLineAndTitle("deleteUser");
            System.out.println(EntityUtils.toString(response.getEntity()));
            boolean result = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
            return result;
        }
    }

    /**
     * 2.1.6 获取用户信息
     *
     * @param userName 用户名
     * @param type     用户类型
     * @return 用户详细信息
     * @throws Exception
     */
    public static boolean getUserInfo(Connection conObject, String userName, String type) throws Exception {
        String path = USER_URL + "/" + type + "/" + userName;
        try (
                CloseableHttpResponse response = conObject.get(path)
        ) {
            conObject.printLineAndTitle("getUserInfo");
            //System.out.println(EntityUtils.toString(response.getEntity()));
            boolean result = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
            return result;
        }
    }


    /**
     * 2.1.7 修改用户信息
     *
     * @param userName        用户名
     * @param userType        用户类型
     * @param changedFullName 修改后的用户全名
     * @return 是否修改成功
     * @throws Exception
     */
    public static boolean editUser(Connection conObject, String userName, String userType, String changedFullName, String changedEmail, String changedDepart,
                                   String changedDescription, List<String> groupNamesList, List<String> groupNamesAddDeleteList, List<String> roleNamesList,
                                   List<String> roleNamesAddDeleteList) throws Exception {
        boolean result;
        String path = USER_URL + "/" + userType + "/" + userName;

        JSONObject user = new JSONObject();
        user.put("userName", userName);
        if (changedFullName != null && changedFullName != "") {
            user.put("fullName", changedFullName);
        } else {
            user.put("fullName", "");
        }
        if (changedEmail != null && changedEmail != "") {
            user.put("userEmail", changedEmail);
        } else {
            user.put("userEmail", "");
        }
        if (changedDepart != null && changedDepart != "") {
            user.put("userDept", changedDepart);
        } else {
            user.put("userDept", "");
        }
        if (changedDescription != null && changedDescription != "") {
            user.put("userDescription", changedDescription);
        } else {
            user.put("userDescription", "");
        }
        JSONArray groupNamesArray = new JSONArray();
        if (groupNamesList != null) {
            for (int i = 0; i < groupNamesList.size(); i++) {
                JSONObject group = new JSONObject();
                group.put("groupName", groupNamesList.get(i));
                group.put("status", groupNamesAddDeleteList.get(i));
                groupNamesArray.put(group);
            }
        }
        user.put("groups", groupNamesArray);
        JSONArray roleNamesArray = new JSONArray();
        if (roleNamesList != null) {
            for (int i = 0; i < roleNamesList.size(); i++) {
                JSONObject role = new JSONObject();
                role.put("roleName", roleNamesList.get(i));
                role.put("status", roleNamesAddDeleteList.get(i));
                roleNamesArray.put(role);
            }
        }
        user.put("roles", roleNamesArray);
        user.put("type", userType);
        try (
                CloseableHttpResponse response = conObject.put(path, user.toString())
        ) {
            conObject.printLineAndTitle("editUser");
            //System.out.println(EntityUtils.toString(response.getEntity()));
            result = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
            return result;
        }
    }

    /**
     * 2.1.8 管理员设置用户密码
     *
     * @param userName        用户名
     * @param type            用户类型
     * @param changedPassword 修改后的密码
     * @return 是否设置成功
     * @throws Exception
     */
    public static boolean setUserPassword(Connection conObject, String userName, String type, String changedPassword) throws Exception {
        boolean result;
        String path = USER_URL + "/" + type + "/" + userName + "/set_password";

        try (
                CloseableHttpResponse response = conObject.put(path, changedPassword)
        ) {
            conObject.printLineAndTitle("setUserPassword");
            System.out.println(EntityUtils.toString(response.getEntity()));
            result = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
            return result;
        }
    }

    /**
     * 2.1.9 更新用户密码
     *
     * @param userName    用户名
     * @param type        用户类型
     * @param oldPassword 老密码
     * @param newPassword 新密码
     * @return 是否更新成功
     * @throws Exception
     */
    public static boolean updatePassword(Connection conObject, String userName, String type, String oldPassword, String newPassword) throws Exception {
        boolean result;
        String path = USER_URL + "/update_password";

        JSONObject userInfo = new JSONObject();
        userInfo.put("userName", userName);
        userInfo.put("userType", type);
        userInfo.put("currentPassword", oldPassword);
        userInfo.put("newPassword", newPassword);
        try (
                CloseableHttpResponse response = conObject.put(path, userInfo.toString())
        ) {
            conObject.printLineAndTitle("updatePassword");
            System.out.println(EntityUtils.toString(response.getEntity()));
            result = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
            return result;
        }
    }

    /**
     * 2.1.10 锁定用户
     *
     * @param userName 用户名
     * @param type     用户类型
     * @return 是否锁定成功
     * @throws Exception
     */
    public static boolean lockUser(Connection conObject, String userName, String type) throws Exception {
        boolean result;
        String path = USER_URL + "/" + type + "/" + userName + "/lock";
        try (
                CloseableHttpResponse response = conObject.put(path, null)
        ) {
            conObject.printLineAndTitle("lockUser");
            System.out.println(EntityUtils.toString(response.getEntity()));
            result = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
            return result;
        }
    }

    /**
     * 2.1.11 解锁用户
     *
     * @param userName 用户名
     * @param type     用户类型
     * @return 是否解锁成功
     * @throws Exception
     */
    public static boolean unlockUser(Connection conObject, String userName, String type) throws Exception {
        boolean result;
        String path = USER_URL + "/" + type + "/" + userName + "/unlock";
        try (
                CloseableHttpResponse response = conObject.put(path, null)
        ) {
            conObject.printLineAndTitle("unlockUser");
            System.out.println(EntityUtils.toString(response.getEntity()));
            result = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
            return result;
        }
    }

    /**
     * 2.1.12 下载KeyTab
     *
     * @param userName 用户名
     * @param type     用户类型
     * @param fileName KeyTab文件名
     * @return 是否解锁成功
     * @throws Exception
     */
    public static boolean downloadKeyTab(Connection confObject, String userName, String type, String fileName) throws Exception {
        String url = USER_URL + "/" + type + userName + "/keytab/" + fileName;
        File file = new File(fileName);
        try (
                CloseableHttpResponse response = confObject.get(url);
                InputStream in = response.getEntity().getContent();
                FileOutputStream fout = new FileOutputStream(file);
        ) {
            confObject.printLineAndTitle("downloadKeyTab");
            int l = -1;
            byte[] tmp = new byte[1024 * 1024];
            while ((l = in.read(tmp)) != -1) {
                fout.write(tmp, 0, l);
            }
            fout.flush();
            boolean result = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
            return result;
        }
    }


    /**
     * 2.1.13 获取用户首选项
     *
     * @param userName 用户名
     * @param type     用户类型
     * @return 用户首选项信息
     * @throws Exception
     */
    public static boolean getUserPreference(Connection conObject, String userName, String type) throws Exception {
        String path = USER_URL + "/" + type + "/" + userName + "/preferences";

        try (
                CloseableHttpResponse response = conObject.get(path)
        ) {
            conObject.printLineAndTitle("getUserPreference");
            System.out.println(EntityUtils.toString(response.getEntity()));
            boolean result = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
            return result;
        }
    }

    /**
     * 2.1.14 获取用户权限
     *
     * @param userName 用户名
     * @param type     用户类型
     * @return 用户权限信息
     * @throws Exception
     */
    public static boolean getUserPermissions(Connection conObject, String userName, String type) throws Exception {
        String path = USER_URL + "/" + type + "/" + userName + "/permissions";
        try (
                CloseableHttpResponse response = conObject.get(path)
        ) {
            conObject.printLineAndTitle("getUserPermissions");
            System.out.println(EntityUtils.toString(response.getEntity()));
            boolean result = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
            return result;
        }
    }
}

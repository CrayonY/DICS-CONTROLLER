package com.ucd.server.service.userservice.Impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ucd.client.DaoClient;
import com.ucd.common.VO.ResultVO;
import com.ucd.common.enums.ResultExceptEnum;
import com.ucd.common.utils.Tools;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.userDTO.UserDTO;
import com.ucd.daocommon.VO.userVO.UserVO;
import com.ucd.daocommon.VO.userVO.UserVO1;
import com.ucd.server.exception.SoftwareException;
import com.ucd.server.service.userservice.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    DaoClient daoClient;

    @Override
    public ResultVO userValidate(String username, String password) {
        ResultVO resultVO =null;
        resultVO=daoClient.userValidate(username,password);
        return resultVO;
    }

    @Override
    public PageView getUser(PageView pageView, UserDTO userDTO) throws Exception {
        Map<String, Object> models = new HashMap<String, Object>();
        models.put("pageView",pageView);
        models.put("userDTO",userDTO);
        ResultVO resultVO = daoClient.getUser(models);
        logger.info("resultVO=" + resultVO);
        if("000000".equals(resultVO.getCode())){
            Object object = resultVO.getData();
            if (object != null) {
                String pageViewString = Tools.toJson(object);
                Gson gs = new Gson();
                pageView = gs.fromJson(pageViewString, new TypeToken<PageView>() {
                }.getType());
                logger.info("pageViewString:" + pageViewString);
            }
        }else {
            logger.info("异常：e=" + ResultExceptEnum.ERROR_SELECT + "," + resultVO.getMsg()+resultVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_SELECT,resultVO.getMsg()+resultVO.getData());
        }
        return  pageView;
    }

    @Override
    public ResultVO checkUser(String userName) throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName(userName);
        ResultVO resultVO = daoClient.getUserListData(userDTO);
        Map<String,String> result = new HashMap<String, String>();
        if("000000".equals(resultVO.getCode())){
            Object object = resultVO.getData();
            if (object != null) {
                String userVOString = Tools.toJson(object);
                Gson gs = new Gson();
//                List<UserVO> userVOList = gs.fromJson(userVOString, new TypeToken<List<UserVO>>() {
//                }.getType());
//                logger.info("userVOString:" + userVOString);
//                if (null == userVOList || userVOList.size() == 0){
//                    logger.info("异常：e=" + ResultExceptEnum.ERROR_NOFOUND_USER.getCode() + "," + ResultExceptEnum.ERROR_NOFOUND_USER.getMessage());
//                    throw new SoftwareException(ResultExceptEnum.ERROR_NOFOUND_USER);
//                }
//                UserVO userVO = userVOList.get(0);
//                List<Map<String,String>> rolesList = userVO.getRoles();
//                if (null == rolesList || rolesList.size() == 0){
//                    result.put("power","NO");
//                }else{
//                    result.put("power","NO");
//                    for (Map<String,String> roleMap : rolesList){
//                        logger.info("roleMap:"+roleMap);
//                        if ("admin".equals(roleMap.get("roleName"))){
//                                result.put("power","YES");
//                       }
//                    }
//                }
                List<UserVO1> userVO1List = gs.fromJson(userVOString, new TypeToken<List<UserVO1>>() {
                }.getType());
                logger.info("userVOString:" + userVOString);
                if (null == userVO1List || userVO1List.size() == 0){
                    logger.info("异常：e=" + ResultExceptEnum.ERROR_NOFOUND_USER.getCode() + "," + ResultExceptEnum.ERROR_NOFOUND_USER.getMessage());
                    throw new SoftwareException(ResultExceptEnum.ERROR_NOFOUND_USER);
                }
                UserVO1 userVO1 = userVO1List.get(0);
                String rolesList = userVO1.getRoles();
                logger.info("roleName=admin++++:"+rolesList);
                if (null == rolesList || "[]".equals(rolesList)){
                    logger.info("roleName=admin++++:"+rolesList);
                    result.put("power","NO");
                }else{
                    logger.info("roleName=admin++++:"+(rolesList.indexOf("roleName=admin")));
                    result.put("power","NO");
                    if (rolesList.indexOf("roleName=admin") != -1){
                        result.put("power","YES");
                    }
                }
                resultVO.setData(result);
                return resultVO;
            }else {
                logger.info("异常：e=" + ResultExceptEnum.ERROR_NOFOUND_USER.getCode() + "," + ResultExceptEnum.ERROR_NOFOUND_USER.getMessage());
                throw new SoftwareException(ResultExceptEnum.ERROR_NOFOUND_USER);
            }
        }else {
            logger.info("异常：e=" + ResultExceptEnum.ERROR_SELECT + "," + resultVO.getMsg()+resultVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_SELECT,resultVO.getMsg()+resultVO.getData());
        }
    }

    @Override
    public String checkUserPower(String userName) throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName(userName);
        ResultVO resultVO = daoClient.getUserListData(userDTO);
        String result = "NO";
        if("000000".equals(resultVO.getCode())){
            Object object = resultVO.getData();
            if (object != null) {
                String userVOString = Tools.toJson(object);
                Gson gs = new Gson();
//                List<UserVO> userVOList = gs.fromJson(userVOString, new TypeToken<List<UserVO>>() {
//                }.getType());
//                logger.info("userVOString:" + userVOString);
//                if (null == userVOList || userVOList.size() == 0){
//                    logger.info("异常：e=" + ResultExceptEnum.ERROR_NOFOUND_USER.getCode() + "," + ResultExceptEnum.ERROR_NOFOUND_USER.getMessage());
//                    throw new SoftwareException(ResultExceptEnum.ERROR_NOFOUND_USER);
//                }
//                UserVO userVO = userVOList.get(0);
//                List<Map<String,String>> rolesList = userVO.getRoles();
//                if (null == rolesList || rolesList.size() == 0){
//                    result = "NO";
//                }else{
//                    result = "NO";
//                    for (Map<String,String> roleMap : rolesList){
//                        logger.info("roleMap:"+roleMap);
//                        if ("admin".equals(roleMap.get("roleName"))){
//                            result = "YES";
//                        }
//                    }
//                }
                List<UserVO1> userVO1List = gs.fromJson(userVOString, new TypeToken<List<UserVO1>>() {
                }.getType());
                logger.info("userVOString:" + userVOString);
                if (null == userVO1List || userVO1List.size() == 0){
                    logger.info("异常：e=" + ResultExceptEnum.ERROR_NOFOUND_USER.getCode() + "," + ResultExceptEnum.ERROR_NOFOUND_USER.getMessage());
                    throw new SoftwareException(ResultExceptEnum.ERROR_NOFOUND_USER);
                }
                UserVO1 userVO1 = userVO1List.get(0);
                String rolesList = userVO1.getRoles();
                if (null == rolesList || "[]".equals(rolesList)){
                    result = "NO";
                }else{
                    result = "NO";
                    if (rolesList.indexOf("roleName=admin") != -1){
                        result = "YES";
                    }
                }
                return result;
            }else {
                logger.info("异常：e=" + ResultExceptEnum.ERROR_NOFOUND_USER.getCode() + "," + ResultExceptEnum.ERROR_NOFOUND_USER.getMessage());
                throw new SoftwareException(ResultExceptEnum.ERROR_NOFOUND_USER);
            }
        }else {
            logger.info("异常：e=" + ResultExceptEnum.ERROR_SELECT + "," + resultVO.getMsg()+resultVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_SELECT,resultVO.getMsg()+resultVO.getData());
        }
    }
}

package com.ucd.server.service.userservice;

import com.ucd.common.VO.ResultVO;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.userDTO.UserDTO;

public interface UserService {
    ResultVO userValidate(String username, String password);

    PageView getUser(PageView pageView, UserDTO userDTO) throws Exception;

    ResultVO checkUser(String userName) throws Exception;

    String checkUserPower(String userName) throws Exception;
}

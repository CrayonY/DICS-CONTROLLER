package com.ucd.server.service.userservice.Impl;

import com.ucd.client.DaoClient;
import com.ucd.common.VO.ResultVO;
import com.ucd.server.service.userservice.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}

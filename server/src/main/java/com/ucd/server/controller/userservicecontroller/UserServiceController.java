package com.ucd.server.controller.userservicecontroller;

import com.ucd.common.VO.ResultVO;
import com.ucd.server.service.userservice.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserServiceController {
    private static final Logger logger= LoggerFactory.getLogger(UserServiceController.class);
    @Autowired
    UserService userService;
    @RequestMapping(value = "/validate",method = RequestMethod.POST)
    public ResultVO userValidate(@RequestParam(value = "username",required = true) String username,
                                 @RequestParam(value = "password",required = true)String password){

        return  userService.userValidate(username,password);
    }
}
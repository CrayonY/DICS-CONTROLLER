package com.ucd.server.controller.userservicecontroller;

import com.ucd.common.VO.ResultVO;
import com.ucd.common.enums.ResultExceptEnum;
import com.ucd.common.utils.ResultVOUtil;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.userDTO.UserDTO;
import com.ucd.server.enums.TdhServicesReturnEnum;
import com.ucd.server.exception.SoftwareException;
import com.ucd.server.service.userservice.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserServiceController {
    private static final Logger logger= LoggerFactory.getLogger(UserServiceController.class);
    @Autowired
    UserService userService;
//    @RequestMapping(value = "/validate",method = RequestMethod.POST)
//    public ResultVO userValidate(@RequestParam(value = "username",required = true) String username,
//                                 @RequestParam(value = "password",required = true)String password){
//
//        return  userService.userValidate(username,password);
//    }

    /**
     * 获得用户列表
     * @param pageView
     * @param userDTO
     * @return
     */
    @PostMapping(value = "/getUser")
    public ResultVO getUser(PageView pageView, UserDTO userDTO){
        logger.info("pageView:"+pageView);
        logger.info("userDTO:"+userDTO);
        ResultVO resultVO = new ResultVO();
        try {
            if(pageView == null){
                pageView = new PageView();
            }
            pageView = userService.getUser(pageView,userDTO);
            resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(),TdhServicesReturnEnum.SUCCESS.getMessage(),pageView);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = ResultVOUtil.error(e);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        }
    }

    /**
     * 校验用户有无操作权限（角色包含admin拥有操作权限）
     * @param req
     * @return
     */
    @PostMapping(value = "/checkUser")
    public ResultVO checkUser(HttpServletRequest req){
        ResultVO resultVO = new ResultVO();
        String userName = "";
        String accessToken = "";
        try {
            //通过cookie获得用户信息与数据库做校验，不满足直接返回失败，满足进行下一步操作
            req.setCharacterEncoding("utf-8");
            //获取cookie
            Cookie cookies[] = req.getCookies();
            if(cookies==null || cookies.length == 0){
                logger.info("没有cookie");
                throw new SoftwareException(ResultExceptEnum.ERROR_HTTP_COOKIE.getCode(),ResultExceptEnum.ERROR_HTTP_COOKIE.getMessage());
            }else{
                for (Cookie cookie : cookies){

                    //获取cookie的解释内容
                    String comment = cookie.getComment();
                    System.out.println("comment:"+comment);
                    //获取cookie的键
                    String key = cookie.getName();
                    System.out.println("key:"+key);
                    if ("userName".equals(key)) {

                        //获取cookie的值
                        String value = cookie.getValue();
                        System.out.println("userNameValue:" + value);
                        userName = value;
                    }
                    if ("accessToken".equals(key)) {

                        //获取cookie的值
                        String value = cookie.getValue();
                        System.out.println("accessTokenValue:" + value);
                        accessToken = value;
                    }

                    //获取cookie的有效时间。
                    int time = cookie.getMaxAge();
                    System.out.println("time:"+time);

                    //获取服务器的IP对应的域名
                    String domain = cookie.getDomain();
                    System.out.println("domain:"+ domain);

                    //获取有效路径
                    String path = cookie.getPath();
                    System.out.println("path:"+ path);

                }
                if("".equals(accessToken)){
                    logger.info("token为空");
                    throw new SoftwareException(ResultExceptEnum.ERROR_HTTP_TOKEN.getCode(),ResultExceptEnum.ERROR_HTTP_TOKEN.getMessage());
                }
                if("".equals(userName)){
                    logger.info("userName为空");
                    throw new SoftwareException(ResultExceptEnum.ERROR_HTTP_USER.getCode(),ResultExceptEnum.ERROR_HTTP_USER.getMessage());
                }
            }
            resultVO = userService.checkUser(userName);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = ResultVOUtil.error(e);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        }
    }


}
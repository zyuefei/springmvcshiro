package com.demo.control;

import com.demo.bean.User;
import com.demo.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;


/**
 * create by ff on 2018/6/23
 */
@Controller
public class UserControl {

    @Resource
    private UserService userService;

    @RequestMapping(value = "/subLogin",method = RequestMethod.POST)
    @ResponseBody
    public Object subLogin(User user){
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(),user.getPassword());
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "登录成功";
    }

    @RequestMapping(value = "/regist",method = RequestMethod.POST)
    @ResponseBody
    public Object regist(User user){
        try {
            userService.createUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "成功";
    }
}

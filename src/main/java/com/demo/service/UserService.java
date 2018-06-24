package com.demo.service;

import com.demo.bean.User;
import com.demo.common.util.PasswordUtil;
import com.demo.dao.UserDAO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * create by ff on 2018/6/23
 */
@Service
public class UserService {

    @Resource
    private UserDAO userDAO;

    public Set<String> getRoles(String userName){

        return userDAO.findRoles(userName);
    }

    public Set<String> getPremissions(String userName){
        return userDAO.findPermissions(userName);
    }

    public User findByUsername(String userName) {
        return userDAO.findUser(userName);
    }

    /**
     * 新建用户
     * @param user
     * @return
     */
    public User createUser(User user) {
        PasswordUtil.encryptPassword(user);//生成数据库密码
        user.setLocked("1");
        boolean result = userDAO.createUser(user);
        System.out.println("create:"+result);
        return user;
    }
}

package com.demo.dao;

import com.demo.bean.User;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * create by ff on 2017/12/4
 */
public interface UserDAO {

    public Set<String> findRoles(@Param("username") String username);

    public Set<String> findPermissions(@Param("username") String username);

    public User findUser(@Param("username") String username);

    public boolean createUser(@Param("user") User user);

}

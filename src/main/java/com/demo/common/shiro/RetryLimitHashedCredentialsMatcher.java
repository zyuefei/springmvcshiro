package com.demo.common.shiro;

import com.demo.common.util.JedisUtil;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.springframework.util.SerializationUtils;
import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 限制重复登录次数
 * 证书匹配（即判断是否可以登录）
 */
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {


    @Resource
    private JedisUtil jedisUtil;

    private final String retry_limit_prefix="zyuefei-retry_limit:";

    //单位s，默认一个小时
    private int expires=3600;

    public void setExpires(int expires) {
        this.expires = expires;
    }

    private byte[] getkey(String key){
        return (retry_limit_prefix+key).getBytes();
    }



    /**
     * atomicInteger 线程安全，
     * getAndAddInt()去当前值，并且自加
     * @param token
     * @param info
     * @return
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String username = (String)token.getPrincipal();
        byte[] key = getkey(username);
        //retry count + 1
        AtomicInteger retryCount = (AtomicInteger) SerializationUtils.deserialize(jedisUtil.get(key));
        if(retryCount == null) {
            retryCount = new AtomicInteger(0);
            byte[] value = SerializationUtils.serialize(retryCount);
            jedisUtil.set(key,value);
            jedisUtil.expire(key,expires);
        }
        if(retryCount.incrementAndGet() > 5) {
            //if retry count > 5 throw
            throw new ExcessiveAttemptsException();//多次重复登录失败
        }

        boolean matches = super.doCredentialsMatch(token, info);//验证密码
        if(matches) {
            //clear retry count
            jedisUtil.del(key);//移除缓存
        }
        return matches;
    }
}

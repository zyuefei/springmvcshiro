package com.demo.common.shiro;

import com.demo.common.util.JedisUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Set;

/**
 * create by ff on 2018/6/23
 */
@Component
public class RedisCache<K,V> implements Cache<K, V> {
    private final String cache_prefix="zyuefei_caceh:";

    //默认超时时间为10分钟
    private final int expire = 600;

    @Resource
    private JedisUtil jedisUtil;

    private byte[] getkey(K k){
        if (k instanceof  String){
            return (cache_prefix+k).getBytes();
        }
        return SerializationUtils.serialize(k);
    }


    @Override
    public V get(K k) throws CacheException {
        System.out.println("从redis获取权限数据");
        byte[] value = jedisUtil.get(getkey(k));
        if (value != null){
            return (V)SerializationUtils.deserialize(value);
        }
        return null;
    }

    @Override
    public V put(K k, V v) throws CacheException {
        byte[] key = getkey(k);
        byte[] value = SerializationUtils.serialize(v);
        jedisUtil.set(key,value);
        jedisUtil.expire(key,expire);
        return null;
    }

    @Override
    public V remove(K k) throws CacheException {
        byte[] key = getkey(k);
        byte[] value = jedisUtil.get(key);
        jedisUtil.del(key);
        if (value!=null){
            return (V) SerializationUtils.deserialize(value);
        }
        return null;
    }

    @Override
    public void clear() throws CacheException {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<K> keys() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }
}

package com.demo.common.session;

import com.demo.common.util.JedisUtil;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.util.CollectionUtils;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * create by ff on 2018/6/23
 */
public class RedisSessionDao extends AbstractSessionDAO {

    @Resource
    private JedisUtil jedisUtil;

    private final String shiro_session_prefix="zyuefei-session:";

    private  byte[] getkey(String key){
        return (shiro_session_prefix+key).getBytes();
    }

    private void saveSession(Session session){
        if (session!=null && session.getId()!=null){
            byte[] key = getkey(session.getId().toString());
            byte[] value = SerializationUtils.serialize(session);
            jedisUtil.set(key,value);
            jedisUtil.expire(key,600);
        }
    }

    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
       saveSession(session);
//       sessionid 和session进行捆绑
       assignSessionId(session,sessionId);
        return sessionId;
    }

    protected Session doReadSession(Serializable sessionId) {
        if (sessionId==null) return null;
        byte[] key = getkey(sessionId.toString());
        byte[] value = jedisUtil.get(key);
        return (Session)SerializationUtils.deserialize(value);
    }

    public void update(Session session) throws UnknownSessionException {
        saveSession(session);
    }

    public void delete(Session session) {
        if (session==null || session.getId()==null) return;
        byte[] key = getkey(session.getId().toString());
        jedisUtil.del(key);
    }

    public Collection<Session> getActiveSessions() {
        Set<byte[]> keys = jedisUtil.keys(shiro_session_prefix);
        Set<Session> sessions = new HashSet<Session>();
        if (CollectionUtils.isEmpty(keys)){
            return sessions;
        }
        for (byte[] key:keys){
            Session session = (Session) SerializationUtils.deserialize(jedisUtil.get(key));
            sessions.add(session);
        }
        return sessions;
    }
}

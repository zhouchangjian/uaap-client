package com.cj.uaap.session;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UaapSessionDAO extends CachingSessionDAO {
	Logger loger = LoggerFactory.getLogger(UaapSessionDAO.class);
	static Map<Serializable,Object> map = new HashMap<Serializable,Object>();
    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        map.put(sessionId, session);
        loger.info("sessionDao创建session，sessionID为："+session.getId());
        return session.getId();
    }
    @Override
    protected void doUpdate(Session session) {
        if(session instanceof ValidatingSession && !((ValidatingSession)session).isValid()) {
            return; //如果会话过期/停止 没必要再更新了
        }
        loger.info("sessionDao修改session，sessionID为："+session.getId());
        map.put(session.getId(), session);
    }
    @Override
    protected void doDelete(Session session) {
    	loger.info("sessionDao删除session，sessionID为："+session.getId());
    	map.remove(session.getId());
    }
    @Override
    protected Session doReadSession(Serializable sessionId) {
    	loger.info("sessionDao读取session，sessionID为："+sessionId);
        return (Session)map.get(sessionId);
    }
}

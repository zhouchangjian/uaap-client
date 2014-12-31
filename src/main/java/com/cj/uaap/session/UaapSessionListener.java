package com.cj.uaap.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class UaapSessionListener extends SessionListenerAdapter{
	Logger loger = LoggerFactory.getLogger(UaapSessionListener.class);
	@Override
    public void onStart(Session session) {//会话创建时触发
		loger.info("会话创建：" + session.getId());
		if(session instanceof UaapSession){
			UaapSession uaapSession = (UaapSession) session;
			loger.info("会话信息：" + uaapSession.getHost()+";"+uaapSession.getSystemHost()+";"+uaapSession.getUserAgent()+";"+uaapSession.getStartTimestamp());
		}
    }
    @Override
    public void onExpiration(Session session) {//会话过期时触发
    	loger.info("会话过期：" + session.getId());
    	if(session instanceof UaapSession){
			UaapSession uaapSession = (UaapSession) session;
			loger.info("会话信息：" + uaapSession.getHost()+";"+uaapSession.getSystemHost()+";"+uaapSession.getUserAgent()+";"+uaapSession.getStartTimestamp());
		}
    }
    @Override
    public void onStop(Session session) {//退出/会话过期时触发
    	loger.info("会话停止：" + session.getId());
    	if(session instanceof UaapSession){
			UaapSession uaapSession = (UaapSession) session;
			loger.info("会话信息：" + uaapSession.getHost()+";"+uaapSession.getSystemHost()+";"+uaapSession.getUserAgent()+";"+uaapSession.getStartTimestamp());
		}
    }
}

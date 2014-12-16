package com.cj.uaap.oauth2;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
public class OAuth2AuthenticationFilter extends AuthenticatingFilter {

	Logger loger = LoggerFactory.getLogger(OAuth2AuthenticationFilter.class);
    //oauth2 authc code参数名
    private String authcCodeParam = "code";
    //客户端id
    private String clientId;
    //服务器端登录成功/失败后重定向到的客户端地址
    private String redirectUrl;
    //oauth2服务器响应类型
    private String responseType = "code";

    private String failureUrl;

    public void setAuthcCodeParam(String authcCodeParam) {
        this.authcCodeParam = authcCodeParam;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public void setFailureUrl(String failureUrl) {
        this.failureUrl = failureUrl;
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
    	loger.info("createToken：开始");
    	HttpServletRequest httpRequest = (HttpServletRequest) request;
        String code = httpRequest.getParameter(authcCodeParam);
        loger.info("createToken：code="+code);
        return new OAuth2Token(code);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
    	loger.info("isAccessAllowed：开始");
    	return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
    	loger.info("onAccessDenied：开始");
        String error = request.getParameter("error");
        loger.info("onAccessDenied：error="+error);
        String errorDescription = request.getParameter("error_description");
        loger.info("onAccessDenied：errorDescription="+errorDescription);
        if(!StringUtils.isEmpty(error)) {//如果服务端返回了错误
        	loger.info("onAccessDenied：error="+error);
            WebUtils.issueRedirect(request, response, failureUrl + "?error=" + error + "error_description=" + errorDescription);
            return false;
        }

        Subject subject = getSubject(request, response);
        if(StringUtils.isEmpty(request.getParameter(authcCodeParam))){
        	loger.info("onAccessDenied：authcCodeParam="+request.getParameter(authcCodeParam));
        	 if(subject.isAuthenticated()){
        		 loger.info("onAccessDenied：isAuthenticated="+subject.isAuthenticated());
        		 return true;
        	 }
        	 saveRequestAndRedirectToLogin(request, response);
             return false;
        }else{
        	 loger.info("onAccessDenied：executeLogin开始");
        	 return executeLogin(request, response);
        }

    }

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
                                     ServletResponse response) throws Exception {
    	loger.info("onLoginSuccess：issueSuccessRedirect：开始");
        issueSuccessRedirect(request, response);
        loger.info("onLoginSuccess：issueSuccessRedirect：结束");
        return false;
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException ae, ServletRequest request,
                                     ServletResponse response) {
    	loger.info("onLoginFailure：开始");
        Subject subject = getSubject(request, response);
        if (subject.isAuthenticated() || subject.isRemembered()) {
            try {
                issueSuccessRedirect(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                WebUtils.issueRedirect(request, response, failureUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}

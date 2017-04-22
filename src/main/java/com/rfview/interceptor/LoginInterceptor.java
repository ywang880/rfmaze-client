package com.rfview.interceptor;

import java.util.Map;
import java.util.logging.Logger;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class LoginInterceptor implements Interceptor {
    private static final long serialVersionUID = 1L;
    private Logger logger = Logger.getLogger(LoginInterceptor.class.getName());

    @Override
    public void destroy() {
        logger.info("destroy ... ");
    }

    @Override
    public void init() {
        logger.info("init ... ");
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        String retCode;
        Map<String, Object> sessionAttributes = invocation.getInvocationContext().getSession();
        if (sessionAttributes == null) {
            return invocation.invoke();
        }

        if (sessionAttributes.get("loginId") == null) {
            retCode = invocation.invoke();
            return retCode;
        }
            
        if (sessionAttributes == null || sessionAttributes.get("loginId") == null) {
            retCode = invocation.invoke();
            return retCode;
        } else {
            if (!((String) sessionAttributes.get("loginId")).equals(null)) {
                retCode = invocation.invoke();
                return retCode;
            } else {
                retCode = invocation.invoke();
                return retCode;
            }
        }
    }
}

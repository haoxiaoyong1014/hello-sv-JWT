package com.liumapp.helloSv.backend.web.interceptors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liumapp.helloSv.backend.web.model.Permission;
import com.liumapp.helloSv.backend.web.utils.CacheUtil;
import org.apache.log4j.Logger;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by liumapp on 10/19/17.
 * E-mail:liumapp.com@gmail.com
 * home-page:http://www.liumapp.com
 */
public class GeneralInterceptor extends HandlerInterceptorAdapter {
    private static Logger logger = Logger.getLogger(GeneralInterceptor.class);
    private AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI(), userCookie;
        boolean isSuccess = false;
        if ((userCookie = parseUserCookie(request.getHeader("cookie"))) != null) {
            JSONObject object = JSON.parseObject(userCookie);
            Object cache = CacheUtil.getCache(object.getString("name") + object.getString("timestamp"));
            if (cache != null) {
                List<Permission> permissionList = (List<Permission>) cache;
                for (Permission permission : permissionList) {
                    if (matcher.match(permission.getUrl(), uri)) {
                        isSuccess = true;
                    }
                }
            }
        }
        logger.info(String.format("uri:%s,%s", uri, isSuccess));
        return isSuccess;
    }

    private String parseUserCookie(String cookie) {
        if (!StringUtils.isEmpty(cookie)) {
            String[] s = cookie.split(";");
            for (String ss : s) {
                String[] sss = ss.split("=");
                if ("user".equals(sss[0].trim())) {
                    try {
                        return URLDecoder.decode(sss[1], "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        logger.error(e);
                    }
                }
            }
        }
        return null;
    }
}

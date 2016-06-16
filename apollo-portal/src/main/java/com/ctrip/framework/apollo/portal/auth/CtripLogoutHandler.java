package com.ctrip.framework.apollo.portal.auth;

import com.ctrip.framework.apollo.portal.repository.ServerConfigRepository;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CtripLogoutHandler implements LogoutHandler{

  @Autowired
  private ServerConfigRepository serverConfigRepository;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    //将session销毁
    request.getSession().invalidate();

    Cookie cookie = new Cookie("memCacheAssertionID", null);
    //将cookie的有效期设置为0，命令浏览器删除该cookie
    cookie.setMaxAge(0);
    cookie.setPath(request.getContextPath() + "/");
    response.addCookie(cookie);

    //重定向到SSO的logout地址
    String casServerUrl = serverConfigRepository.findByKey("casServerUrlPrefix").getValue();
    String serverName = serverConfigRepository.findByKey("serverName").getValue();

    try {
      response.sendRedirect(casServerUrl + "/logout?service=" + serverName);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
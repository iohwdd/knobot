package com.iohw.knobot.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author: iohw
 * @date: 2025/5/20 17:15
 * @description:
 */
public class CookieUtils {
    /**
     * 添加cookie
     *
     * @param response
     * @param name
     * @param value
     * @param maxAge
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * cookie 根据key找value
     *
     * @param name
     * @param request
     * @return
     */
    public static String findValueByName(String name, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 根据key删除cookie
     *
     * @param request
     * @param response
     * @param name
     */
    public static void deleteCookieByName(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) {
            return;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                cookie.setMaxAge(0);
                cookie.setPath("/");
                cookie.setValue(null);
                response.addCookie(cookie);
                return;
            }
        }
    }
}

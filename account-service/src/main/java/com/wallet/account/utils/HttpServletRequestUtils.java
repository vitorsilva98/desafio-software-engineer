package com.wallet.account.utils;

import java.util.UUID;

import com.wallet.account.constants.TokenClaimsConstants;

import jakarta.servlet.http.HttpServletRequest;

public class HttpServletRequestUtils {
    
    private HttpServletRequestUtils() {
        throw new IllegalStateException("Cannot instantiate HttpServletRequestUtils class");
    }

    public static UUID getUserIdFromRequest(HttpServletRequest httpServletRequest) {
        return UUID.fromString(httpServletRequest.getAttribute(TokenClaimsConstants.USER_ID_CLAIM).toString());
    }

    public static String getUserEmailFromRequest(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getAttribute(TokenClaimsConstants.USER_EMAIL_CLAIM).toString();
    }
}

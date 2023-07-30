package com.wallet.account.constants;

public class TokenClaimsConstants {

    private TokenClaimsConstants() {
        throw new IllegalStateException("Cannot instantiate TokenClaimsConstants class");
    }

    public static final String USER_ID_CLAIM = "userId";
    public static final String USER_EMAIL_CLAIM = "subject";
}

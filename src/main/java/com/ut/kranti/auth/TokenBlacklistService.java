package com.ut.kranti.auth;

/**
 * TokenBlacklistService deprecated: blacklist removed. This stub remains
 * to avoid compile errors in case any leftover references exist; it is a no-op.
 */
@Deprecated
public class TokenBlacklistService {
    public void blacklistToken(String token, Long expiryEpochSeconds) {
        // no-op
    }
    public boolean isBlacklisted(String token) {
        return false;
    }
}
package com.ut.kranti.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

// logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JWTService {

    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);

    private String secretkey = "fallback-secret";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Base64.Encoder urlEncoder = Base64.getUrlEncoder().withoutPadding();
    private final Base64.Decoder urlDecoder = Base64.getUrlDecoder();

    public JWTService() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            secretkey = Base64.getEncoder().encodeToString(sk.getEncoded());
            logger.info("Generated random JWT secret key");
        } catch (NoSuchAlgorithmException e) {
            // fallback: use default secret
            secretkey = Base64.getEncoder().encodeToString("default-secret-key-which-is-not-very-secure".getBytes(StandardCharsets.UTF_8));
            logger.warn("Failed to generate secure key. Using fallback secret key.");
        }
    }

    private byte[] getSecretBytes() {
        return Base64.getDecoder().decode(secretkey);
    }

    public String generateToken(String username) {
        try {
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            Map<String, Object> payload = new HashMap<>();
            long now = Instant.now().getEpochSecond();
            payload.put("sub", username);
            payload.put("iat", now);
            payload.put("exp", now + 3600); // 1 hour expiry

            String headerJson = objectMapper.writeValueAsString(header);
            String payloadJson = objectMapper.writeValueAsString(payload);

            String headerB64 = urlEncoder.encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
            String payloadB64 = urlEncoder.encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));

            String signingInput = headerB64 + "." + payloadB64;
            String signature = sign(signingInput);

            String token = signingInput + "." + signature;
            logger.debug("Generated JWT for username={}", username);
            return token;
        } catch (Exception e) {
            logger.error("Failed to generate token for username={}: {}", username, e.toString(), e);
            throw new RuntimeException("Failed to generate token", e);
        }
    }

    private String sign(String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(getSecretBytes(), "HmacSHA256");
        mac.init(keySpec);
        byte[] sig = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return urlEncoder.encodeToString(sig);
    }

    private Map<String, Object> decodePayload(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;
            String payloadJson = new String(urlDecoder.decode(parts[1]), StandardCharsets.UTF_8);
            return objectMapper.readValue(payloadJson, Map.class);
        } catch (Exception e) {
            logger.warn("Failed to decode JWT payload: {}", e.getMessage());
            return null;
        }
    }

    public String extractUserName(String token) {
        Map<String, Object> payload = decodePayload(token);
        if (payload == null) return null;
        Object sub = payload.get("sub");
        return sub != null ? sub.toString() : null;
    }

    public Long extractExpirationEpoch(String token) {
        Map<String, Object> payload = decodePayload(token);
        if (payload == null) return null;
        Object expObj = payload.get("exp");
        if (expObj == null) return null;
        try {
            return Long.parseLong(String.valueOf(expObj));
        } catch (Exception e) {
            logger.warn("Failed to parse exp from token payload: {}", expObj);
            return null;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;
            String signingInput = parts[0] + "." + parts[1];
            String expectedSig = sign(signingInput);
            if (!expectedSig.equals(parts[2])) {
                logger.warn("JWT signature mismatch for user={}", userDetails.getUsername());
                return false;
            }

            Map<String, Object> payload = decodePayload(token);
            if (payload == null) return false;
            Object expObj = payload.get("exp");
            long exp = Long.parseLong(String.valueOf(expObj));
            long now = Instant.now().getEpochSecond();
            String username = (String) payload.get("sub");
            boolean ok = (username != null && username.equals(userDetails.getUsername()) && exp > now);
            logger.debug("Validated token for username={} valid={}", username, ok);
            return ok;
        } catch (Exception e) {
            logger.warn("JWT validation error: {}", e.getMessage());
            return false;
        }
    }
}
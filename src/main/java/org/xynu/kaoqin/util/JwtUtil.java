package org.xynu.kaoqin.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtil {

    // 长度必须 >= 32 字节
    private static final String SECRET_KEY = "kaoqin-2024-secret-very-very-long-key-123456";

    // Token 有效期：7天（单位毫秒）
    private static final long EXPIRE = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 生成 JWT Token
     *
     * @param id       用户ID
     * @param username 用户名
     * @return String 生成的 Token
     */
    public static String createToken(Integer id, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims) // 设置自定义载荷
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE)) // 设置过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes(StandardCharsets.UTF_8)) // 使用安全密钥签名
                .compact(); // 生成字符串形式的 Token
    }

    /**
     * 解析 Token 获取其中的 Claims（用户信息）
     *
     * @param token 待解析的 Token
     * @return Claims 包含用户信息的对象
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8)) // 使用相同的密钥进行验证
                .parseClaimsJws(token) // 解析并验证签名
                .getBody(); // 获取负载部分
    }

    public static Integer getUserId(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
            .parseClaimsJws(token)
            .getBody();
        Object idObj = claims.get("id");
        if (idObj instanceof Integer) {
            return (Integer) idObj;
        } else if (idObj instanceof Number) {
            return ((Number) idObj).intValue();
        } else if (idObj != null) {
            return Integer.parseInt(idObj.toString());
        }
        return null;
    }
}

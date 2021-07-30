package com.example.server.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.w3c.dom.UserDataHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : whz
 */
@Component
public class JwtTokenUtil {

	private static final String CLAIM_KEY_USERNAME = "sub";
	private static final String CLAM_KEY_CREATED = "created";
	@Value("${jwt.expiration}")
	private Long expiration;
	@Value("${jwt.secret}")
	private String secret;

	/**
	 * 根据用户信息生成token
	 */
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
		claims.put(CLAM_KEY_CREATED, new Date());
		return generateToken(claims);
	}

	/**
	 * 根据载荷生成token
	 */
	public String generateToken(Map<String, Object> claims) {
		return Jwts.builder()
				.setClaims(claims)
				.setExpiration(generateExpirationDate())
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	}

	/**
	 * 生成token失效时间
	 */
	private Date generateExpirationDate() {
		return new Date(System.currentTimeMillis() + expiration * 1000);
	}

	/**
	 * 从token中获取负载
	 */
	public Claims getClaimsFromToken(String token) {
		Claims claims = null;
		try {
			claims = Jwts.parser()
					.setSigningKey(secret)
					.parseClaimsJws(token)
					.getBody();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return claims;
	}

	/**
	 * 从token中获取登陆用户名
	 */
	public String getUserNameFromToken(String token) {
		String username = null;
		try {
			Claims claims = getClaimsFromToken(token);
			username = claims.getSubject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return username;
	}

	/**
	 * 从token中获取过期时间
	 */
	public Date getExpireDateFromToken(String token) {
		Claims claims = getClaimsFromToken(token);
		return claims.getExpiration();
	}

	/**
	 * 验证token是否有效
	 */
	public boolean validateToken(String token, UserDetails userDetails) {
		String username = getUserNameFromToken(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpiration(token);
	}

	/**
	 * 验证token是否过期
	 */
	public boolean isTokenExpiration(String token) {
		Date expireDate = getExpireDateFromToken(token);
		return expireDate.before(new Date());
	}

}

package com.generation.blogpessoal.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

 //objetivo da classe criar e confirmar o token
 
@Component
public class JwtService {
	
	//site para acessarmos a secret https://www.keygen.io/ opção sha 256
	//constante que gera uma chave para encodar as informações do token
	public static final String SECRET = "a4ba1c90acd1e54e8b500bd32f2432ae88afad7de3df44d162eaa69226c70c9f";
	
	//assinatura token
	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	//método que tem como objeto retornar todas as claims inseridas
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSignKey()).build()
				.parseClaimsJws(token).getBody();
	}
	    
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
		
	//recuperar os dados da parte sub do claim onde encontramos o email(usuario)
	
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	//data que o token expira
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	//valida se a data que o token expira esta dentro da validade
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	
	//validar se o usuario que foi extraido o token condiz com o usuario que a userdetails tem e se está dentro da dara de validade
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	
	//objetivo de calcular o tempo de validade do token, formar o claim com as informações do token
		
	 	private String createToken(Map<String, Object> claims, String userName) {
		return Jwts.builder()
					.setClaims(claims)
					.setSubject(userName)
					.setIssuedAt(new Date(System.currentTimeMillis()))
					.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
					.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}
	
	//gerar o token puxando os claims formados no metodo anterior
	public String generateToken(String userName) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userName);
	}
		
	
}

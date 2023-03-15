package com.example.securityexample.service.concretes;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${security.jwt.secret}")
    private String SECRET_KEY;
    public String findUsername(String token) {
        //gelen tokenımızım içinden claims diyerek subject bölümünden username çekmemize yarayan fonksiyon
        return exportToken(token, Claims::getSubject);
    }

    private <T> T exportToken(String token, Function<Claims,T> claimsTFunction) {
        //claimsin içinde yer alan
        final Claims claims= Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build().parseClaimsJws(token).getBody();
        return claimsTFunction.apply(claims);
    }

    //secret key oluşturma.
    private Key getKey(){
        byte[] key= Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(key);
    }

    public boolean tokenKontrol(String jwt, UserDetails userDetails) {
        //gelen tokendan usernami çekiyoruz
        final String userName=findUsername(jwt);
        //usernameler aynı mı ve tokenın zamanı dolmuş mu?
        return (userName.equals(userDetails.getUsername())&& !exportToken(jwt,Claims::getExpiration).before(new Date()));
    }

    public String generateToke(UserDetails user) {

        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*24))

               .signWith(getKey(),SignatureAlgorithm.HS256)
                .compact();
    }
}

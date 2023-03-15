package com.example.securityexample.configuration;

import com.example.securityexample.service.concretes.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
@Component
@RequiredArgsConstructor
@Configuration
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException,
            IOException {
                //gelen requestin içerisinden headeri aldık
                final String header=request.getHeader("Authorization");
                final String jwt;
                final String username;
                //alınan header null mı ya da headırın başlığı beraerla başlıyor mu?
                if(header==null || !header.startsWith("Bearer ")){
                    filterChain.doFilter(request,response);
                    return;
                }
                //Bearer dan sonra tokenımız devam ettiği için tokenı aldık
                jwt=header.substring(7);
                //token içinden username çekmek için kullanacağımız fonksiyon.
                username=jwtService.findUsername(jwt);
                //mevcut bir oturum olup olmadığı kontrol edilir.
                if(username!=null&& SecurityContextHolder.getContext().getAuthentication()==null){
                    UserDetails userDetails=userDetailsService.loadUserByUsername(username);

                    // token içindeki user nesnesi ile userDetail nesnesi birbirine uyuyyor mu?
                    if(jwtService.tokenKontrol(jwt,userDetails)){
                        //bu nesneye userDetail parametresi alarak verilen kulanıc bilgilerini alıcak ve getAuthorities metodu ile
                        //kullanıcının yetkilerini bu neseneye verilecek.
                        UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
                        //user detail içindeki bilgiler güncellendiyse nesneye güncel bilgileri ekler.
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
                filterChain.doFilter(request,response);

    }
}

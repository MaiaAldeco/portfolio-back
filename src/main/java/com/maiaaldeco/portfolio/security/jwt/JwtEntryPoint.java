package com.maiaaldeco.portfolio.security.jwt;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
//COMPRUEBA SI HAY UN TOKEN VÁLIDO
@Component
public class JwtEntryPoint implements AuthenticationEntryPoint{

    //PRUEBA EN CASO DE ERROR
    private final static Logger logger = LoggerFactory.getLogger(JwtEntryPoint.class);
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        logger.error("Fail en método commence" + authException.getMessage());
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nombre de usuario o contraseña incorrecta");
    }  
}

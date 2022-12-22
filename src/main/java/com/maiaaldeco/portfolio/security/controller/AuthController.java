package com.maiaaldeco.portfolio.security.controller;

import com.maiaaldeco.portfolio.dto.Mensaje;
import com.maiaaldeco.portfolio.security.dto.JwtDto;
import com.maiaaldeco.portfolio.security.dto.LoginUsuario;
import com.maiaaldeco.portfolio.security.dto.NuevoUsuario;
import com.maiaaldeco.portfolio.security.entity.Rol;
import com.maiaaldeco.portfolio.security.entity.Usuario;
import com.maiaaldeco.portfolio.security.enums.RolNombre;
import com.maiaaldeco.portfolio.security.jwt.JwtProvider;
import com.maiaaldeco.portfolio.security.service.RolService;
import com.maiaaldeco.portfolio.security.service.UsuarioService;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    RolService rolService;

    @Autowired
    JwtProvider jwtProvider;

    @PostMapping("/create")
    public ResponseEntity<?> nuevo(@Valid @RequestBody NuevoUsuario nuevoUsuario, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                return new ResponseEntity(new Mensaje(error.getDefaultMessage()), HttpStatus.BAD_REQUEST);
            }
        }
        if(rolService.lista().isEmpty()){
            Rol rolAdmin = new Rol(RolNombre.ROLE_ADMIN);
            Rol rolUser = new Rol(RolNombre.ROLE_USER);

            rolService.save(rolUser);
            rolService.save(rolAdmin);
        }

        if (usuarioService.existsByNombreUsuario(nuevoUsuario.getNombreUsuario())) {
            return new ResponseEntity(new Mensaje("El nombre de usuario ya existe"), HttpStatus.BAD_REQUEST);
        }
        if (usuarioService.existsByEmail(nuevoUsuario.getEmail())) {
            return new ResponseEntity(new Mensaje("El email ya existe"), HttpStatus.BAD_REQUEST);
        }
        Usuario usuario = new Usuario(nuevoUsuario.getNombre(), nuevoUsuario.getNombreUsuario(), nuevoUsuario.getEmail(), passwordEncoder.encode(nuevoUsuario.getPassword()));
        Set<Rol> roles = new HashSet<>();

        if(nuevoUsuario.getNombreUsuario().equalsIgnoreCase("Admin")){

            roles.add(rolService.getByRolNombre(RolNombre.ROLE_ADMIN).get());
        } else {
            roles.add(rolService.getByRolNombre(RolNombre.ROLE_USER).get());
        }
        if (nuevoUsuario.getRoles().contains("admin")) {
            roles.add(rolService.getByRolNombre(RolNombre.ROLE_ADMIN).get());
        }
        usuario.setRoles(roles);
        usuarioService.save(usuario);
        return new ResponseEntity(new Mensaje("usuario guardado"), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDto> login(@Valid @RequestBody LoginUsuario loginUsuario, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return new ResponseEntity(new Mensaje("Campos mal puestos"), HttpStatus.BAD_REQUEST);
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUsuario.getNombreUsuario(), loginUsuario.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);
        JwtDto jwtDto = new JwtDto(jwt);
        return new ResponseEntity(jwtDto, HttpStatus.OK);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refresh(@RequestBody JwtDto jwtDto) throws ParseException{
        String token = jwtProvider.refreshToken(jwtDto);
        JwtDto jwt = new JwtDto(token);
        return new ResponseEntity(jwt, HttpStatus.OK);
    }
}

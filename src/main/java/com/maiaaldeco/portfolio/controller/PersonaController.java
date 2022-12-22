package com.maiaaldeco.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maiaaldeco.portfolio.dto.Mensaje;
import com.maiaaldeco.portfolio.dto.PersonaDto;
import com.maiaaldeco.portfolio.entity.Contacto;
import com.maiaaldeco.portfolio.entity.Persona;
import com.maiaaldeco.portfolio.security.entity.Usuario;
import com.maiaaldeco.portfolio.security.service.UsuarioService;
import com.maiaaldeco.portfolio.service.IContactoService;
import com.maiaaldeco.portfolio.service.IPersonaService;
import com.maiaaldeco.portfolio.service.ImageService;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
@CrossOrigin("*")
@RequestMapping("/persona")
public class PersonaController {

    @Autowired
    IPersonaService personaService;

    @Autowired
    IContactoService contactoService;

    @Autowired
    UsuarioService usuarioService;

    @ApiOperation("Muestra una lista de personas")
    @GetMapping("/lista")
    public ResponseEntity<List<Persona>> list() {
        List<Persona> list = personaService.list();
        return new ResponseEntity<List<Persona>>(list, HttpStatus.OK);
    }

    @ApiOperation("Muestra una persona por id")
    @GetMapping("/detail/{id}")
    public ResponseEntity<Persona> getById(@PathVariable("id") long id) {
        if (!personaService.existsById(id)) {
            return new ResponseEntity(new Mensaje("No existe el dato al que intenta acceder"), HttpStatus.NOT_FOUND);
        } else {
            Persona persona = personaService.getOne(id).get();
            return new ResponseEntity<Persona>(persona, HttpStatus.OK);
        }
    }

    @ApiOperation("Muestra una persona por contacto id")
    @GetMapping("/contacto/{contactoId}")
    public ResponseEntity<List<Persona>> getPersonaByContactoId(@PathVariable(value = "contactoId") long contactoId) {
        if (!contactoService.existsById(contactoId)) {
            return new ResponseEntity(new Mensaje("No existe el dato al que intenta acceder"), HttpStatus.NOT_FOUND);
        }
        List<Persona> contactos = personaService.findByContactoId(contactoId);
        return new ResponseEntity<>(contactos, HttpStatus.OK);
    }

    @ApiOperation("Muestra una persona por apellido")
    @GetMapping("/detailname/{apellido}")
    public ResponseEntity<List<Persona>> getByNombre(@PathVariable(value = "apellido") String apellido) {
        if (!personaService.existsByApellido(apellido)) {
            return new ResponseEntity(new Mensaje("No existe el dato al que intenta acceder"), HttpStatus.NOT_FOUND);
        }
        List<Persona> persona = personaService.findByApellido(apellido);
        return new ResponseEntity<>(persona, HttpStatus.OK);
    }

    @ApiOperation("Crea una persona")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestParam("imageFile") MultipartFile file, @RequestParam("persona") String persona,
            @CurrentSecurityContext(expression = "authentication?.name") String username) throws  IOException{

        PersonaDto personaRecibida = new ObjectMapper().readValue(persona, PersonaDto.class);

        Contacto contactoNuevo = new Contacto(personaRecibida.getLocalidad(), personaRecibida.getTelefono(), personaRecibida.getEmail());
        contactoService.save(contactoNuevo);
        Usuario usuario = usuarioService.getByNombreUsuario(username).get();
        System.out.println("usuario " + usuario.getNombreUsuario());

        Persona personaNueva = new Persona(personaRecibida.getNombre(), personaRecibida.getApellido(), personaRecibida.getStack(), personaRecibida.getTecnologia(), personaRecibida.getDescripcion(), ImageService.compressBytes(file.getBytes()), contactoNuevo, usuario);
        personaService.save(personaNueva);
        return new ResponseEntity(new Mensaje("Persona creado con éxito"), HttpStatus.OK);
    }

    @ApiOperation("Actualiza una persona")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable("id") long id, @RequestParam("persona") String persona) throws IOException {

        Persona personaRecibida = new ObjectMapper().readValue(persona, Persona.class);

        Persona personaNueva = personaService.getOne(id).get();
        personaNueva.setNombre(personaRecibida.getNombre());
        personaNueva.setApellido(personaRecibida.getApellido());
        personaNueva.setStack(personaRecibida.getStack());
        personaNueva.setTecnologia(personaRecibida.getTecnologia());
        personaNueva.setDescripcion(personaRecibida.getDescripcion());

        personaService.save(personaNueva);
        return new ResponseEntity(new Mensaje("Persona actualizado con éxito"), HttpStatus.OK);
    }

    @ApiOperation("Actualiza foto perfil")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateImg/{id}")
    public ResponseEntity<?> update(@PathVariable("id") long id, @RequestParam("imageFile") MultipartFile file) throws IOException {

        Persona personaNueva = personaService.getOne(id).get();
        personaNueva.setPicByte(ImageService.compressBytes(file.getBytes()));

        personaService.save(personaNueva);
        return new ResponseEntity(new Mensaje("Persona actualizado con éxito"), HttpStatus.OK);
    }
    @ApiOperation("Obtiene una foto por id")
    @GetMapping(path = "/getImage/{id}")
    public byte[] getImage(@PathVariable("id") long id) throws IOException {

        Persona retrievedImage = personaService.getOne(id).get();
        byte [] bytes = ImageService.decompressBytes(retrievedImage.getPicByte());
        return bytes;
    }

    @ApiOperation("Elimina una persona y todos sus datos asociados")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        if (!personaService.existsById(id)) {
            return new ResponseEntity(new Mensaje("No existe el dato al que intenta acceder"), HttpStatus.NOT_FOUND);
        }
        personaService.delete(id);
        return new ResponseEntity(new Mensaje("Eliminado con éxito"), HttpStatus.OK);
    }
}

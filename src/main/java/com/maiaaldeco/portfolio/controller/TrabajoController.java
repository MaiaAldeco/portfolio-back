package com.maiaaldeco.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maiaaldeco.portfolio.dto.Mensaje;
import com.maiaaldeco.portfolio.entity.Persona;
import com.maiaaldeco.portfolio.entity.Trabajo;
import com.maiaaldeco.portfolio.service.IPersonaService;
import com.maiaaldeco.portfolio.service.ITrabajoService;
import com.maiaaldeco.portfolio.service.ImageService;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin("*")
@RequestMapping("/trabajo")
public class TrabajoController {

    @Autowired
    ITrabajoService trabajoService;

    @Autowired
    IPersonaService personaService;

    @ApiOperation("Muestra una lista de proyectos")
    @GetMapping("/lista")
    public ResponseEntity<List<Trabajo>> list() {
        List<Trabajo> list = trabajoService.list();
        return new ResponseEntity<List<Trabajo>>(list, HttpStatus.OK);
    }

    @ApiOperation("Muestra un proyecto por id")
    @GetMapping("/detail/{id}")
    public ResponseEntity<Trabajo> getById(@PathVariable("id") long id) {
        if (!trabajoService.existsById(id)) {
            return new ResponseEntity(new Mensaje("No existe el dato al que intenta acceder"), HttpStatus.NOT_FOUND);
        } else {
            Trabajo trabajo = trabajoService.getOne(id).get();
            return new ResponseEntity<Trabajo>(trabajo, HttpStatus.OK);
        }
    }

    @ApiOperation("Muestra una lista de proyectos por nombre")
    @GetMapping("/detailname/{titulo}")
    public ResponseEntity<List<Trabajo>> getByNombre(@PathVariable(value = "titulo") String titulo) {
        if (!trabajoService.existsByTitulo(titulo)) {
            return new ResponseEntity(new Mensaje("No existe el dato al que intenta acceder"), HttpStatus.NOT_FOUND);
        }
        List<Trabajo> skill = trabajoService.getByTitulo(titulo);
        return new ResponseEntity<>(skill, HttpStatus.OK);
    }

    @ApiOperation("Muestra una lista de proyectos por id persona")
    @GetMapping("/persona/{persona_id}")
    public ResponseEntity<List<Trabajo>> getAllPersonasByEstudioId(@PathVariable(value = "persona_id") long persona_id) {
        if (!personaService.existsById(persona_id)) {
            return new ResponseEntity(new Mensaje("No existe el dato al que intenta acceder"), HttpStatus.NOT_FOUND);
        }
        List<Trabajo> exp = trabajoService.findByPersonaId(persona_id);
        return new ResponseEntity<>(exp, HttpStatus.OK);
    }

    @ApiOperation("Crea un trabajo por id persona")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create/{personaId}")
    public ResponseEntity<?> create(@PathVariable(value = "personaId") long personaId, @RequestParam("imageFile") MultipartFile file, @RequestParam("project") String project) throws IOException {

        Trabajo trabajo = new ObjectMapper().readValue(project, Trabajo.class);

        trabajo.setBytePic(ImageService.compressBytes(file.getBytes()));
        Persona persona = personaService.getOne(personaId).get();
        trabajo.setPersona(persona);
        trabajoService.save(trabajo);

        return new ResponseEntity(new Mensaje("Persona creado con éxito"), HttpStatus.OK);
    }

    @ApiOperation("Actualiza una proyecto por id")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable("id") long id, @RequestParam("project") String project) throws  IOException {

        Trabajo trabajo = new ObjectMapper().readValue(project, Trabajo.class);

        Trabajo trabajoNuevo = trabajoService.getOne(id).get();
        trabajoNuevo.setTitulo(trabajo.getTitulo());
        trabajoNuevo.setDescripcion(trabajo.getDescripcion());
        trabajoNuevo.setLinkTrabajo(trabajo.getLinkTrabajo());;

        trabajoService.save(trabajoNuevo);
        return new ResponseEntity(new Mensaje("Trabajo actualizado con éxito"), HttpStatus.OK);
    }

    @ApiOperation("Actualiza foto perfil")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateImg/{id}")
    public ResponseEntity<?> updateImg(@PathVariable("id") long id, @RequestParam("imageFile") MultipartFile file) throws IOException {

        Trabajo trabajo = trabajoService.getOne(id).get();
        trabajo.setBytePic(ImageService.compressBytes(file.getBytes()));

        trabajoService.save(trabajo);
        return new ResponseEntity(new Mensaje("Trabajo actualizado con éxito"), HttpStatus.OK);
    }

    @ApiOperation("Elimina un proyecto por id")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        if (!trabajoService.existsById(id)) {
            return new ResponseEntity(new Mensaje("No existe el dato al que intenta acceder"), HttpStatus.NOT_FOUND);
        }
        trabajoService.delete(id);
        return new ResponseEntity(new Mensaje("Trabajo eliminado con éxito"), HttpStatus.OK);
    }

    @ApiOperation("Elimina los proyectos por id persona")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{personaId}/trabajo")
    public ResponseEntity<List<Trabajo>> deleteAllEstudiosDePersonas(@PathVariable(value = "personaId") long personaId) {
        if (!personaService.existsById(personaId)) {
            return new ResponseEntity(new Mensaje("No existe el dato al que intenta acceder"), HttpStatus.NOT_FOUND);
        }
        trabajoService.deleteByPersonaId(personaId);
        return new ResponseEntity(new Mensaje("Personas eliminadas del trabajo con éxito"), HttpStatus.OK);
    }
    @ApiOperation("Obtiene imagen del proyecto por id")
    @GetMapping(path = "/getImage/{id}")
    public byte[] getImage(@PathVariable("id") long id) throws IOException {

        Trabajo retrievedImage = trabajoService.getOne(id).get();
        byte [] bytes = ImageService.decompressBytes(retrievedImage.getBytePic());
        return bytes;
    }
}

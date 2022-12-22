package com.maiaaldeco.portfolio.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.maiaaldeco.portfolio.service.ImageService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.util.Arrays;

@Entity
@NoArgsConstructor
@Getter @Setter
@Table(name="projects")
public class Trabajo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_trabajo")
    private long id;
    @NotNull
    @Column(name = "title")
    private String titulo;
    @NotNull
    @Column(name = "description", length = 3000)
    private String descripcion;
    @NotNull
    @Column(name = "link")
    private String linkTrabajo;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "people_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Persona persona;
    @Column(name = "pic_project", columnDefinition = "MEDIUMBLOB")
    @Lob
    private byte[] bytePic;

    public Trabajo(String titulo, String descripcion, String linkTrabajo, Persona persona, byte[] bytePic) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.linkTrabajo = linkTrabajo;
        this.persona = persona;
        this.bytePic = bytePic;
    }


    public Trabajo(String titulo, String descripcion, Persona persona) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.persona = persona;
    }

    public Trabajo(byte[] bytePic) {
        this.bytePic = bytePic;
    }
    
}

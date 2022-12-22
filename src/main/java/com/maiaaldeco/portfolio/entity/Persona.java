package com.maiaaldeco.portfolio.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.maiaaldeco.portfolio.security.entity.Usuario;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "people")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_people")
    private long id;

    @NotNull
    @Column(name = "firstname")
    private String nombre;
    @NotNull
    @Column(name = "lastname")
    private String apellido;
    @NotNull
    @Column(name = "stack")
    private String stack;
    @NotNull
    @Column(name = "tech")
    private String tecnologia;
    @Column(name = "pic_profile", columnDefinition="MEDIUMBLOB")
    @Lob
    private byte[] picByte;
    @NotNull
    @Column(name = "description", length = 4000)
    private String descripcion;
    @JsonIgnore
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "contacto_id")
    private Contacto contacto;
    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    
    public Persona(String nombre, String apellido, String stack, String tecnologia, String descripcion, byte[] picByte,Contacto contacto, Usuario usuario) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.stack = stack;
        this.tecnologia = tecnologia;
        this.descripcion = descripcion;
        this.picByte = picByte;
        this.contacto = contacto;
        this.usuario = usuario;
    }


    public Persona(String nombre, String apellido, String stack, String tecnologia, String descripcion, Contacto contacto) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.stack = stack;
        this.tecnologia = tecnologia;
        this.descripcion = descripcion;
        this.contacto = contacto;
    }
    
    
}

package com.maiaaldeco.portfolio.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "contacts")
public class Contacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contact")
    private long id;
    @NotNull
    @Column(name = "location")
    private String localidad;
    @NotNull
    @Column(name = "phone_number")
    private String telefono;
    @NotNull
    @Column(name = "email")
    private String email;

    public Contacto(String localidad, String telefono, String email) {
        this.localidad = localidad;
        this.telefono = telefono;
        this.email = email;
    }
}

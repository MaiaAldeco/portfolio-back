package com.maiaaldeco.portfolio.dto;

import com.maiaaldeco.portfolio.entity.Persona;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TrabajoDto {
    
    @NotBlank(message="El titulo es obligatorio")
    private String titulo;
    @NotBlank(message="La descripción es obligatoria")
    private String descripcion;
    @NotBlank(message="El link al trabajo es obligatorio")
    private String linkTrabajo;
    private Persona persona;
    private byte [] bytePic;


}

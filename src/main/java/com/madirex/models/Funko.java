package com.madirex.models;

import com.madirex.utils.Utils;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class Funko {
    @Builder.Default
    private UUID cod = UUID.randomUUID();
    private String name;
    private Model model;
    private double price;
    private LocalDate releaseDate;

    @Override
    public String toString() {
        return "Funko:" +
                "\n\tCod=" + cod +
                "\n\tNombre='" + name + '\'' +
                "\n\tModelo=" + model +
                "\n\tPrecio=" + Utils.getInstance().doubleToESLocal(price) +
                "\n\tFecha lanzamiento=" + releaseDate +
                '\n';
    }
}
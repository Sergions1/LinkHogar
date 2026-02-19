package com.linkhogar.domain.address;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linkhogar.domain.house.House;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Setter
@Getter
@Builder // (Lombok) Patrón Builder para crear instancias cómodamente.
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "addresses")
public class Address {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String street;
    @Column(nullable = false)
    private int number;
    private String floor;
    private String door;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String province;
    @Column(nullable = false)
    private String country;

    // --- Geolocalización ---
    // Usamos Double (clase wrapper) para permitir nulos.
    private Double latitude;
    private Double longitude;

    //Relaciones
    @OneToOne(mappedBy = "address")
    @ToString.Exclude // (Lombok) ¡IMPEDIR BUCLE INFINITO! Al imprimir Address, no imprimas House.
    @EqualsAndHashCode.Exclude // (Lombok) ¡IMPEDIR BUCLE INFINITO! Al comparar Address, ignora House.
    @JsonIgnore //Evita que Jackson intente pintar la casa dentro de la dirección
    private House house;
}

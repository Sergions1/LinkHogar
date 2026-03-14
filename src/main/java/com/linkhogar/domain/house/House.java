package com.linkhogar.domain.house;

import com.linkhogar.domain.address.Address;
import com.linkhogar.domain.common.enums.PublicationStatus;
import com.linkhogar.domain.house.enums.HouseStatus;
import com.linkhogar.domain.house.enums.HouseType;
import com.linkhogar.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "houses")
public class House {
    @Id
    private UUID id;
    private String title;
    private String description;
    private LocalDateTime creationDate;
    private LocalDateTime publicationDate;
    private LocalDateTime updateDate;
    private HouseType houseType;
    private PublicationStatus publicationStatus;
    private HouseStatus status;




    private int size;
    private int rooms;
    private int baths;

    @Builder.Default
    private Boolean lift = Boolean.FALSE;
    @Builder.Default
    private Boolean furnished = Boolean.FALSE;
    @Builder.Default
    private Boolean airConditioned = Boolean.FALSE;
    @Builder.Default
    private Boolean terrace = Boolean.FALSE;
    @Builder.Default
    private Boolean balcony = Boolean.FALSE;
    @Builder.Default
    private Boolean garage = Boolean.FALSE;
    @Builder.Default
    private Boolean storage = Boolean.FALSE;
    @Builder.Default
    private Boolean pool = Boolean.FALSE;
    @Builder.Default
    private Boolean commonAreas = Boolean.FALSE;
    @Builder.Default
    private Boolean petsAllowed = Boolean.FALSE;

    private long price;

    //Relaciones
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY) //No carga los datos a menos que se pidan
    private User owner;

}

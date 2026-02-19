package com.linkhogar.domain.house;

import com.linkhogar.domain.address.Address;
import com.linkhogar.domain.common.enums.PublicationStatus;
import com.linkhogar.domain.house.enums.*;
import com.linkhogar.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.CascadeType;

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

    private boolean lift = false;
    private boolean furnished = false;
    private boolean airConditioned = false;
    private boolean terrace = false;
    private boolean balcony = false;
    private boolean garage = false;
    private boolean storage = false;
    private boolean pool = false;
    private boolean commonAreas = false;
    private boolean petsAllowed = false;

    private long price;

    //Relaciones
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY) //No carga los datos a menos que se pidan
    private User owner;

}

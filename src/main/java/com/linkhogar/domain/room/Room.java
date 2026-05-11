package com.linkhogar.domain.room;

import com.linkhogar.domain.house.House;
import com.linkhogar.domain.room.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "house")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id", nullable = false)
    private House house;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long price;

    private Double size;

    @Column(name = "has_private_bath")
    private boolean hasPrivateBath;

    @Column(name = "bed_type")
    private String bedType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status;

    //Si status == AVAILABLE, todos estos campos serán NULL en BD.
    @Embedded
    private TenantProfile currentTenant;

    // Tabla auxiliar para las URLs de las fotos de la habitación
    @ElementCollection
    @CollectionTable(name = "room_photos", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "photo_url")
    private List<String> photoUrls = new ArrayList<>();
}

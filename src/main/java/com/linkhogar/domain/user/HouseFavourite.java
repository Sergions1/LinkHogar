package com.linkhogar.domain.user;

import com.linkhogar.domain.house.House;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "houseId"})
})
public class HouseFavourite {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "houseId", nullable = false)
    private House house;

    @Builder.Default
    private LocalDateTime addedAt = LocalDateTime.now();
}

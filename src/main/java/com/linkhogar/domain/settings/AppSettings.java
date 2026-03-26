package com.linkhogar.domain.settings;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AppSettings {
    @Id
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String value;

    private String description;
}

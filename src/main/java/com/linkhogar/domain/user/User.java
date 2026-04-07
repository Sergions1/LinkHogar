package com.linkhogar.domain.user;

import com.linkhogar.domain.house.House;
import com.linkhogar.domain.user.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
    @Id
    private UUID id;
    private String firstName;
    private String lastName;

    @Column(nullable = true)
    private Long phone;

    @Column(unique = true)
    private String mail;
    private String password;
    private LocalDate fecha_nac;
    private LocalDateTime registerDate;

    @Enumerated(EnumType.STRING) //Guardamos el texto, no el numero
    private Role role;
    private boolean enabled = true;

    private String avatarUrl;
    private String verificationCode;
    private LocalDateTime verificationCodeExpiration;

    //Relaciones
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true) //En caso de ser eliminada una casa del listado, esta se elimina
    @ToString.Exclude // ¡VITAL! Evita bucle infinito
    @Builder.Default  // Para que el Builder no la ponga a null
    private List<House> houses = new ArrayList<>();





    public void updateUser(String firstName, String lastName, LocalDate fechaNac, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.fecha_nac = fechaNac;
        if (phone != null && !phone.isBlank()) {
            this.phone = Long.parseLong(phone);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return getId().toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }


}

package com.linkhogar.domain.room;

import com.linkhogar.domain.room.enums.Occupation;
import com.linkhogar.domain.user.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantProfile {

    @Enumerated(EnumType.STRING)
    @Column(name = "tenant_gender")
    private Gender gender;

    @Column(name = "tenant_age_range", length = 20)
    private String ageRange;

    @Enumerated(EnumType.STRING)
    @Column(name = "tenant_occupation")
    private Occupation occupation;

    @Column(name = "tenant_description", length = 500)
    private String description;

    @Column(name = "tenant_is_smoker")
    private Boolean isSmoker;

    @Column(name = "tenant_has_pets")
    private Boolean hasPets;
}
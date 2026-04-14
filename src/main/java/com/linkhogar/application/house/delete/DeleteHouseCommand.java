package com.linkhogar.application.house.delete;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

public record DeleteHouseCommand(UUID houseId, UUID userId, Collection<? extends GrantedAuthority> authorities){
}

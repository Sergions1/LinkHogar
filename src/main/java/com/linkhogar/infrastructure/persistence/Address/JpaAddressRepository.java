package com.linkhogar.infrastructure.persistence.Address;

import com.linkhogar.domain.address.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaAddressRepository extends JpaRepository<Address, UUID> {
}

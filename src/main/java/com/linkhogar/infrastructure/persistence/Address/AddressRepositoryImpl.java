package com.linkhogar.infrastructure.persistence.Address;

import com.linkhogar.domain.address.Address;
import com.linkhogar.domain.address.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AddressRepositoryImpl implements AddressRepository {
    private final JpaAddressRepository jpaAddressRepository;

    @Override
    public void save(Address address) {
        jpaAddressRepository.save(address);
    }
}

package com.linkhogar.application.house.getByOwnerId;

import lombok.Value;

import java.util.UUID;

@Value
public class GetByOwnerIdQuery {
    UUID ownerId;
    int page;
    int size;
}

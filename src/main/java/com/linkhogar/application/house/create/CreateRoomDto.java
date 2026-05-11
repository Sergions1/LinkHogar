package com.linkhogar.application.house.create;


import com.linkhogar.domain.room.enums.RoomStatus;

public record CreateRoomDto (
        String name,
        Long price, // Usamos Long para coincidir con tu entidad House
        Double size,
        Boolean hasPrivateBath,
        String bedType,
        RoomStatus status
) {}
package com.linkhogar.application.user.deleteHouseFavourite;

import java.util.UUID;

public record DeleteHouseFavouriteCommand(UUID userId, UUID houseId) {
}

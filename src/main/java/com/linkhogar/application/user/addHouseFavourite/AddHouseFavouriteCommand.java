package com.linkhogar.application.user.addHouseFavourite;

import java.util.UUID;

public record AddHouseFavouriteCommand(UUID userId, UUID houseId) {}

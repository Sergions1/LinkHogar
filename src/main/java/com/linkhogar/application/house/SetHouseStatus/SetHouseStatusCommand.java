package com.linkhogar.application.house.SetHouseStatus;

import com.linkhogar.domain.common.enums.PublicationStatus;
import java.util.UUID;

public record SetHouseStatusCommand (UUID houseId,
                                    PublicationStatus status){}

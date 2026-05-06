package com.linkhogar.application.home.addMember;

import java.util.UUID;

public record AddMemberToHomeCommand(
        UUID homeId,
        String email,
        UUID requesterId
) {
}
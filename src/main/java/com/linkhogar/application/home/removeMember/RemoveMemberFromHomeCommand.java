package com.linkhogar.application.home.removeMember;

import java.util.UUID;

public record RemoveMemberFromHomeCommand(
        UUID homeId,
        UUID memberId,
        UUID requesterId
) {
}
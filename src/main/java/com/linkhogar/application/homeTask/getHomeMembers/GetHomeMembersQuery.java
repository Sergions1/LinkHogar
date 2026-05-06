package com.linkhogar.application.homeTask.getHomeMembers;

import java.util.UUID;

public record GetHomeMembersQuery (
        UUID homeId,
        UUID requesterId
){
}

package com.linkhogar.application.homeTask.getHomeMembers;

import java.util.UUID;

public record HomeMemberResponse (
        UUID id,
        String firstName,
        String lastName,
        String name,
        String avatarUrl,
        String email
){
}

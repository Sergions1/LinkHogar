package com.linkhogar.application.homeTask.getHomeMembers;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.homeTasks.HomeErrors;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetHomeMembersQueryHandler {
    private final UserRepository userRepository;

    public Result<List<HomeMemberResponse>> handle(GetHomeMembersQuery query){
        try {
            Optional<User> requesterOpt = userRepository.userById(query.requesterId());

            if (requesterOpt.isEmpty()) {
                return Result.failure(UserErrors.NotFound(query.requesterId()));
            }

            User requester = requesterOpt.get();

            // Seguridad: Verificamos que el usuario pertenece al hogar que está consultando
            if (requester.getHomeId() == null || !requester.getHomeId().equals(query.homeId())) {
                return Result.failure(HomeErrors.UNAUTHORIZED_ACCESS);
            }

            // Obtenemos todos los usuarios con ese homeId
            List<User> members = userRepository.findByHome(query.homeId());

            // Mapeamos al DTO para no exponer contraseñas ni datos sensibles
            List<HomeMemberResponse> response = members.stream()
                    .map(user -> new HomeMemberResponse(
                            user.getId(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getFirstName() + " " + user.getLastName(),
                            user.getAvatarUrl()
                    ))
                    .collect(Collectors.toList());

            return Result.success(response);

        } catch (Exception e) {
            System.out.println("Error al obtener integrantes del hogar: " + e.getMessage());
            return Result.failure(HomeErrors.GET_MEMBERS_FAILED);
        }
    }
}

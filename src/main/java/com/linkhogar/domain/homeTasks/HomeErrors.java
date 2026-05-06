package com.linkhogar.domain.homeTasks;

import com.linkhogar.domain.common.result.Error;
import java.util.UUID;

public class HomeErrors {
    private HomeErrors() {}

    public static Error TaskNotFound(UUID taskId) {
        return Error.notFound(
                "HomeTasks.NotFound",
                "La tarea con el Id = '" + taskId + "' no fue encontrada"
        );
    }

    public static final Error UNAUTHORIZED_ACCESS = Error.unauthorized(
            "HomeTasks.UnauthorizedAccess",
            "Acceso denegado: No perteneces a este hogar"
    );

    public static final Error INVALID_STATUS = Error.conflict(
            "HomeTasks.InvalidStatus",
            "El estado proporcionado no es válido"
    );

    public static final Error CREATION_FAILED = Error.failure(
            "HomeTasks.CreationFailed",
            "No se pudo crear la tarea"
    );

    public static final Error TASK_UPDATE_FAILED = Error.failure(
            "HomeTasks.UpdateFailed",
            "No se pudo actualizar el estado de la tarea"
    );

    public static final Error GET_MEMBERS_FAILED = Error.failure(
            "HomeTasks.GetMembersFailed",
            "No se pudo obtener a los miembros del hogar"
    );

    public static final Error GET_CHAT_FAILED = Error.failure(
            "HomeTasks.GetChatFailed",
            "No se pudo obtener a el chat del hogar"
    );

    public static final Error USER_IN_HOME = Error.failure(
            "HomeTasks.UserInHome",
            "El usuario ya pertenece a este hogar"
    );

    public static final Error USER_NOT_IN_HOME = Error.failure(
            "HomeTasks.UserNotInHome",
            "El usuario no pertenece a este hogar"
    );

    public static final Error USER_IN_OTHER_HOME = Error.failure(
            "HomeTasks.UserInHome",
            "El usuario ya está asignado a otro hogar diferente"
    );

    public static final Error ADD_MEMBER_FAILED = Error.failure(
            "HomeTasks.AddMemberFailed",
            "Ha ocurrido un error al añadir al usuario"
    );

    public static final Error REMOVE_MEMBER_FAILED = Error.failure(
            "HomeTasks.RemoveMemberFailed",
            "Ha ocurrido un error al eliminar al usuario"
    );
}
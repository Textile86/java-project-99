package hexlet.code.mapper;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserPatchDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.model.User;
import org.mapstruct.*;

@Mapper(
        uses = { JsonNullableMapper.class },
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {
    UserDTO toDTO(User user);

    @Mapping(target = "passwordDigest", ignore = true)
    User toEntity(UserCreateDTO dto);

    @Mapping(target = "passwordDigest", ignore = true)
    void updateEntity(UserUpdateDTO dto, @MappingTarget User user);

    @Mapping(target = "passwordDigest", ignore = true)
    void updateEntityFromPatch(UserPatchDTO dto, @MappingTarget User user);
}


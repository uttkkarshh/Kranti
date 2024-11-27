package com.ut.kranti.user;

import org.modelmapper.ModelMapper;

public class UserMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    // Entity to DTO
    public static UserDto toDto(UserProfile user) {
        return modelMapper.map(user, UserDto.class);
    }

    // DTO to Entity
    public static UserProfile toEntity(UserDto userDto) {
        return modelMapper.map(userDto, UserProfile.class);
    }
}

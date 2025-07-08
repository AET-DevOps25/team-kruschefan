package com.devops.kruschefan.user.config;

import com.devops.kruschefan.openapi.model.UserResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public Converter<UserRepresentation, UserResponse> userRepresentationToUserResponseConverter() {
        return context -> {
            UserRepresentation source = context.getSource();
            UserResponse destination = new UserResponse();
            destination.setId(source.getId());
            destination.setUsername(source.getUsername());
            destination.setEmail(source.getEmail());
            destination.setFirstName(source.getFirstName());
            destination.setLastName(source.getLastName());
            return destination;
        };
    }
}
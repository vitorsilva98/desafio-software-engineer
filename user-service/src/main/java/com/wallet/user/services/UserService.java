package com.wallet.user.services;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.wallet.user.configurations.exception.InvalidEntityDataException;
import com.wallet.user.models.UserModel;
import com.wallet.user.payloads.requests.CreateUserRequest;
import com.wallet.user.payloads.responses.CreateUserResponse;
import com.wallet.user.repositories.UserRepository;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
	private RabbitTemplate rabbitTemplate;

    public CreateUserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InvalidEntityDataException("Este email já é utilizado.");
        }

        if (userRepository.existsByDocument(request.getDocument())) {
            throw new InvalidEntityDataException("Este documento já é utilizado.");
        }

        UserModel userModel = new UserModel();
        userModel.setDocument(request.getDocument());
        userModel.setEmail(request.getEmail());
        userModel.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));
        userModel.setDisabled(Boolean.FALSE);
        userRepository.save(userModel);
        LOGGER.info(String.format("[USER CREATED] = %s", userModel));

        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(userModel.getId(), userModel.getEmail());
        rabbitTemplate.convertAndSend("users_events.created", userCreatedEvent);
        LOGGER.info(String.format("Sent %s to users_events.created", userCreatedEvent));

        return new CreateUserResponse("Cadastrado com sucesso. Você receberá seus dados bancários em seu email.");
    }

    private record UserCreatedEvent(UUID id, String email) {}
}

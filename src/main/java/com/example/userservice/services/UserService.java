package com.example.userservice.services;

import com.example.userservice.configs.KafkaProducerConfig;
import com.example.userservice.dtos.SendEmailDto;
import com.example.userservice.exceptions.InvalidCredentialsException;
import com.example.userservice.exceptions.TokenNotFoundException;
import com.example.userservice.exceptions.UserNotFoundException;
import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.repositories.TokenRepository;
import com.example.userservice.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;
    private TokenRepository tokenRepository;
    private KafkaProducerConfig kafkaProducerConfig;
    private ObjectMapper objectMapper;
    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder,
                       UserRepository userRepository,
                       TokenRepository tokenRepository,
                       KafkaProducerConfig kafkaProducerConfig,
                       ObjectMapper objectMapper) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.kafkaProducerConfig = kafkaProducerConfig;
        this.objectMapper = objectMapper;
    }
    public User signUp(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));
        user.setEmailVerified(true);
        user.setCreatedAt(new Date());


        // save the user to database
        User savedUser = userRepository.save(user);

        // push the event to Kafka
        SendEmailDto emailDto = new SendEmailDto();
        emailDto.setReceiverEmail(user.getEmail());
        emailDto.setSenderEmail("kandarpanaveen77@gmail.com");
        emailDto.setSubject("Welcome to Amazon");
        emailDto.setBody("Thank you for registering at Amazon. Hope you will have a great experience!!");
        try {
            kafkaProducerConfig.sendMessage("sendEmail", objectMapper.writeValueAsString(emailDto));
        }
        catch(Exception e) {
            System.out.println("Something went wrong while sending a message to Kafka");
        }
        return savedUser;
    }

    public Token login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()) {
            throw new UserNotFoundException("No user found with given email " + email + " Please signup");
        }
        User user = optionalUser.get();
        if(!bCryptPasswordEncoder.matches(password, user.getHashedPassword())) {
            // login failed
            throw new InvalidCredentialsException("Please provide valid credentials");
        }
        // Login successful, generate a token
        Token token = generateToken(user);
        return tokenRepository.save(token);
        // TODO: restrict the number of active sessions
    }

    private Token generateToken(User user) {
        Token token = new Token();
        // set the expiry date
        Date date = new Date();
        LocalDate currentDate = LocalDate.now();
        LocalDate futureDate = currentDate.plusDays(30);
        Date expiryDate = Date.from(futureDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        token.setExpiryAt(expiryDate);

        // generate a random token of length 128
        token.setValue(RandomStringUtils.randomAlphanumeric(128));

        // setting the user
        token.setUser(user);
        return token;
    }

    public void logout(String tokenValue) {
        Optional<Token> optionalToken = tokenRepository.findByValueAndIsDeleted(tokenValue, false);
        if(optionalToken.isEmpty()) {
            throw new TokenNotFoundException("No valid token found");
        }
        Token token = optionalToken.get();
        token.setDeleted(true);
        tokenRepository.save(token);
    }

    public User validateToken(String tokenValue) {
        Optional<Token> optionalToken = tokenRepository.findByValueAndIsDeletedAndExpiryAtGreaterThanEqual(tokenValue, false, new Date());
        if(optionalToken.isEmpty()) {
            throw new TokenNotFoundException("No token found");
        }
        Token token = optionalToken.get();
        return token.getUser();
    }
}

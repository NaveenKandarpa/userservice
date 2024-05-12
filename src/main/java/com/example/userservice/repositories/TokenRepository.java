package com.example.userservice.repositories;

import com.example.userservice.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    @Override
    Token save(Token token);

    Optional<Token> findByValueAndIsDeleted(String tokenValue, boolean IsDeleted);

    /*
        select * frok tokens where value = tokenValue and isDeleted = false and expiryAt > currentTime
     */
    Optional<Token> findByValueAndIsDeletedAndExpiryAtGreaterThanEqual(String tokenValue, boolean isDeleted, Date currentTime);

}

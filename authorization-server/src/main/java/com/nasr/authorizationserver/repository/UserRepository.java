package com.nasr.authorizationserver.repository;

import com.nasr.authorizationserver.domain.User;
import com.nasr.authorizationserver.dto.response.UserResponseDto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    @EntityGraph(attributePaths = "role")
    Optional<User> findWithRoleByEmail(String email);

    @Query(" select case when count(u.id)> 0 then true else false end from User as u")
    Boolean isExists();

    @Query("select new com.nasr.authorizationserver.dto.response.UserResponseDto(u.id,u.firstName,u.lastName,u.email) " +
            "from User as u where u.email = :email")
    UserResponseDto findByUserName(String email);
}

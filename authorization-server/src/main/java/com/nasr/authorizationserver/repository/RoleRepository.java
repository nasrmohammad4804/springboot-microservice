package com.nasr.authorizationserver.repository;

import com.nasr.authorizationserver.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByName(String name);

    @Query("select case when count(r.id)>0 then true  else false  end from Role as r")
    Boolean isExists();
}

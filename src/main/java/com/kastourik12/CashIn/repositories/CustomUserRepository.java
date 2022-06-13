package com.kastourik12.CashIn.repositories;


import com.kastourik12.CashIn.models.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomUserRepository extends JpaRepository<CustomUser,Long> {
    Optional<CustomUser> findByUsername(String username);

    @Query("SELECT u FROM CustomUser u WHERE u.username = ?1 OR u.email = ?1 OR u.phone = ?1")
    Optional<CustomUser> findByUsernameOrEmailOrPhone(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}

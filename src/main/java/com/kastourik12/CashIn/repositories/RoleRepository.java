package com.kastourik12.CashIn.repositories;

import com.kastourik12.CashIn.models.ERole;
import com.kastourik12.CashIn.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository< Role , Long> {
    Optional<Role> findByName(ERole roleAdmin);
}

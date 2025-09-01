package com.libra_s.libraS.repository;

import com.libra_s.libraS.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long>, AppUserRepositoryCustom {
    Optional<AppUser> findByEmail(String email);

    @Query("SELECT COUNT(u) FROM AppUser u WHERE u.created_at >= :since")
    long countUsersCreatedSince(@Param("since") LocalDateTime since);
}

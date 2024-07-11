package com.libra_s.libraS.repository;

import com.libra_s.libraS.domain.UserBookInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBookInfoRepository extends JpaRepository<UserBookInfo, Long> {

    Optional<UserBookInfo> findByAppUserIdAndBookId(Long userId, Long bookId);
}

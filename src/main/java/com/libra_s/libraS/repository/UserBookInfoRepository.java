package com.libra_s.libraS.repository;

import com.libra_s.libraS.domain.UserBookInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBookInfoRepository extends JpaRepository<UserBookInfo, Long> {

}

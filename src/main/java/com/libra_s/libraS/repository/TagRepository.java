package com.libra_s.libraS.repository;

import com.libra_s.libraS.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("SELECT t, COUNT(DISTINCT ubi.appUser.id) as readers " +
           "FROM UserBookInfo ubi JOIN ubi.book b JOIN b.tags t " +
           "GROUP BY t ORDER BY readers DESC")
    List<Object[]> findTopTagsByReaders(Pageable pageable);
}

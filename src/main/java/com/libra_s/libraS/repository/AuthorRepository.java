package com.libra_s.libraS.repository;

import com.libra_s.libraS.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("SELECT a, COUNT(DISTINCT ubi.appUser.id) as readers " +
           "FROM UserBookInfo ubi JOIN ubi.book b JOIN b.authors a " +
           "GROUP BY a ORDER BY readers DESC")
    List<Object[]> findTopAuthorsByReaders(Pageable pageable);
}

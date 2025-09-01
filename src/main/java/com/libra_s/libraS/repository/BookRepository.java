package com.libra_s.libraS.repository;

import com.libra_s.libraS.domain.Book;
import com.libra_s.libraS.domain.enums.UserBookStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {

    List<Book> findTop8ByOrderByNbVisitDesc();

    List<Book> findTop20ByOrderByNbVisitDesc();

    List<Book> findTop8ByOrderByNoteDesc();

    List<Book> findTop8ByIsCompletedTrueOrderByNoteDescNbVisitDesc();

    List<Book> findTop8ByIsCompletedFalseOrderByNoteDescNbVisitDesc();

    @Query("SELECT ubi.book FROM UserBookInfo ubi WHERE ubi.appUser.id = :userId AND ubi.status = :status")
    List<Book> findUserBookInProgress(Long userId, UserBookStatus status, Pageable pageable);

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.id IN :authorIds")
    List<Book> findByAuthorIds(List<Long> authorIds);

    @Query("SELECT b FROM Book b JOIN b.tags t WHERE t.name IN :tags GROUP BY b HAVING COUNT(b) = :tagCount")
    List<Book> findByTags(List<String> tags, Long tagCount);

    List<Book> findTop20ByOrderByDateStartDesc();
    List<Book> findTop8ByOrderByDateStartDesc();

    Optional<Book> findByFrenchSearchName(String title);

    @Query("SELECT ubi.book FROM UserBookInfo ubi WHERE ubi.appUser.id = :userId")
    List<Book> findBooksByUser(Long userId);

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE " +
            "b.frenchSearchName LIKE %:search% OR " +
            "a.name LIKE %:search% OR " +
            "b.synopsis LIKE %:search%")
    List<Book> search(@Param("search") String search);

    @Query("SELECT COUNT(b) FROM Book b WHERE b.createdAt >= :since")
    long countBooksCreatedSince(@Param("since") LocalDate since);

    List<Book> findAllByOrderByNoteDesc(Pageable pageable);

    List<Book> findAllByNoteNotNullOrderByNoteDesc(Pageable pageable);
}

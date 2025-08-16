package com.libra_s.libraS.repository;

import com.libra_s.libraS.domain.Book;
import com.libra_s.libraS.dtos.BookFilterDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BookRepositoryImpl implements BookRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Book> findBooksWithFilters(BookFilterDto filter, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> book = query.from(Book.class);

        List<Predicate> predicates = buildPredicates(cb, book, filter);
        query.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Book> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Book> books = typedQuery.getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Book> countBook = countQuery.from(Book.class);
        countQuery.select(cb.count(countBook));
        countQuery.where(buildPredicates(cb, countBook, filter).toArray(new Predicate[0]));

        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(books, pageable, total);
    }

    @Override
    public List<Book> findBooksWithFilters(BookFilterDto filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> book = query.from(Book.class);

        List<Predicate> predicates = buildPredicates(cb, book, filter);
        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList();
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Book> book, BookFilterDto filter) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasText(filter.getSearch())) {
            String searchPattern = "%" + filter.getSearch().toLowerCase() + "%";
            Predicate searchPredicate = cb.or(
                cb.like(cb.lower(book.get("frenchSearchName")), searchPattern),
                cb.like(cb.lower(book.get("synopsis")), searchPattern)
            );
            predicates.add(searchPredicate);
        }

        if (filter.getDateFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(book.get("dateStart"), filter.getDateFrom()));
        }

        if (filter.getDateTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(book.get("dateStart"), filter.getDateTo()));
        }

        if (filter.getIsCompleted() != null) {
            predicates.add(cb.equal(book.get("isCompleted"), filter.getIsCompleted()));
        }

        if (filter.getMinVolumes() != null) {
            predicates.add(cb.greaterThanOrEqualTo(book.get("nbVolume"), filter.getMinVolumes()));
        }

        if (filter.getMaxVolumes() != null) {
            predicates.add(cb.lessThanOrEqualTo(book.get("nbVolume"), filter.getMaxVolumes()));
        }

        if (filter.getMinRating() != null) {
            predicates.add(cb.greaterThanOrEqualTo(book.get("note"), filter.getMinRating()));
        }

        if (filter.getMaxRating() != null) {
            predicates.add(cb.lessThanOrEqualTo(book.get("note"), filter.getMaxRating()));
        }

        if (filter.getTags() != null && !filter.getTags().isEmpty()) {
            Join<Book, Object> tagJoin = book.join("tags");
            predicates.add(tagJoin.get("name").in(filter.getTags()));
        }

        if (StringUtils.hasText(filter.getAuthor())) {
            Join<Book, Object> authorJoin = book.join("authors");
            predicates.add(cb.like(cb.lower(authorJoin.get("name")), 
                "%" + filter.getAuthor().toLowerCase() + "%"));
        }

        return predicates;
    }
}

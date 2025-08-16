package com.libra_s.libraS.repository;

import com.libra_s.libraS.domain.AppUser;
import com.libra_s.libraS.dtos.UserFilterDto;
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
public class AppUserRepositoryImpl implements AppUserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<AppUser> findUsersWithFilters(UserFilterDto filter, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AppUser> query = cb.createQuery(AppUser.class);
        Root<AppUser> user = query.from(AppUser.class);

        List<Predicate> predicates = buildPredicates(cb, user, filter);
        query.where(predicates.toArray(new Predicate[0]));

        TypedQuery<AppUser> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<AppUser> users = typedQuery.getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<AppUser> countUser = countQuery.from(AppUser.class);
        countQuery.select(cb.count(countUser));
        countQuery.where(buildPredicates(cb, countUser, filter).toArray(new Predicate[0]));

        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(users, pageable, total);
    }

    @Override
    public List<AppUser> findUsersWithFilters(UserFilterDto filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AppUser> query = cb.createQuery(AppUser.class);
        Root<AppUser> user = query.from(AppUser.class);

        List<Predicate> predicates = buildPredicates(cb, user, filter);
        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList();
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<AppUser> user, UserFilterDto filter) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasText(filter.getSearch())) {
            String searchPattern = "%" + filter.getSearch().toLowerCase() + "%";
            Predicate searchPredicate = cb.or(
                cb.like(cb.lower(user.get("displayname")), searchPattern),
                cb.like(cb.lower(user.get("email")), searchPattern)
            );
            predicates.add(searchPredicate);
        }

        if (filter.getRole() != null) {
            predicates.add(cb.like(
                user.get("roles").as(String.class), 
                "%" + filter.getRole().name() + "%"));
        }

        if (filter.getCreatedAfter() != null) {
            predicates.add(cb.greaterThanOrEqualTo(user.get("created_at"), filter.getCreatedAfter()));
        }

        if (filter.getCreatedBefore() != null) {
            predicates.add(cb.lessThanOrEqualTo(user.get("created_at"), filter.getCreatedBefore()));
        }

        return predicates;
    }
}

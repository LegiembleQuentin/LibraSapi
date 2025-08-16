package com.libra_s.libraS.repository;

import com.libra_s.libraS.domain.AppUser;
import com.libra_s.libraS.dtos.UserFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AppUserRepositoryCustom {
    Page<AppUser> findUsersWithFilters(UserFilterDto filter, Pageable pageable);
    List<AppUser> findUsersWithFilters(UserFilterDto filter);
}

package com.libra_s.libraS.service;

import com.libra_s.libraS.domain.AppUser;
import com.libra_s.libraS.dtos.AdminUserDto;
import com.libra_s.libraS.dtos.UserFilterDto;
import com.libra_s.libraS.dtos.mapper.AdminUserMapper;
import com.libra_s.libraS.repository.AppUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final AdminUserMapper adminUserMapper;

    public AppUserService(AppUserRepository appUserRepository, AdminUserMapper adminUserMapper) {
        this.appUserRepository = appUserRepository;
        this.adminUserMapper = adminUserMapper;
    }

    public Optional<AppUser> getConnectedUser(UserDetails userDetails) {
        String email = userDetails.getUsername();
        return appUserRepository.findByEmail(email);
    }
    
    public Page<AdminUserDto> getUsersWithFilters(UserFilterDto filter, Pageable pageable) {
        Page<AppUser> usersPage = appUserRepository.findUsersWithFilters(filter, pageable);
        return usersPage.map(adminUserMapper::toAdminDto);
    }
    
    public Optional<AdminUserDto> getUserById(Long id) {
        return appUserRepository.findById(id)
            .map(adminUserMapper::toAdminDto);
    }
}

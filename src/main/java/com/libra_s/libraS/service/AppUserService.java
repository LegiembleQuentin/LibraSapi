package com.libra_s.libraS.service;

import com.libra_s.libraS.domain.AppUser;
import com.libra_s.libraS.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppUserService {
    AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }


    public Optional<AppUser> getConnectedUser(UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<AppUser> user = appUserRepository.findByEmail(email);
        return user;
    }
}

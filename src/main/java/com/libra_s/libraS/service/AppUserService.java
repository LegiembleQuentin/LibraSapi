package com.libra_s.libraS.service;

import com.libra_s.libraS.domain.AppUser;
import com.libra_s.libraS.domain.UserBookInfo;
import com.libra_s.libraS.domain.enums.UserBookStatus;
import com.libra_s.libraS.dtos.AdminUserDto;
import com.libra_s.libraS.dtos.UserFilterDto;
import com.libra_s.libraS.dtos.mapper.AdminUserMapper;
import com.libra_s.libraS.repository.AppUserRepository;
import com.libra_s.libraS.repository.UserBookInfoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final AdminUserMapper adminUserMapper;
    private final UserBookInfoRepository userBookInfoRepository;

    public AppUserService(AppUserRepository appUserRepository, AdminUserMapper adminUserMapper, UserBookInfoRepository userBookInfoRepository) {
        this.appUserRepository = appUserRepository;
        this.adminUserMapper = adminUserMapper;
        this.userBookInfoRepository = userBookInfoRepository;
    }

    public Optional<AppUser> getConnectedUser(UserDetails userDetails) {
        String email = userDetails.getUsername();
        return appUserRepository.findByEmail(email);
    }
    
    public Page<AdminUserDto> getUsersWithFilters(UserFilterDto filter, Pageable pageable) {
        Page<AppUser> usersPage = appUserRepository.findUsersWithFilters(filter, pageable);
        return usersPage.map(adminUserMapper::toAdminDto);
    }
    
    public Optional<AdminUserDto> getUserByIdForAdmin(Long id) {
        Optional<AppUser> userOpt = appUserRepository.findById(id);
        if (userOpt.isPresent()) {
            AppUser user = userOpt.get();
            AdminUserDto adminUserDto = adminUserMapper.toAdminDto(user);
            
            List<UserBookInfo> userBooks = userBookInfoRepository.findByAppUserId(id);
            
            Long totalBooks = (long) userBooks.size();
            Long booksInProgress = userBooks.stream()
                .filter(ubi -> ubi.getStatus() == UserBookStatus.READING)
                .count();
            Long booksCompleted = userBooks.stream()
                .filter(ubi -> ubi.getStatus() == UserBookStatus.COMPLETED)
                .count();
            
            Double averageRating = userBooks.stream()
                .filter(ubi -> ubi.getNote() > 0)
                .mapToDouble(UserBookInfo::getNote)
                .average()
                .orElse(0.0);
            
            adminUserDto.setTotalBooks(totalBooks);
            adminUserDto.setBooksInProgress(booksInProgress);
            adminUserDto.setBooksCompleted(booksCompleted);
            adminUserDto.setAverageRating(averageRating);
            
            return Optional.of(adminUserDto);
        }
        return Optional.empty();
    }
}

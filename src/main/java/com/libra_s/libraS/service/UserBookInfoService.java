package com.libra_s.libraS.service;

import com.libra_s.libraS.domain.UserBookInfo;
import com.libra_s.libraS.repository.UserBookInfoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserBookInfoService {

    private final UserBookInfoRepository userBookInfoRepository;

    public UserBookInfoService(UserBookInfoRepository userBookInfoRepository) {
        this.userBookInfoRepository = userBookInfoRepository;
    }

    public Optional<UserBookInfo> getUserBookInfo(Long userId, Long bookId) {
        Optional<UserBookInfo> result = userBookInfoRepository.findByAppUserIdAndBookId(userId, bookId);

        return result;
    }
}

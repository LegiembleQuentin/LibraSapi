package com.libra_s.libraS.service;

import com.libra_s.libraS.domain.AppUser;
import com.libra_s.libraS.domain.Book;
import com.libra_s.libraS.domain.UserBookInfo;
import com.libra_s.libraS.domain.enums.UserBookStatus;
import com.libra_s.libraS.repository.UserBookInfoRepository;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

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

    public void switchBookInLibrary(Book book, AppUser user) {
        Optional<UserBookInfo> userBookInfoDb = getUserBookInfo(user.getId(), book.getId());

        if (userBookInfoDb.isPresent()) {
            UserBookInfo userBookInfo = userBookInfoDb.get();
            userBookInfoRepository.delete(userBookInfo);
        } else {
            addBookToLibrary(book, user);
        }
    }

    public void addBookToLibrary(Book book, AppUser user) {
        UserBookInfo userBookInfo = new UserBookInfo();
        userBookInfo.setBook(book);
        userBookInfo.setAppUser(user);
        userBookInfo.setStatus(UserBookStatus.TO_READ);
        userBookInfo.setModifiedAt(LocalDateTime.now());

        userBookInfoRepository.save(userBookInfo);
    }

    public void save(UserBookInfo userBookInfo) {
        userBookInfo.setModifiedAt(LocalDateTime.now());
        userBookInfoRepository.save(userBookInfo);
    }

    public List<UserBookInfo> getUserBookInfos(Long userId) {
        List<UserBookInfo> result = userBookInfoRepository.findByAppUserId(userId);

        return result;
    }
}

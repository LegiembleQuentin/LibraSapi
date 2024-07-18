package com.libra_s.libraS.service;

import com.libra_s.libraS.domain.AppUser;
import com.libra_s.libraS.domain.Book;
import com.libra_s.libraS.domain.UserBookInfo;
import com.libra_s.libraS.domain.enums.UserBookStatus;
import com.libra_s.libraS.dtos.AuthorDto;
import com.libra_s.libraS.dtos.BookDto;
import com.libra_s.libraS.dtos.DiscoverPageDto;
import com.libra_s.libraS.dtos.TagDto;
import com.libra_s.libraS.dtos.mapper.BookMapper;
import com.libra_s.libraS.repository.BookRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final UserBookInfoService userBookInfoService;

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookService(UserBookInfoService userBookInfoService, BookRepository bookRepository, BookMapper bookMapper) {
        this.userBookInfoService = userBookInfoService;
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    public List<BookDto> getBooks() {
        List<Book> books = bookRepository.findAll();

        return books.stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public DiscoverPageDto getDiscoverPageInfos(Long userId) {
        DiscoverPageDto discoverPageDto = new DiscoverPageDto();

        discoverPageDto.setPopular(get8MostPopularBooks());
        discoverPageDto.setBestRated(get8BestRatedBooks());
        discoverPageDto.setNewBooks(get8LastModifiedBooks());
        discoverPageDto.setCompleted(get8BestRatedCompletedBooks());
        discoverPageDto.setUserInProgress(get8InProgressBookByUser(userId));


        discoverPageDto.setRecommended(new ArrayList<>());
        discoverPageDto.setCarrousselBooks(new ArrayList<>());

        return discoverPageDto;
    }

    public List<BookDto> get8MostPopularBooks() {
        List<Book> books = bookRepository.findTop8ByOrderByNbVisitDesc();

        return books.stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> get8BestRatedBooks() {
        List<Book> books = bookRepository.findTop8ByOrderByNoteDesc();

        return books.stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> get8LastModifiedBooks() {
        List<Book> books = bookRepository.findTop8ByOrderByDateStartDesc();

        return books.stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> get8BestRatedCompletedBooks() {
        List<Book> books = bookRepository.findTop8ByIsCompletedTrueOrderByNoteDescNbVisitDesc();

        return books.stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> get8BestRatedInProgressBooks() {
        List<Book> books = bookRepository.findTop8ByIsCompletedFalseOrderByNoteDescNbVisitDesc();

        return books.stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    private List<BookDto> get8InProgressBookByUser(Long userId) {
        Pageable pageable = PageRequest.of(0, 8, JpaSort.unsafe("random()"));;
        List<Book> books = bookRepository.findUserBookInProgress(userId, UserBookStatus.READING, pageable);

        return books.stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public BookDto getBookDetailsForUser(Long bookId, Long userId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        BookDto bookDto = bookMapper.toDto(book);

        Set<AuthorDto> authors = bookDto.getAuthors();
        List<Long> authorIds = authors.stream().map(AuthorDto::getId).collect(Collectors.toList());

        List<BookDto> sameAuthorBooks = bookRepository.findByAuthorIds(authorIds).stream()
                .map(bookMapper::toDto)
                .filter(b -> !b.getId().equals(bookId)) // Filtrer le livre actuel
                .collect(Collectors.toList());
        Set<BookDto> sameAuthorBooksSet = new HashSet<>(sameAuthorBooks);
        bookDto.setSameAuthorBooks(sameAuthorBooksSet);

        setUserBookInfo(userId, bookId, bookDto);

        return bookDto;
    }

    public BookDto setUserBookInfo(Long userId, Long bookId, BookDto bookDto){
        Optional<UserBookInfo> userBookInfo = userBookInfoService.getUserBookInfo(userId, bookId);

        Random random = new Random();
        double randomDouble = 65 + (100 - 65) * random.nextDouble();
        BigDecimal randomBigDecimal = BigDecimal.valueOf(randomDouble).setScale(2, BigDecimal.ROUND_HALF_UP);
        bookDto.setUserMatch(randomBigDecimal);

        if(userBookInfo.isPresent()) {
            UserBookInfo userBookInfo1 = userBookInfo.get();
            bookDto.setUserStatus(userBookInfo1.getStatus());
            bookDto.setUserRating(userBookInfo1.getNote());
            bookDto.setUserCurrentVolume(userBookInfo1.getCurrentVolume());
        }

        return bookDto;
    }

    public List<BookDto> getBooksByTags(List<TagDto> tags) {
        if(tags.isEmpty()) {
            return bookRepository.findTop20ByOrderByNbVisitDesc().stream()
                    .map(bookMapper::toDto)
                    .collect(Collectors.toList());
        }

        List<String> tagIds = tags.stream().map(TagDto::getName).collect(Collectors.toList());

        List<Book> books = bookRepository.findByTags(tagIds, (long) tagIds.size());

        return books.stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> getRecentBooks() {
        List<Book> books = bookRepository.findTop20ByOrderByDateStartDesc();

        return books.stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public void switchBookInLibrary(Long bookId, AppUser user) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));

        userBookInfoService.switchBookInLibrary(book, user);
    }

    public void updateUserBookInfo(BookDto bookDto, AppUser user) {
        Optional<UserBookInfo> userBookInfoDb = userBookInfoService.getUserBookInfo(user.getId(), bookDto.getId());

        if(userBookInfoDb.isPresent()) {
            UserBookInfo userBookInfo = userBookInfoDb.get();
            userBookInfo.setNote(bookDto.getUserRating());
            userBookInfo.setStatus(bookDto.getUserStatus());
            userBookInfo.setCurrentVolume(bookDto.getUserCurrentVolume());
            userBookInfoService.save(userBookInfo);
        } else {
            UserBookInfo userBookInfo = UserBookInfo.builder()
                    .appUser(user)
                    .book(bookRepository.findById(bookDto.getId()).get())
                    .note(bookDto.getUserRating())
                    .status(bookDto.getUserStatus())
                    .currentVolume(bookDto.getUserCurrentVolume())
                    .build();
            userBookInfoService.save(userBookInfo);
        }
    }

    public BookDto findByFrenchSearchName(String name) {
        Optional<Book> book = bookRepository.findByFrenchSearchName(name);
        return book.map(bookMapper::toDto).orElse(null);
    }

    public BookDto searchBookByFrenchTitles(List<String> cleanedTitles) {
        for (String title : cleanedTitles) {
            Optional<Book> book = bookRepository.findByFrenchSearchName(title);
            if (book.isPresent()) {
                return bookMapper.toDto(book.get());
            }
        }
        return null;
    }

    public List<BookDto> getBooksByUser(Long id) {
        List<Book> books = bookRepository.findBooksByUser(id);
        List<BookDto> bookDtos = books.stream().map(bookMapper::toDto).toList();

        List<UserBookInfo> userBookInfos = userBookInfoService.getUserBookInfos(id);
        Map<Long, UserBookInfo> userBookInfoMap = userBookInfos.stream().collect(Collectors.toMap(ubi -> ubi.getBook().getId(), ubi -> ubi));

        for (BookDto bookDto : bookDtos) {
            UserBookInfo userBookInfo = userBookInfoMap.get(bookDto.getId());
            if (userBookInfo != null) {
                bookDto.setUserStatus(userBookInfo.getStatus());
                bookDto.setUserRating(userBookInfo.getNote());
                bookDto.setUserCurrentVolume(userBookInfo.getCurrentVolume());
            }
        }

        return bookDtos;
    }

    public List<BookDto> search(String search) {
        List<Book> books = bookRepository.search(search);

        return books.stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }
}

package com.libra_s.libraS.service;

import com.libra_s.libraS.domain.Book;
import com.libra_s.libraS.domain.enums.UserBookStatus;
import com.libra_s.libraS.dtos.BookDto;
import com.libra_s.libraS.dtos.DiscoverPageDto;
import com.libra_s.libraS.dtos.mapper.BookMapper;
import com.libra_s.libraS.repository.BookRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
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
        List<Book> books = bookRepository.findTop8ByOrderByModifiedAtDesc();

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
}

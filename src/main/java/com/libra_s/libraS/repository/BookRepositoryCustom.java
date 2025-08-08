package com.libra_s.libraS.repository;

import com.libra_s.libraS.domain.Book;
import com.libra_s.libraS.dtos.BookFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookRepositoryCustom {
    Page<Book> findBooksWithFilters(BookFilterDto filter, Pageable pageable);
    List<Book> findBooksWithFilters(BookFilterDto filter);
}

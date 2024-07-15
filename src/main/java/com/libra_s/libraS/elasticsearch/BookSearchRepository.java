package com.libra_s.libraS.elasticsearch;

import com.libra_s.libraS.domain.Book;
import com.libra_s.libraS.domain.elasticsearch.BookIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface BookSearchRepository extends ElasticsearchRepository<BookIndex, Long> {

    List<BookIndex> findByFrenchSearchName(String frenchSearchName);

    List<BookIndex> findByFrenchSearchName(List<String> names);
}
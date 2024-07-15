package com.libra_s.libraS.elasticsearch.custom.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.libra_s.libraS.domain.elasticsearch.BookIndex;
import com.libra_s.libraS.elasticsearch.custom.BookIndexCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class BookIndexCustomRepositoryImpl implements BookIndexCustomRepository {

    private final ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    public BookIndexCustomRepositoryImpl(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public List<BookIndex> searchByFrenchSearchName(String searchText) {
        NativeQuery searchQuery = new NativeQueryBuilder()
                .withQuery(MatchQuery.of("frenchSearchName", searchText))
                .build();

        SearchHits<BookIndex> searchHits = elasticsearchTemplate.search(searchQuery, BookIndex.class);

        return searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }
}

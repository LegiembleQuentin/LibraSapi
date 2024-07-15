package com.libra_s.libraS.elasticsearch.custom;

import com.libra_s.libraS.domain.elasticsearch.BookIndex;

import java.util.List;

public interface BookIndexCustomRepository {
    List<BookIndex> searchByFrenchSearchName(String searchText);
}

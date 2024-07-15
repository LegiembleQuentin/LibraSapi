package com.libra_s.libraS.domain.elasticsearch;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "books")
public class BookIndex {

    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String frenchSearchName;
}


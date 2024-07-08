package com.libra_s.libraS.domain;

import com.libra_s.libraS.domain.converter.ArrayListConverter;
import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book")
@Data
@EqualsAndHashCode(of = "id")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    @NotNull
    @Column(nullable = false)
    @Convert(converter = ArrayListConverter.class)
    private List<String> names;

    @NotNull
    @Column(nullable = false)
    private LocalDate dateStart;

    private LocalDate dateEnd;

    @NotNull
    @Column(nullable = false)
    private int nbVolume;

    @NotNull
    @Column(nullable = false)
    private int nbVisit = 0;

    private BigDecimal note;

    @NotNull
    @Column(nullable = false)
    private boolean isCompleted = false;

    private String externalId;

    @ManyToMany
    @JoinTable(
            name = "book_tag",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    private String imgUrl;

    @ManyToMany
    @JoinTable(
            name = "book_author",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "related_books",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "related_book_id")
    )
    private Set<Book> relatedBooks = new HashSet<>();

    @NotNull
    @Column(nullable = false)
    private LocalDate createdAt;

    private LocalDate modifiedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifiedAt = LocalDate.now();
    }

    
}

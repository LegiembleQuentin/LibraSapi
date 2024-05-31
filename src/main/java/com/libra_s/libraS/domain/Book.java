package com.libra_s.libraS.domain;

import com.libra_s.libraS.domain.converter.ArrayListConverter;
import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

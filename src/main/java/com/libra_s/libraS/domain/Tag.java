package com.libra_s.libraS.domain;

import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tag")
@Data
@EqualsAndHashCode(of = "id")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Book> books = new HashSet<>();
}

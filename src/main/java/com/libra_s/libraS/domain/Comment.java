package com.libra_s.libraS.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comment")
@Data
@EqualsAndHashCode(of = "id")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    private float note;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}

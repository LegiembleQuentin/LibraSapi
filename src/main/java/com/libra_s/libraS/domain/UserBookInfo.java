package com.libra_s.libraS.domain;

import com.libra_s.libraS.domain.enums.UserBookStatus;
import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_book_info")
@Data
@EqualsAndHashCode(of = "id")
public class UserBookInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id", nullable = false)
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private int note;

    private UserBookStatus status;

    private Integer currentVolume;
}

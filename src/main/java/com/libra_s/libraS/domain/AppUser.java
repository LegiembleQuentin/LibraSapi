package com.libra_s.libraS.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")
@Data
@EqualsAndHashCode(of = "id")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String fname;
    private String displayname;
    private String img_url;
    private String password;

//    @ElementCollection
//    private List<String> roles;

    private LocalDateTime created_at;
    private LocalDateTime modified_at;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Book> books;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Comment> comments;
}

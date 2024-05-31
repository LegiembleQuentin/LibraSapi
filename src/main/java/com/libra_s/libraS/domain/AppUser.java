package com.libra_s.libraS.domain;

import com.libra_s.libraS.domain.converter.RoleListConverter;
import com.libra_s.libraS.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;


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

    @NotNull
    private String name;
    private String fname;
    private String displayname;
    private String img_url;
    private String password;

    @Convert(converter = RoleListConverter.class)
    private List<Role> roles;

    private LocalDateTime created_at;
    private LocalDateTime modified_at;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Book> books;
//
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;
}

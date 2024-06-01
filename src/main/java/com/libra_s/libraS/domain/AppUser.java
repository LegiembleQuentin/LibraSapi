package com.libra_s.libraS.domain;

import com.libra_s.libraS.domain.converter.RoleListConverter;
import com.libra_s.libraS.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private String fname;

    @NotNull
    @Column(nullable = false)
    private String displayname;

    private String img_url;

    @NotNull
    @Column(nullable = false)
    private String password;

    @NotNull
    @Column(nullable = false)
    @Convert(converter = RoleListConverter.class)
    private List<Role> roles;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime created_at;

    private LocalDateTime modified_at;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @ManyToMany(mappedBy = "likedByUsers")
    private Set<Comment> likedComments = new HashSet<>();
}

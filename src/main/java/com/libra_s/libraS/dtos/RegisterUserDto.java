package com.libra_s.libraS.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RegisterUserDto {
    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String displayname;
}

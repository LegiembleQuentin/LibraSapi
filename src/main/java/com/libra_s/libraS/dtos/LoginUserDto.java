package com.libra_s.libraS.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginUserDto {

    @NotNull
    private String email;

    @NotNull
    private String password;
}

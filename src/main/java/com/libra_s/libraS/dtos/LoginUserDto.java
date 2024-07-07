package com.libra_s.libraS.dtos;

import javax.validation.constraints.NotNull;

public class LoginUserDto {

    @NotNull
    private String email;

    @NotNull
    private String password;
}

package com.libra_s.libraS.rest;

import com.libra_s.libraS.domain.AppUser;
import com.libra_s.libraS.dtos.LoginResponseDto;
import com.libra_s.libraS.dtos.LoginUserDto;
import com.libra_s.libraS.dtos.RegisterUserDto;
import com.libra_s.libraS.service.AuthenticationService;
import com.libra_s.libraS.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<LoginResponseDto> register(@RequestBody RegisterUserDto registerUserDto) {
        AppUser registeredUser = authenticationService.signup(registerUserDto);

        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail(registerUserDto.getEmail());
        loginUserDto.setPassword(registerUserDto.getPassword());
        AppUser authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponseDto loginResponse = new LoginResponseDto();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> authenticate(@RequestBody LoginUserDto loginUserDto) {
        AppUser authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponseDto loginResponse = new LoginResponseDto();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}

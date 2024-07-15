package com.libra_s.libraS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@EnableJpaRepositories(basePackages = "com.libra_s.libraS.repository")
public class LibraSApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraSApplication.class, args);
	}
}

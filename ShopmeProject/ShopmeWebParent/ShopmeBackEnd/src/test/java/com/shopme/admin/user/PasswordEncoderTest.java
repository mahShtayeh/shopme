package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
	@Test
	public void testEncodePassword() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); 
		
		String rowPassword = "password"; 
		String encodedPassword = encoder.encode(rowPassword); 
		
		System.out.println(encodedPassword); 
		
		boolean matches = encoder.matches(rowPassword, encodedPassword); 
		
		assertThat(matches).isTrue(); 
	}
}

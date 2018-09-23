/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import sample.web.CustomAuthorizationRequestResolver;

/**
 * @author Joe Grandja
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;

	// @formatter:off
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/assets/**", "/webjars/**").permitAll()
				.anyRequest().authenticated()
				.and()
			.oauth2Login()
				.loginPage("/oauth2/authorization/messaging")
				.failureUrl("/login?error")
				.permitAll()
				.authorizationEndpoint()
					.authorizationRequestResolver(customAuthorizationRequestResolver())
					.and()
				.and()
			.logout()
				.logoutSuccessUrl("http://localhost:8090/uaa/logout.do?client_id=messaging&redirect=http://localhost:8080")
				.and()
			.oauth2Client();
	}
	// @formatter:on

	private OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver() {
		return new CustomAuthorizationRequestResolver(this.clientRegistrationRepository);
	}
}
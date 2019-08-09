package org.insanedevelopment.hass.influx.gatherer.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);
		http.rememberMe()
				.key("influx-db-gatherer")
				.rememberMeCookieName("influx-db-gatherer-remember-me")
				.userDetailsService(userDetailsService);
	}

}

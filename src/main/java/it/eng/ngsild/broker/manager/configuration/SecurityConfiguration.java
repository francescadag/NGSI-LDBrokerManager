package it.eng.ngsild.broker.manager.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private UserDetailsService uds;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		// DICIAMO A SPRING quali path sono da proteggere e quali no
		http.authorizeRequests()
		.antMatchers("/admin").hasRole("ADMIN")
		.antMatchers("/admin/**").hasRole("ADMIN")
		.antMatchers("/").permitAll()
		.and().formLogin()
		.loginPage("/login")
		.and().csrf().disable(); // questo permetti alla API di ADMIN di funzionare. è un controllo avanzato che va 
								// disabilitato per sicurezza per evitare accessi indesiderati alle nostre API
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(uds);
	}

}

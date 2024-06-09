package com.generation.blogpessoal.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class BasicSecurityConfig {

    @Autowired
    private JwtAuthFilter authFilter; //Filtro JWT para autenticação de tokens
    //ajustes usuario e senha - Define o serviço que carregará os detalhes do usuário a partir do banco de dados
    @Bean
    UserDetailsService userDetailsService() {

        return new UserDetailsServiceImpl();
    }
    //criptografia da senha PasswordEncoder
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    //conseguiu validar usuario e senha
    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
    //implementar gerenciamento e autenticação
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    //Configura a cadeia de filtros de segurança
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	//Define as regras de autorização para as requisições HTTP
    	http
	        .sessionManagement(management -> management
	                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        		.csrf(csrf -> csrf.disable())
	        		.cors(withDefaults());

    	http
	        .authorizeHttpRequests((auth) -> auth
	                .requestMatchers("/usuarios/logar").permitAll() //Permite acesso público ao endpoint de login
	                .requestMatchers("/usuarios/cadastrar").permitAll() //Permite acesso público ao endpoint de cadastro
	                .requestMatchers("/usuarios").permitAll()
	                .requestMatchers("/temas/{id}").permitAll()
	                .requestMatchers("/temas").permitAll()
	                .requestMatchers("/temas/descricao/{descricao}").permitAll()
	                .requestMatchers("/postagens").permitAll()
	                .requestMatchers("/postagens/{id}").permitAll()
	                .requestMatchers("/postagens/titulo/{titulo}").permitAll()
	                .requestMatchers("/error/**").permitAll() //Permite acesso público aos endpoints de erro
	                .requestMatchers(HttpMethod.OPTIONS).permitAll() //Permite todas as requisições OPTIONS
	                .anyRequest().authenticated()) //Requer autenticação para qualquer outra requisição
	        .authenticationProvider(authenticationProvider()) //Configura o provedor de autenticação
	        .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
	        .httpBasic(withDefaults());

		return http.build();

    }

}
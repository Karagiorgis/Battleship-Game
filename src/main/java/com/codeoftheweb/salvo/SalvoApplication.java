package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}


	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("*")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
						.allowedHeaders("*", "Access-Control-Allow-Headers", "origin", "Content-type", "accept", "x-requested-with", "x-requested-by") //What is this for?
						.allowCredentials(true);
			}
		};
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository,
									  GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository,
									  ShipRepository shipRepository,
									  SalvoRepository salvoRepository,
									  ScoreRepository scoreRepository,
									  PasswordEncoder passwordEncoder) {
		return (args) -> {
			Player player1 = new Player("Jack@gmail.com", passwordEncoder.encode("test"));
			playerRepository.save(player1);
			Player player2 = new Player("Chloe@gmail.com",passwordEncoder.encode("test"));
			playerRepository.save(player2);
			Player player3 = new Player("Kim@gmail.com",passwordEncoder.encode("test"));
			playerRepository.save(player3);
			Player player4 = new Player("David@gmail.com",passwordEncoder.encode("test"));
			playerRepository.save(player4);
			Player player5 = new Player("Michelle@gmail.com",passwordEncoder.encode("test"));
			playerRepository.save(player5);


			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			Date newDate = Date.from(date.toInstant().plusSeconds(3600));
			Date newDate2 = Date.from(date.toInstant().plusSeconds(7200));


			Game game1 = new Game(formatter.format(date).toString());
//			Game game2 = new Game(formatter.format(newDate).toString());
//			Game game3 = new Game(formatter.format(newDate2).toString());
//
			gameRepository.save(game1);
//			gameRepository.save(game2);
//			gameRepository.save(game3);
//
			GamePlayer gamePlayer1 = new GamePlayer(player1,game1);
			gamePlayerRepository.save(gamePlayer1);
			GamePlayer gamePlayer2 = new GamePlayer(player2,game1);
			gamePlayerRepository.save(gamePlayer2);
//			GamePlayer gamePlayer3 = new GamePlayer(player3,game3);
//			gamePlayerRepository.save(gamePlayer3);
//			GamePlayer gamePlayer4 = new GamePlayer(player2,game1);
//			gamePlayerRepository.save(gamePlayer4);
//			GamePlayer gamePlayer5 = new GamePlayer(player5,game2);
//			gamePlayerRepository.save(gamePlayer5);
//			GamePlayer gamePlayer6 = new GamePlayer(player2,game3);
//			gamePlayerRepository.save(gamePlayer6);
//
			Ship ship1 = new Ship("Aircraft-Carrier", Arrays.asList("H2", "H3", "H4", "H5", "H6"),gamePlayer1);
			shipRepository.save(ship1);
			Ship ship2 = new Ship("Battleship", Arrays.asList("A10", "B10", "C10", "D10"),gamePlayer1);
			shipRepository.save(ship2);
			Ship ship3 = new Ship("Submarine", Arrays.asList("D1", "D2", "D3"),gamePlayer1);
			shipRepository.save(ship3);
			Ship ship4 = new Ship("Destroyer", Arrays.asList("J2", "J3", "J4"),gamePlayer1);
			shipRepository.save(ship4);
			Ship ship5 = new Ship("Patrol-Boat", Arrays.asList("J7", "J8"),gamePlayer1);
			shipRepository.save(ship5);

			Ship ship6 = new Ship("Aircraft-Carrier", Arrays.asList("H2", "H3", "H4", "H5", "H6"),gamePlayer2);
			shipRepository.save(ship6);
			Ship ship7 = new Ship("Battleship", Arrays.asList("A10", "B10", "C10", "D10"),gamePlayer2);
			shipRepository.save(ship7);
			Ship ship8 = new Ship("Submarine", Arrays.asList("D1", "D2", "D3"),gamePlayer2);
			shipRepository.save(ship8);
			Ship ship9 = new Ship("Destroyer", Arrays.asList("J2", "J3", "J4"),gamePlayer2);
			shipRepository.save(ship9);
			Ship ship10 = new Ship("Patrol-Boat", Arrays.asList("J7", "J8"),gamePlayer2);
			shipRepository.save(ship10);
//
//			Salvo salvo1 = new Salvo(gamePlayer1, 1, Arrays.asList("H2", "H3", "H4"));
//			salvoRepository.save(salvo1);
//			Salvo salvo2 = new Salvo(gamePlayer1, 2, Arrays.asList("C8", "E4", "D4", "C2"));
//			salvoRepository.save(salvo2);
//			Salvo salvo3 = new Salvo(gamePlayer1, 3, Arrays.asList("A2", "A3"));
//			salvoRepository.save(salvo3);
//			Salvo salvo4 = new Salvo(gamePlayer1, 4, Arrays.asList("F3", "F4"));
//			salvoRepository.save(salvo4);
//
//			Salvo salvo5 = new Salvo(gamePlayer4, 1, Arrays.asList("A1", "B1", "C2", "C3"));
//			salvoRepository.save(salvo5);
//			Salvo salvo6 = new Salvo(gamePlayer4, 2, Arrays.asList("J6", "J2", "E4", "D2"));
//			salvoRepository.save(salvo6);
//			Salvo salvo7 = new Salvo(gamePlayer4, 3, Arrays.asList("H2", "H3", "H4"));
//			salvoRepository.save(salvo7);
//			Salvo salvo8 = new Salvo(gamePlayer4, 4, Arrays.asList("D3", "F4"));
//			salvoRepository.save(salvo8);
//
//
//			String date5 = new Date().toString();
//
//			Score score1 = new Score(date5, 0.5, player1,game1);
//			scoreRepository.save(score1);
//			Score score2 = new Score(date5, 1, player2,game1);
//			scoreRepository.save(score2);
//			Score score3 = new Score(date5, 0, player2,game2);
//			scoreRepository.save(score3);
//			Score score4 = new Score(date5, 1, player5,game2);
//			scoreRepository.save(score4);
		};

	};
}


@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName-> {
			Player player = playerRepository.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});
	}
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.cors();

		http.authorizeRequests()
				.antMatchers("/**").permitAll()
//				.anyRequest().hasAuthority("USER")
				.and()
				.formLogin();

		http.formLogin()
				.usernameParameter("userName")
				.passwordParameter("password")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");
		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}
}
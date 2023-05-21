package com.sixtymeters.homelab.security;

import com.sixtymeters.homelab.exception.HomelabBaseException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static java.util.Map.entry;

@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableMethodSecurity
public class DevelopmentSecurityConfig {

    //TODO: move to actual oauth2 config later
    public static final String OAUTH_LOGIN_URL = "/oauth2/authorization/oauth-login";
    public static final AntPathRequestMatcher API_BASE_PATH_MATCHER = new AntPathRequestMatcher("/backend/api/**");

    public static final String LOGIN_PROCESSING_URL = "/perform_login";

    @Value("${homelab.config.routing.frontend-url:/}")
    private String frontendUrl;
    private final Environment environment;

    @Value("${homelab.config.security.open-endpoints}")
    private String[] unauthenticatedPaths;

    @Bean
    @Primary
    public InMemoryUserDetailsManager userDetailsService() {
        var userDev = User.withUsername("dev")
                .password(passwordEncoder().encode("dev"))
                .authorities("TEST_USER") // at least 1 authority is needed, otherwise it won't start
                .build();

        // additional users could be added here ...
        return new InMemoryUserDetailsManager(userDev);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //TODO: rename with actual login name
    @Bean(name = "oauth-login")
    public SecurityFilterChain devLoginConfiguration(HttpSecurity http) throws Exception {
        verifyThatDevelopmentSecurityConfigIsNotUsedOnProduction();

        // Configure Spring Form Login to behave like an OAuth login provider
        http.formLogin(c -> c
                .loginPage(OAUTH_LOGIN_URL)
                .loginProcessingUrl(LOGIN_PROCESSING_URL)
                .successHandler(this::handleAuthenticationSuccess)
                .permitAll());
        http.apply(new AdditionalFormLoginConfigurer())
                .loginPage(OAUTH_LOGIN_URL)
                .loginProcessingUrl(LOGIN_PROCESSING_URL);

        // Enables csrf support configured for angular. This means POSTs need to include an X-XSRF-TOKEN header.
        var cookieCsrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        cookieCsrfTokenRepository.setCookiePath("/");

        // See https://docs.spring.io/spring-security/reference/5.8/migration/servlet/exploits.html#_defer_loading_csrftoken
        var tokenRequestHandler = new CsrfTokenRequestAttributeHandler();
        tokenRequestHandler.setCsrfRequestAttributeName(null);
        http.csrf(c ->
                c.csrfTokenRepository(cookieCsrfTokenRepository)
                        .csrfTokenRequestHandler(tokenRequestHandler));

        // Matching unauthenticated requests -> 401, not matching -> redirect to login (default)
        http.exceptionHandling(e -> e.defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                API_BASE_PATH_MATCHER));

        http.authorizeHttpRequests(c -> c
                .requestMatchers(unauthenticatedPaths).permitAll()
                .anyRequest().authenticated());

        http.logout(c -> c
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutSuccessHandler(this::handleLogoutSuccess));

        return http.build();
    }

    private void verifyThatDevelopmentSecurityConfigIsNotUsedOnProduction() {
        if(Arrays.asList(environment.getActiveProfiles()).contains("prod")){
            throw new HomelabBaseException(
                    "DevelopmentSecurityConfig is not allowed to be used on a production environment");
        }
        log.warn("DevelopmentSecurityConfig is active. Logins are mocked via FormLogin and don't use the real Authenticator.");
    }

    private void handleAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                             Authentication authentication) throws IOException {
        var authToken = createFakeOAuth2Token(authentication.getName());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        response.sendRedirect(frontendUrl);
        log.info("User {} has logged in successfully via Development Form Login", getUsername(authentication));
    }

    private OAuth2AuthenticationToken createFakeOAuth2Token(String username) {
        var user = new OAuth2User() {
            @Override
            public Map<String, Object> getAttributes() {
                return Map.ofEntries(
                        entry(SecurityContextServiceImpl.USERNAME_ATTRIBUTE, username),
                        entry(SecurityContextServiceImpl.EMAIL_ATTRIBUTE, "username@sixtymeters.com"),
                        entry(SecurityContextServiceImpl.FIRST_NAME_ATTRIBUTE, "firstname"),
                        entry(SecurityContextServiceImpl.LAST_NAME_ATTRIBUTE, "lastname")
                );
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.emptySet();
            }

            @Override
            public String getName() {
                return username;
            }
        };
        return new OAuth2AuthenticationToken(user, user.getAuthorities(), "fake-authentication-client-registration");
    }

    private void handleLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info(String.format("User %s has logged out successfully.", getUsername(authentication)));
    }

    private String getUsername(Authentication authentication){
        // Read directly from token instead of via SecurityContextHelper to also work in case of logout,
        // when the SecurityContextHolder is already purged
        return authentication.getName();
    }
}

package project.seatsence.global.config.security;

import static project.seatsence.global.constants.Constants.SwaggerPatterns;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;
import project.seatsence.global.config.filter.JwtFilter;
import project.seatsence.src.user.service.UserDetailsServiceImpl;

/** Spring Security 환경 설정 클래스 사용자에 대한 '인증'과 '인가'의 구성을 Bean으로 주입 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private JwtProvider jwtProvider;
    @Autowired private JwtFilter jwtFilter;

    @Autowired private UserDetailsServiceImpl userDetailsServiceImpl;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //    @Bean
    //    public WebSecurityCustomizer webSecurityCustomizer() {
    //        return web ->
    // web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    //    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin().disable().csrf().disable();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .mvcMatchers(SwaggerPatterns)
                .permitAll()
                .requestMatchers(CorsUtils::isPreFlightRequest)
                .permitAll()
                .antMatchers("/v1/users/validate/**", "/v1/users/sign-in", "/v1/users/sign-up")
                .permitAll()
                .antMatchers("/v1/admins/validate/**", "/v1/admins/sign-in", "/v1/admins/sign-up")
                .permitAll()
                .anyRequest()
                .authenticated();
        return http.build();
    }

    //        @Bean
    //        public AuthenticationManager authenticationManager() {
    //            return new ProviderManager(authenticationProvider());
    //        }
    //
    //        @Bean
    //        public AuthenticationProvider authenticationProvider() {
    //            return new CustomAuthenticationProvider(userDetailsService,
    // bCryptPasswordEncoder);
    //        }
    //
    //        @Bean
    //        public CustomAuthenticationFilter customAuthenticationFilter() {
    //            CustomAuthenticationFilter filter =
    //                    new CustomAuthenticationFilter(
    //                            new AntPathRequestMatcher("/api/v1/sign-in",
    // HttpMethod.POST.name()));
    //
    //            filter.setAuthenticationManager(authenticationManager());
    //            filter.setAuthenticationSuccessHandler(customSignInSuccessHandler());
    //            filter.setAuthenticationFailureHandler(customSignInFailureHandler());
    //            return filter;
    //        }
    //
    //        @Bean
    //        public CustomAuthSuccessHandler customSignInSuccessHandler() {
    //            return new CustomAuthSuccessHandler();
    //        }
    //
    //        @Bean
    //        public CustomAuthFailureHandler customSignInFailureHandler() {
    //            return new CustomAuthFailureHandler();
    //        }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

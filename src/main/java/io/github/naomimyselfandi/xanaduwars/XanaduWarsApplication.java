package io.github.naomimyselfandi.xanaduwars;

import io.github.naomimyselfandi.xanaduwars.util.NotCovered;
import jakarta.servlet.Filter;
import org.mapstruct.extensions.spring.SpringMapperConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@NotCovered
@EnableScheduling
@EnableWebSecurity
@SpringMapperConfig
@EnableMethodSecurity
@SpringBootApplication
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
class XanaduWarsApplication {

    public static void main(String[] args) {
        SpringApplication.run(XanaduWarsApplication.class, args);
    }

    @Bean
    @SuppressWarnings("unused")
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            @Qualifier("requestBodyCachingFilter") Filter requestBodyCachingFilter,
            @Qualifier("jwtAuthenticationFilter") Filter jwtAuthenticationFilter
    ) throws Exception {
        // Putting this here since it can't be meaningfully unit tested...
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/info/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(requestBodyCachingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}

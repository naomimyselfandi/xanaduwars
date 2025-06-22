package io.github.naomimyselfandi.xanaduwars;

import io.github.naomimyselfandi.xanaduwars.util.ExcludeFromCoverageReport;
import org.mapstruct.extensions.spring.SpringMapperConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableScheduling
@SpringMapperConfig
@EnableMethodSecurity
@SpringBootApplication
@EnableAspectJAutoProxy
@ExcludeFromCoverageReport
@ConfigurationPropertiesScan
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
class XanaduWarsApplication {

	public static void main(String[] args) {
		SpringApplication.run(XanaduWarsApplication.class, args);
	}

}

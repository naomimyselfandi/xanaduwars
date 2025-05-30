package io.github.naomimyselfandi.xanaduwars;

import io.github.naomimyselfandi.xanaduwars.ext.ExcludeFromCoverageReport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ExcludeFromCoverageReport
@ConfigurationPropertiesScan
class XanaduWarsApplication {

	public static void main(String[] args) {
		SpringApplication.run(XanaduWarsApplication.class, args);
	}

}

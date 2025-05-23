package io.github.naomimyselfandi.xanaduwars;

import io.github.naomimyselfandi.xanaduwars.ext.ExcludeFromCoverageReport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ExcludeFromCoverageReport
public class XanaduWarsApplication {

	public static void main(String[] args) {
		SpringApplication.run(XanaduWarsApplication.class, args);
	}

}

package com.sensedia.api.app;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfiguration {
	@Bean
	@RuntimeEnvironment
	public FlywayMigrationStrategy cleanMigrateStrategy() {
	    return new FlywayMigrationStrategy() {
	        @Override
	        public void migrate(Flyway flyway) {
	        	flyway.setBaselineOnMigrate(true);
	            flyway.setCleanOnValidationError(true);
	            flyway.setValidateOnMigrate(true);
	            flyway.repair();
	            flyway.migrate();
	        }
	    };
	}
}

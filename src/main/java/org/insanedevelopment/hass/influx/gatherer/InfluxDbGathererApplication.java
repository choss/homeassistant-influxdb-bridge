package org.insanedevelopment.hass.influx.gatherer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InfluxDbGathererApplication {

	public static void main(String[] args) {
		SpringApplication.run(InfluxDbGathererApplication.class, args);
	}

}

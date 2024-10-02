package com.stl.image_generator;

import com.stl.image_generator.view.RestApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RestApiProperties.class)
public class ImageGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageGeneratorApplication.class, args);
	}

}

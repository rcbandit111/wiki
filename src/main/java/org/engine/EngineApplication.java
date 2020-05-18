package org.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication(scanBasePackages = { "org.engine.*", "org.engine.production.service", "org.engine.warehouse.service" })
public class EngineApplication {

    public static void main(final String[] args)
    {
        SpringApplication.run(EngineApplication.class, args);
    }

}

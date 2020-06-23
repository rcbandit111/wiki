package org.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@EnableSwagger2WebMvc
@SpringBootApplication(scanBasePackages = { "org.engine.*", "org.engine.production.service", "org.engine.warehouse.service" })
public class EngineApplication {

    public static void main(final String[] args)
    {
        SpringApplication.run(EngineApplication.class, args);
    }

}

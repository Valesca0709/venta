package ventamicroservicio.venta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "ventamicroservicio")
@EnableJpaRepositories(basePackages = "ventamicroservicio.repository")
@ComponentScan(basePackages = "ventamicroservicio")
@EntityScan(basePackages = "ventamicroservicio.model")
public class VentaApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(VentaApplication.class, args);
    }
}
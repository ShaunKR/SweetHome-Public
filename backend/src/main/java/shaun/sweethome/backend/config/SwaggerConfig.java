package shaun.sweethome.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI sweetHomeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SweetHome API")
                        .description("식물 관리 + 가계부 API 문서")
                        .version("v1.0.0"));
    }
}

package project.seatsence.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @OpenAPIDefinition(
//        info =
//                @Info(
//                        title = "Seat Sence API 명세서",
//                        description = "실시간 자리 확인 및 좌석 관리 서비스 API 명세서",
//                        version = "v1"))
@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {
    //    @Bean
    //    public GroupedOpenApi seatSenceOpenApi() {
    //        String[] paths = {"api/v1/**"};
    //
    //        return GroupedOpenApi.builder()
    //                .group("실시간 자리 확인 및 좌석 관리 서비스 API v1")
    //                .pathsToMatch(paths)
    //                .build();
    //    }

    @Bean
    public OpenAPI openAPI(@Value("${springdoc.version}") String version) {

        Info info =
                new Info()
                        .title("Seat Sence API 명세서")
                        .version(version)
                        .description("실시간 자리 확인 및 좌석 관리 서비스 API 명세서") // 문서 설명
                        .contact(
                                new Contact()
                                        .name("benjamin(seat-sense)")
                                        .email("chosj1526@gmail.com"));

        String jwtSchemeName = "JWT TOKEN";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        Components components =
                new Components()
                        .addSecuritySchemes(
                                jwtSchemeName,
                                new SecurityScheme()
                                        .name(jwtSchemeName)
                                        .type(SecurityScheme.Type.HTTP) // HTTP 방식
                                        .scheme("Bearer")
                                        .bearerFormat("JWT")); // 토큰 형식을 지정하는 임의의 문자(Optional)

        return new OpenAPI().addSecurityItem(securityRequirement).components(components).info(info);
    }
}

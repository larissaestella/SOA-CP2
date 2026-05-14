package com.hotel.sishotel.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sishotelOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SisHotel — Sistema de Reserva de Hotel")
                        .version("1.0.0")
                        .description("""
                            **CP2 — Arquitetura Orientada a Serviço**

                            API REST para gestão do ciclo completo de reservas:
                            `CREATED → CHECKED_IN → CHECKED_OUT` ou `CREATED → CANCELED`

                            ### Regras de negócio implementadas
                            | # | Regra | HTTP |
                            |---|---|---|
                            | 1 | checkoutExpected > checkinExpected | 400 |
                            | 2 | Sem sobreposição de período (exceto CANCELED) | 409 |
                            | 3 | numGuests ≤ capacidade do quarto | 400 |
                            | 4 | FSM de status válido | 409 |
                            | 5 | Check-in somente na data prevista ou após | 422 |
                            | 6 | valorFinal = max(1, dias) × preçoDiária | — |
                            | 7 | Quarto com reservas ativas não é excluído fisicamente | 409 |

                            ### Banco de dados
                            H2 em memória. Acesse o console em [/h2-console](/h2-console)
                            - JDBC URL: `jdbc:h2:mem:sishotel`
                            - Usuário: `sa` | Senha: *(vazio)*
                            """));
    }
}

# CP 2 — Sistema de Reserva de Hotel | Arquitetura Orientada a Serviço

---

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.2.5 |
| Persistência | Spring Data JPA + Hibernate |
| Banco de dados | H2 (in-memory) |
| Migrações | Flyway |
| Validação | Jakarta Bean Validation |
| Documentação | SpringDoc OpenAPI 2 (Swagger UI) |
| Build | Maven 3.8+ |

---

## Arquitetura

```
┌──────────────────────────────────────────────┐
│           Controller (REST Layer)             │
│  GuestController / RoomController /           │
│  ReservationController                        │
│  • Recebe HTTP, valida DTOs, devolve respostas│
└───────────────────┬──────────────────────────┘
                    │ DTOs (Request / Response)
┌───────────────────▼──────────────────────────┐
│         Service / Domain Layer                │
│  GuestService / RoomService /                 │
│  ReservationService                           │
│  • Regras de negócio, FSM, cálculos           │
└───────────────────┬──────────────────────────┘
                    │ Entities (JPA)
┌───────────────────▼──────────────────────────┐
│         Repository / DAO Layer                │
│  GuestRepository / RoomRepository /           │
│  ReservationRepository                        │
│  • Spring Data JPA + queries JPQL             │
└───────────────────┬──────────────────────────┘
                    │
┌───────────────────▼──────────────────────────┐
│      H2 in-memory Database                    │
│      Gerenciado via Flyway (V1 + V2)          │
└──────────────────────────────────────────────┘
```

---

## Como executar

### Pré-requisitos
- Java 21+
- Maven 3.8+ (ou use `./mvnw`)

### Rodar a aplicação
```bash
git clone https://github.com/larissaestella/SOA-CP2.git
cd SOA-CP2
mvn spring-boot:run
```

### URLs disponíveis
| URL | Descrição |
|---|---|
| http://localhost:8080/swagger-ui.html | Swagger UI (testar a API) |
| http://localhost:8080/api-docs | OpenAPI JSON |
| http://localhost:8080/h2-console | Console H2 no navegador |

### Console H2
- **JDBC URL:** `jdbc:h2:mem:sishotel`
- **Usuário:** `sa`
- **Senha:** *(deixar vazio)*

---

## Banco de dados

### Migrações Flyway

| Arquivo | Conteúdo |
|---|---|
| `V1__init_schema.sql` | Tabelas `guests`, `rooms`, `reservations` + índices |
| `V2__seed_data.sql` | 2 hóspedes, 3 quartos, 1 reserva de exemplo |

Localização: `src/main/resources/db/migration/`

### Modelo de dados
```
guests                rooms                  reservations
──────                ─────                  ────────────
id (PK, UUID)         id (PK, UUID)          id (PK, UUID)
full_name             number (UNIQUE)        guest_id (FK)
document (UNIQUE)     type                   room_id (FK)
email (UNIQUE)        capacity               checkin_expected
phone                 price_per_night        checkout_expected
created_at            status (ATIVO|INATIVO) checkin_at
                                             checkout_at
                                             status (FSM)
                                             num_guests
                                             estimated_amount
                                             final_amount
                                             created_at
                                             updated_at
```

### Dados do seed (prontos para testar)
| Recurso | ID |
|---|---|
| Hóspede Ana Silva | `11111111-1111-1111-1111-111111111111` |
| Hóspede Bruno Souza | `22222222-2222-2222-2222-222222222222` |
| Quarto 101 STANDARD (cap. 2, R$250/noite) | `aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa` |
| Quarto 201 DELUXE (cap. 3, R$380/noite) | `bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb` |
| Quarto 301 SUITE (cap. 4, R$520/noite) | `cccccccc-cccc-cccc-cccc-cccccccccccc` |
| Reserva de exemplo (CREATED) | `99999999-9999-9999-9999-999999999999` |

---

## Endpoints

### Hóspedes `/api/guests`
| Método | URL | Descrição | Status |
|---|---|---|---|
| GET | `/api/guests` | Listar todos | 200 |
| GET | `/api/guests/{id}` | Buscar por ID | 200 / 404 |
| POST | `/api/guests` | Cadastrar | 201 / 400 / 409 |
| PUT | `/api/guests/{id}` | Atualizar | 200 / 400 / 404 / 409 |
| DELETE | `/api/guests/{id}` | Remover | 204 / 404 |

### Quartos `/api/rooms`
| Método | URL | Descrição | Status |
|---|---|---|---|
| GET | `/api/rooms` | Listar todos | 200 |
| GET | `/api/rooms/active` | Listar ativos | 200 |
| GET | `/api/rooms/{id}` | Buscar por ID | 200 / 404 |
| POST | `/api/rooms` | Cadastrar | 201 / 400 / 409 |
| PUT | `/api/rooms/{id}` | Atualizar | 200 / 400 / 404 / 409 |
| DELETE | `/api/rooms/{id}` | Desativar (soft-delete) | 204 / 404 / 409 |
| PATCH | `/api/rooms/{id}/activate` | Reativar | 200 / 404 |

### Reservas `/api/reservations`
| Método | URL | Descrição | Status |
|---|---|---|---|
| GET | `/api/reservations` | Listar todas | 200 |
| GET | `/api/reservations/{id}` | Buscar por ID | 200 / 404 |
| GET | `/api/reservations/guest/{guestId}` | Por hóspede | 200 / 404 |
| GET | `/api/reservations/room/{roomId}` | Por quarto | 200 / 404 |
| POST | `/api/reservations` | Criar reserva | 201 / 400 / 404 / 409 |
| PUT | `/api/reservations/{id}` | Atualizar datas | 200 / 400 / 404 / 409 |
| PATCH | `/api/reservations/{id}/checkin` | Check-in | 200 / 404 / 409 / 422 |
| PATCH | `/api/reservations/{id}/checkout` | Check-out | 200 / 404 / 409 |
| PATCH | `/api/reservations/{id}/cancel` | Cancelar | 200 / 404 / 409 |

---

## Regras de negócio

| # | Regra | Exceção | HTTP |
|---|---|---|---|
| 1 | `checkoutExpected > checkinExpected` | `InvalidDateRangeException` | 400 |
| 2 | Quarto sem sobreposição de período (exceto CANCELED) | `RoomUnavailableException` | 409 |
| 3 | `numGuests ≤ capacidade do quarto` | `CapacityExceededException` | 400 |
| 4 | FSM: CREATED→CHECKED_IN→CHECKED_OUT \| CREATED→CANCELED | `InvalidReservationStateException` | 409 |
| 5 | Check-in permitido somente na data prevista ou após | `CheckinWindowException` | 422 |
| 6 | `valorFinal = max(1, dias) × preçoDiária` | — | — |
| 7 | Quarto com reservas ativas → INATIVO (nunca excluído fisicamente) | `InvalidReservationStateException` | 409 |

---

## Tratamento de erros

Todos os erros retornam o mesmo payload:

```json
{
  "status": 409,
  "error": "RoomUnavailableException",
  "message": "Room is unavailable for the period 2026-06-10 to 2026-06-12.",
  "path": "/api/reservations",
  "timestamp": "2026-05-14T10:30:00",
  "fieldErrors": null
}
```

Para erros de validação (400), o campo `fieldErrors` é preenchido:
```json
{
  "status": 400,
  "error": "ValidationError",
  "message": "Request has validation errors. See fieldErrors.",
  "path": "/api/guests",
  "timestamp": "2026-05-14T10:30:00",
  "fieldErrors": [
    { "field": "email", "message": "email must be a valid address" }
  ]
}
```

---

## Exemplos cURL

### Criar hóspede
```bash
curl -X POST http://localhost:8080/api/guests \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Carlos Lima",
    "document": "11122233344",
    "email": "carlos@example.com",
    "phone": "+55-11-91234-5678"
  }'
```

### Criar quarto
```bash
curl -X POST http://localhost:8080/api/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "number": 401,
    "type": "SUITE",
    "capacity": 4,
    "pricePerNight": 650.00
  }'
```

### Criar reserva
```bash
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "guestId": "11111111-1111-1111-1111-111111111111",
    "roomId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb",
    "checkinExpected": "2026-07-10",
    "checkoutExpected": "2026-07-13",
    "numGuests": 2
  }'
```

### Fluxo completo com a reserva seed
```bash
# 1. Visualizar reserva seed
curl http://localhost:8080/api/reservations/99999999-9999-9999-9999-999999999999

# 2. Check-in (ajuste a data em V2 se necessário)
curl -X PATCH http://localhost:8080/api/reservations/99999999-9999-9999-9999-999999999999/checkin

# 3. Check-out
curl -X PATCH http://localhost:8080/api/reservations/99999999-9999-9999-9999-999999999999/checkout

# 4. (alternativa) Cancelar — só se ainda CREATED
curl -X PATCH http://localhost:8080/api/reservations/99999999-9999-9999-9999-999999999999/cancel
```

### Desativar quarto (soft-delete)
```bash
curl -X DELETE http://localhost:8080/api/rooms/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa
```

---

## ADRs

### ADR-01: H2 in-memory como banco de dados
**Contexto:** Facilitar execução local sem dependências externas.  
**Decisão:** H2 in-memory com console web habilitado (`/h2-console`). Zero configuração.  
**Consequência:** Dados são perdidos ao reiniciar. Flyway repopula automaticamente via V1+V2.

### ADR-02: Flyway para migrações versionadas
**Contexto:** O enunciado exige migrações versionadas e seeds.  
**Decisão:** V1 cria o schema, V2 insere dados iniciais. Flyway roda automaticamente na inicialização.  
**Consequência:** Histórico rastreável; fácil adição de V3, V4... para evoluções futuras.

### ADR-03: Soft-delete de quartos
**Contexto:** Regra 7 — quartos com reservas ativas não podem ser excluídos fisicamente.  
**Decisão:** `DELETE /rooms/{id}` muda status para `INATIVO` (retorna 409 se tiver reserva ativa).  
`PATCH /rooms/{id}/activate` reverte para `ATIVO`.  
**Consequência:** Integridade referencial garantida; histórico de reservas preservado indefinidamente.

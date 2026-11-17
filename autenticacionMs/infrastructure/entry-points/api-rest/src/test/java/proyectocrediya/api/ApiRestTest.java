package proyectocrediya.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.WebApplicationContext;
import proyectocrediya.model.usuario.Usuario;
import proyectocrediya.usecase.registrarusuario.RegistrarUsuarioUseCase;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TestConfig.class)
class ApiRestTest {

    @MockitoBean
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @Autowired
    private WebApplicationContext context;

    private RestTestClient client;

    @BeforeEach
    void setup() {
        client = RestTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void shouldRegisterUser() {
        when(registrarUsuarioUseCase.ejecutar(any(Usuario.class))).thenReturn(
                Usuario.builder()
                        .id(1L)
                        .nombres("Lilia")
                        .apellidos("Torres")
                        .correoElectronico("lilia@crediya.com")
                        .salarioBase(new BigDecimal("4500000"))
                        .rol("SOLICITANTE")
                        .build()
        );

        client.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"nombres\":\"Lilia\",\"apellidos\":\"Torres\","
                        + "\"correo_electronico\":\"lilia@crediya.com\","
                        + "\"salario_base\":4500000}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.correo_electronico").isEqualTo("lilia@crediya.com");
    }


    @Test
    void shouldReturnNotFoundForInvalidPath() {
        client.get()
                .uri("/api/v1/no-existe")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}

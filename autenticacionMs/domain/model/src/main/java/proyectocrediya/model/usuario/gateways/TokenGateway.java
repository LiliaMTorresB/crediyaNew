package proyectocrediya.model.usuario.gateways;

import proyectocrediya.model.usuario.Usuario;

public interface TokenGateway {
    String generarToken(Usuario usuario);
}


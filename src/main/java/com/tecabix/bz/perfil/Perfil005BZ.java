package com.tecabix.bz.perfil;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.entity.Perfil;
import com.tecabix.db.repository.PerfilRepository;
import com.tecabix.db.repository.UsuarioRepository;
import com.tecabix.res.b.RSB067;
import com.tecabix.sv.rq.RQSV076;

public class Perfil005BZ {

    private final PerfilRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;
    private final Catalogo eliminado;

    public Perfil005BZ(
            final PerfilRepository perfilRepository,
            final UsuarioRepository usuarioRepository,
            final Catalogo eliminado) {
        super();
        this.perfilRepository = perfilRepository;
        this.usuarioRepository = usuarioRepository;
        this.eliminado = eliminado;
    }

    public ResponseEntity<RSB067> eliminar(final RQSV076 rqsv076) {

        RSB067 response = rqsv076.getRsb067();

        Perfil perfil = perfilRepository.findByClave(rqsv076.getPerfil()).orElse(null);

        if(perfil == null) {
            return response.notFound("No existe el perfil");
        }

        Pageable pageable = PageRequest.of(0, 1);

        if(usuarioRepository.findByLikePerfil(perfil.getNombre(), pageable).hasContent()) {
            return response.conflict("El perfil tiene usuarios relacionados");
        }

        perfil.setEstatus(eliminado);
        perfil.setIdUsuarioModificado(rqsv076.getSesion().getUsuario().getId());
        perfil.setFechaDeModificacion(LocalDateTime.now());

        perfilRepository.save(perfil);

        return response.ok(perfil);
    }
}
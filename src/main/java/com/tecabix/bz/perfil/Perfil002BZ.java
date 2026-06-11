package com.tecabix.bz.perfil;

import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.tecabix.db.entity.Perfil;
import com.tecabix.db.repository.PerfilRepository;
import com.tecabix.res.b.RSB064;
import com.tecabix.sv.rq.RQSV073;

public class Perfil002BZ {

    private final PerfilRepository perfilRepository;

    public Perfil002BZ(final PerfilRepository perfilRepository) {
        super();
        this.perfilRepository = perfilRepository;
    }

    public ResponseEntity<RSB064> detalle(final RQSV073 rqsv073) {

        RSB064 response = rqsv073.getRsb064();

        Optional<Perfil> perfilOP = perfilRepository.findByClave(rqsv073.getPerfil());

        if(perfilOP.isEmpty()) {
            return response.notFound("No existe el perfil");
        }

        return response.ok(perfilOP.get());
    }
}
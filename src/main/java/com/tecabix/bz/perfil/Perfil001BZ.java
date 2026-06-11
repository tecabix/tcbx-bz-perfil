package com.tecabix.bz.perfil;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import com.tecabix.db.entity.Perfil;
import com.tecabix.db.repository.PerfilRepository;
import com.tecabix.res.b.RSB063;
import com.tecabix.sv.rq.RQSV072;

public class Perfil001BZ {

    private final PerfilRepository perfilRepository;

    public Perfil001BZ(final PerfilRepository perfilRepository) {
        super();
        this.perfilRepository = perfilRepository;
    }

    public ResponseEntity<RSB063> listar(final RQSV072 rqsv072) {

        RSB063 response = rqsv072.getRsb063();

        Pageable pageable = PageRequest.of(
                0,
                Integer.MAX_VALUE,
                Sort.by("nombre")
        );

        Page<Perfil> perfiles = perfilRepository.findByActivo(pageable);

        return response.ok(perfiles);
    }
}
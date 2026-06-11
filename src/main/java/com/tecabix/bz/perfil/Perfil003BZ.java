package com.tecabix.bz.perfil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.tecabix.db.entity.Autorizacion;
import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.entity.Perfil;
import com.tecabix.db.repository.AutorizacionRepository;
import com.tecabix.db.repository.CatalogoRepository;
import com.tecabix.db.repository.PerfilRepository;
import com.tecabix.res.b.RSB065;
import com.tecabix.sv.rq.RQSV074;

public class Perfil003BZ {

    private final PerfilRepository perfilRepository;
    private final CatalogoRepository catalogoRepository;
    private final AutorizacionRepository autorizacionRepository;
    private final Catalogo activo;

    public Perfil003BZ(
            final PerfilRepository perfilRepository,
            final CatalogoRepository catalogoRepository,
            final AutorizacionRepository autorizacionRepository,
            final Catalogo activo) {
        super();
        this.perfilRepository = perfilRepository;
        this.catalogoRepository = catalogoRepository;
        this.autorizacionRepository = autorizacionRepository;
        this.activo = activo;
    }

    public ResponseEntity<RSB065> crear(final RQSV074 rqsv074) {

        RSB065 response = rqsv074.getRsb065();

        if(perfilRepository.findByNombre(rqsv074.getNombre()).isPresent()) {
            return response.conflict("Ya existe un perfil con el mismo nombre");
        }

        Catalogo tipo = catalogoRepository.findById(rqsv074.getTipo()).orElse(null);

        if(tipo == null) {
            return response.notFound("No existe el tipo de perfil");
        }

        List<Autorizacion> autorizaciones = new ArrayList<>();

        for(UUID autorizacionClave : rqsv074.getAutorizaciones()) {

            Autorizacion autorizacion = autorizacionRepository
                    .findByClave(autorizacionClave)
                    .orElse(null);

            if(autorizacion == null) {
                return response.notFound("No existe una autorización del perfil");
            }

            autorizaciones.add(autorizacion);
        }

        Perfil perfil = new Perfil();
        perfil.setNombre(rqsv074.getNombre());
        perfil.setDescripcion(rqsv074.getDescripcion());
        perfil.setTipo(tipo);
        perfil.setAutorizaciones(autorizaciones);
        perfil.setIdUsuarioModificado(rqsv074.getSesion().getUsuario().getId());
        perfil.setFechaDeModificacion(LocalDateTime.now());
        perfil.setEstatus(activo);
        perfil.setClave(UUID.randomUUID());

        perfilRepository.save(perfil);

        return response.ok(perfil);
    }
}
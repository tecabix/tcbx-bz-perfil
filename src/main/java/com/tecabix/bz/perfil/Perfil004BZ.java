package com.tecabix.bz.perfil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.tecabix.db.entity.Autorizacion;
import com.tecabix.db.entity.Catalogo;
import com.tecabix.db.entity.Perfil;
import com.tecabix.db.repository.AutorizacionRepository;
import com.tecabix.db.repository.CatalogoRepository;
import com.tecabix.db.repository.PerfilRepository;
import com.tecabix.res.b.RSB066;
import com.tecabix.sv.rq.RQSV075;

public class Perfil004BZ {

    private final PerfilRepository perfilRepository;
    private final CatalogoRepository catalogoRepository;
    private final AutorizacionRepository autorizacionRepository;

    public Perfil004BZ(
            final PerfilRepository perfilRepository,
            final CatalogoRepository catalogoRepository,
            final AutorizacionRepository autorizacionRepository) {
        super();
        this.perfilRepository = perfilRepository;
        this.catalogoRepository = catalogoRepository;
        this.autorizacionRepository = autorizacionRepository;
    }

    public ResponseEntity<RSB066> actualizar(final RQSV075 rqsv075) {

        RSB066 response = rqsv075.getRsb066();

        Perfil perfil = perfilRepository.findByClave(rqsv075.getPerfil()).orElse(null);

        if(perfil == null) {
            return response.notFound("No existe el perfil");
        }

        Optional<Perfil> perfilNombreOP = perfilRepository.findByNombre(rqsv075.getNombre());

        if(perfilNombreOP.isPresent()
                && !perfilNombreOP.get().getClave().equals(perfil.getClave())) {
            return response.conflict("Ya existe un perfil con el mismo nombre");
        }

        Catalogo tipo = catalogoRepository.findById(rqsv075.getTipo()).orElse(null);

        if(tipo == null) {
            return response.notFound("No existe el tipo de perfil");
        }

        List<Autorizacion> autorizaciones = new ArrayList<>();

        for(UUID autorizacionClave : rqsv075.getAutorizaciones()) {

            Autorizacion autorizacion = autorizacionRepository
                    .findByClave(autorizacionClave)
                    .orElse(null);

            if(autorizacion == null) {
                return response.notFound("No existe una autorización del perfil");
            }

            autorizaciones.add(autorizacion);
        }

        perfil.setNombre(rqsv075.getNombre());
        perfil.setDescripcion(rqsv075.getDescripcion());
        perfil.setTipo(tipo);
        perfil.setAutorizaciones(autorizaciones);
        perfil.setIdUsuarioModificado(rqsv075.getSesion().getUsuario().getId());
        perfil.setFechaDeModificacion(LocalDateTime.now());

        perfilRepository.save(perfil);

        return response.ok(perfil);
    }
}
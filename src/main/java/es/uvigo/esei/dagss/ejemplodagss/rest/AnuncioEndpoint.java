package es.uvigo.esei.dagss.ejemplodagss.rest;

import es.uvigo.esei.dagss.ejemplodagss.daos.AnuncioDAO;
import es.uvigo.esei.dagss.ejemplodagss.entidades.Anuncio;
import java.net.URI;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path(value = "anuncios")
@Stateless
public class AnuncioEndpoint {

    @Context
    private UriInfo uriInfo;

    @Inject
    private AnuncioDAO anuncioDAO;

    public AnuncioEndpoint() {
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response buscarAnuncios() {
        List<Anuncio> anuncios = anuncioDAO.buscarTodos();

        // Necesario para devolver Listas como entidades XML/JSON
        GenericEntity<List<Anuncio>> entidadXML = new GenericEntity<List<Anuncio>>(anuncios) {/*vacio*/};

        return Response.ok(entidadXML).build();
    }

    @POST
    @Consumes(value = {MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response crearAnuncio(Anuncio anuncio) {
        try {
            Anuncio nuevoAnuncio = anuncioDAO.crear(anuncio);
            URI nuevoAnuncioURI = uriInfo.getAbsolutePathBuilder().path(nuevoAnuncio.getId().toString()).build();
            return Response.created(nuevoAnuncioURI).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GET
    @Produces(value = {MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("{id:[0-9]+}")
    public Response buscarAnuncio(@PathParam("id") Long id) {
        Anuncio anuncio = anuncioDAO.buscarPorId(id);
        if (anuncio != null) {
            return Response.ok(anuncio).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Path("{id:[0-9]+}")
    public Response actualizarAnuncio(Anuncio anuncio) {
        try {
            Anuncio nuevoAnuncio = anuncioDAO.actualizar(anuncio);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("{id:[0-9]+}")
    public Response borrarAnuncio(@PathParam("id") Long id) {
        Anuncio anuncio = anuncioDAO.buscarPorId(id);
        if (anuncio != null) {
            anuncioDAO.borrar(anuncio);
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}

package es.uvigo.esei.dagss.ejemplodagss.rest;

import es.uvigo.esei.dagss.ejemplodagss.daos.AnuncioDAO;
import es.uvigo.esei.dagss.ejemplodagss.daos.NickDAO;
import es.uvigo.esei.dagss.ejemplodagss.entidades.Anuncio;
import es.uvigo.esei.dagss.ejemplodagss.entidades.Nick;
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

@Path(value = "nicks")
@Stateless
public class NickEndpoint {

    @Context
    private UriInfo uriInfo;

    @Inject
    private NickDAO nickDAO;

    @Inject
    private AnuncioDAO anuncioDAO;

    @GET
    @Produces(value = {MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response buscarNicks() {
        List<Nick> autores = nickDAO.buscarTodos();
        // Necesario para devolver Listas como entidades XML/JSON
        GenericEntity<List<Nick>> entidadXML = new GenericEntity<List<Nick>>(autores) {/*vacio*/
        };
        return Response.ok(entidadXML).build();
    }

    @POST
    @Consumes(value = {MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response crearNick(Nick nick) {
        try {
            Nick nuevoNick = nickDAO.crear(nick);
            URI nuevoNickURI = uriInfo.getAbsolutePathBuilder().path(nuevoNick.getId().toString()).build();
            return Response.created(nuevoNickURI).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GET
    @Produces(value = {MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("{id:[0-9]+}")
    public Response buscarNickPorId(@PathParam("id") Long id) {
        Nick nick = nickDAO.buscarPorId(id);
        if (nick != null) {
            return Response.ok(nick).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("{nick:[a-zA-Z][a-zA-Z0-9]*}")
    public Response buscarPorNick(@PathParam("nick") String nickStr) {
        Nick nick = nickDAO.buscarPorNick(nickStr);
        if (nick != null) {
            return Response.ok(nick).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Path("{id:[0-9]+}")
    public Response actualizarNick(Nick nick) {
        try {
            Nick nuevoNick = nickDAO.actualizar(nick);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("{id:[0-9]+}")
    public Response borrarNick(@PathParam("id") Long id) {
        Nick nick = nickDAO.buscarPorId(id);
        if (nick != null) {
            nickDAO.borrar(nick);
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("{id:[0-9]+}/anuncios")
    public Response buscarAnunciosPorNick(@PathParam("id") Long id) {
        Nick nick = nickDAO.buscarPorId(id);
        if (nick != null) {
            List<Anuncio> anuncios = anuncioDAO.buscarPorAutor(nick);
            // Necesario para devolver Listas como entidades XML/JSON
            GenericEntity<List<Anuncio>> entidadXML = new GenericEntity<List<Anuncio>>(anuncios) {/*vacio*/};
            return Response.ok(entidadXML).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}

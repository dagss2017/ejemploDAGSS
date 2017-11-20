package es.uvigo.esei.dagss.ejemplodagss.controladores;

import es.uvigo.esei.dagss.ejemplodagss.daos.AnuncioDAO;
import es.uvigo.esei.dagss.ejemplodagss.entidades.Anuncio;
import es.uvigo.esei.dagss.ejemplodagss.entidades.Comentario;
import es.uvigo.esei.dagss.ejemplodagss.entidades.Nick;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Named(value = "anuncioControlador")
@SessionScoped
public class AnuncioControlador implements Serializable {

    @Inject
    private AnuncioDAO anuncioDAO;

    @Inject
    private NickControlador nickControlador;

    private String textoBusqueda;
    private List<Anuncio> anuncios;

    private Anuncio anuncioActual;
    private String textoComentario;

    public String getTextoBusqueda() {
        return textoBusqueda;
    }

    public void setTextoBusqueda(String textoBusqueda) {
        this.textoBusqueda = textoBusqueda;
    }

    public List<Anuncio> getAnuncios() {
        return anuncios;
    }

    public void setAnuncios(List<Anuncio> anuncios) {
        this.anuncios = anuncios;
    }

    public Anuncio getAnuncioActual() {
        return anuncioActual;
    }

    public void setAnuncioActual(Anuncio anuncioActual) {
        this.anuncioActual = anuncioActual;
    }

    public String getTextoComentario() {
        return textoComentario;
    }

    public void setTextoComentario(String textoComentario) {
        this.textoComentario = textoComentario;
    }        

    public AnuncioControlador() {
    }

    @PostConstruct
    public void listaInicial() {
        Nick nick = nickControlador.getNickActual();
        anuncios = anuncioDAO.buscarPorAutor(nick);
    }

    public String doNuevoAnuncio() {
        anuncioActual = new Anuncio();
        anuncioActual.setAutor(nickControlador.getNickActual());
        return "nuevo_anuncio";
    }

    public String doVerTodos() {
        anuncios = anuncioDAO.buscarTodos();

        return "listado_anuncios";
    }

    public String doBuscarNick() {
        anuncios = anuncioDAO.buscarPorNick(textoBusqueda);
        return "listado_anuncios";
    }

    public String doBuscarTitulo() {
        anuncios = anuncioDAO.buscarPorTexto(textoBusqueda);
        return "listado_anuncios";
    }

    public String doVerDetalle(Anuncio anuncio) {
        anuncioActual = anuncio;
        // FALTAN COSAS
        return "detalle_anuncio";
    }

    public String doCancelarNuevoAnuncio() {
        return "listado_anuncios";
    }
    
    public String doGuardarNuevoAnuncio() {
        anuncioDAO.crear(anuncioActual);
        
        return "listado_anuncios";
    }
    
    public void eventoAnadirComentario() {
        Comentario nuevoComentario = new Comentario(textoComentario, anuncioActual, nickControlador.getNickActual());       
        anuncioActual = anuncioDAO.anadirComentario(anuncioActual, nuevoComentario);        
    }
    
    public void eventoEliminarComentario(Comentario comentario) {
        anuncioActual.getComentarios().remove(comentario);
        anuncioActual = anuncioDAO.actualizar(anuncioActual);
    }
}

package es.uvigo.esei.dagss.ejemplodagss.daos;

import es.uvigo.esei.dagss.ejemplodagss.entidades.Anuncio;
import es.uvigo.esei.dagss.ejemplodagss.entidades.Comentario;
import es.uvigo.esei.dagss.ejemplodagss.entidades.Nick;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class AnuncioDAO {
    @PersistenceContext(unitName = "ejemploDAGSS_PU")
    private EntityManager em;

    public Anuncio crear(Anuncio anuncio) {
        em.persist(anuncio);
        return anuncio;
    }

    public Anuncio actualizar(Anuncio anuncio) {
        return em.merge(anuncio);
    }

    public Anuncio anadirComentario(Anuncio anuncio, Comentario comentario) {
        anuncio.anadirComentario(comentario);
        return em.merge(anuncio);
    }

    public void borrar(Anuncio anuncio) {
        em.remove(em.merge(anuncio));
    }

    public Anuncio buscarPorId(Long id) {
        return em.find(Anuncio.class, id);
    }

    public List<Anuncio> buscarTodos() {
        TypedQuery<Anuncio> q = em.createQuery("SELECT a FROM Anuncio AS a", Anuncio.class);
        return q.getResultList();
    }

    public List<Anuncio> buscarPorAutor(Nick autor) {
        TypedQuery<Anuncio> q = em.createQuery("SELECT a FROM Anuncio AS a WHERE a.autor.id = :autor_id", Anuncio.class);
        q.setParameter("autor_id", autor.getId());
        return q.getResultList();
    }

    public List<Anuncio> buscarPorTexto(String patron) {
        TypedQuery<Anuncio> q = em.createQuery("SELECT a FROM Anuncio AS a "
                + "   WHERE (a.titulo LIKE :patronTitulo) OR "
                + "         (a.detalle LIKE :patronDetalle)", Anuncio.class);

        q.setParameter("patronTitulo", "%" + patron + "%");
        q.setParameter("patronDetalle", "%" + patron + "%");
        return q.getResultList();
    }

    public List<Anuncio> buscarPorNick(String patron) {
        TypedQuery<Anuncio> q = em.createQuery("SELECT a FROM Anuncio AS a WHERE a.autor.nick LIKE :patron", Anuncio.class);
        q.setParameter("patron", "%" + patron + "%");
        return q.getResultList();
    }
}

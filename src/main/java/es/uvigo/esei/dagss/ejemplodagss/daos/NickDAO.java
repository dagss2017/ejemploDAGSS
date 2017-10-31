package es.uvigo.esei.dagss.ejemplodagss.daos;

import es.uvigo.esei.dagss.ejemplodagss.entidades.Nick;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class NickDAO {
    @PersistenceContext(unitName = "ejemploDAGSS_PU")
    private EntityManager em;

    public Nick crear(Nick nick) {
        em.persist(nick);
        return nick;
    }

    public Nick actualizar(Nick nick) {
        return em.merge(nick);
    }

    public void borrar(Nick nick) {
        em.remove(em.merge(nick));
    }

    public Nick buscarPorId(Long id) {
        return em.find(Nick.class, id);
    }

    public List<Nick> buscarTodos() {
        TypedQuery<Nick> q = em.createQuery("SELECT n FROM Nick AS n", Nick.class);
        return q.getResultList();
    }

    public Nick buscarPorNick(String nick) {
        TypedQuery<Nick> q = em.createQuery("SELECT n FROM Nick AS n WHERE n.nick = :nick", Nick.class);
        q.setParameter("nick", nick);

        List<Nick> resultados = q.getResultList();

        if ((resultados != null) && (resultados.size() == 1)) {
            return resultados.get(0);  // Devuelve el encontrado
        } else {  // No encontrado (o con duplicados)
            return null;  // TODO lanzar excepcion
        }
    }

    public List<Nick> buscarPorNombre(String patron) {
        TypedQuery<Nick> q = em.createQuery("SELECT n FROM Nick AS n WHERE (n.nombre LIKE :patron)", Nick.class);
        q.setParameter("patron", "%" + patron + "%");
        return q.getResultList();
    }
}

package es.uvigo.esei.dagss.ejemplodagss.entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Anuncio implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String detalle;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    
    @ManyToOne
    private Nick autor;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "anuncio")
    private List<Comentario> comentarios;

    public Anuncio() {
        this.fecha = Calendar.getInstance().getTime();       
    }
   
    public Anuncio(String titulo, String detalle, Nick autor) {
        this.titulo = titulo;
        this.detalle = detalle;
        this.autor = autor;
        this.fecha = Calendar.getInstance().getTime();       
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Nick getAutor() {
        return autor;
    }

    public void setAutor(Nick autor) {
        this.autor = autor;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public void anadirComentario(Comentario comentario) {
        if (this.comentarios == null) {
            this.comentarios = new ArrayList<Comentario>();
        }
        comentario.setAnuncio(this);
        this.comentarios.add(comentario);
    }
    
    
    public void eliminarComentario(Comentario comentario) {
        if (this.comentarios != null) {
            this.comentarios.remove(comentario);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Anuncio other = (Anuncio) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Anuncio{" + "id=" + id + ", titulo=" + titulo + ", fecha=" + fecha + ", autor=" + autor + '}';
    }

}
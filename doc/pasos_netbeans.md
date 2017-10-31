[TOC]

## Previo
1. Instalar versión Java EE de Netbeas 8.2 desde [https://netbeans.org/downloads/](https://netbeans.org/downloads/)
   * se instalará servidor de aplicaciones GlassFish 4.1.x
2. Copiar driver JDBC para MySQL
   ```
   cp $HOME/netbeans-8.2/ide/modules/ext/mysql-connector-java-5.1.23-bin.jar \
      $HOME/glassfish-4.1.1/glassfish/domains/domain1/lib/
   ```
3. Crear BD `pruebas_dagss` para ejemplo
   ```
   $ mysql -u root -p    [pedirá la contraseña de MySQL]

   mysql> create database pruebas_dagss;
   mysql> grant all privileges on pruebas_dagss.* to dagss@localhost identified by "dagss";
   mysql> exit
   ```

## Crear proyecto en Netbeans
Seleccionar `Menú File > New Project > Maven > Web Application`
   * Project name: `ejemploDAGSS`
   * Group ID: `es.uvigo.esei.dagss`
   * Version: `1.0-SNAPSHOT`
   * Package: `es.uvigo.esei.dagss.ejemplodagss`
   * Server: GlassFish 4.1.1
   * Java EE version: Java EE Web


## Configurar conexión a BD en GlassFish

### Crear recurso (datasource) JDBC y pool de conexiones
Sobre el proyecto `ejemploDAGSS`: `[botón derecho] > New > Other > GlassFish > JDBC Resource`
   * Seleccionar _Create New JDBC Connection Pool_
   * JNDI Name: `jdbc/ejemploDAGSS_resource` + [botón Next]
   * En _Choose Database Connection_
       * JDBC Connection Pool Name : `ejemploDAGSS_pool`
       * Seleccionar _New Configuration using Database_ con `MySQL (MM MySQL Driver)` + [botón Next]
            * Datasource Classname: `com.mysql.jdbc.jdbc2.optional.MysqlDataSource
            * Resource Type: `javax.sql.Datasource
            * Properties:
                * URL: `jdbc:mysql://localhost:3306/pruebas_dagss?relaxAutoCommit=true`
                * User: `dagss`
                * Password: `dagss`
            * Opciones por defecto en _Specify optional properties for Connection Pool_ + [botón Finish]

En `ejemploDAGSS > Web Pages > WEB-INF` se creará el fichero de configuración `glassFishResources.xml`


### Crear contexto de persistencia (persistence unit)
Sobre el proyecto `ejemploDAGSS`:  `[botón derecho] > New > Other > Persistence > Persistence Unit`
   * Persistence Unit Name: `ejemploDAGSS_PU`
   * Persistence Provider: `EclipseLink (JPA 2.1)`
   * Datasource: `java:app/jdbc/ejemploDAGSS_resource`
   * Habilitar _Use Java Transaction API_
   * Seleccionar `Create` como _Table Generation Strategy_

En `ejemploDAGSS > Other Sources > src/main/resources > META-INF`se creará el fichero de configuración `persistence.xml`



## Crear entidades JPA
Sobre el proyecto `ejemploDAGSS`: `[botón derecho] > New > Other > Persistence > Entity Class`
   * Class Name: `Nick`
   * Package: `es.uvigo.esei.dagss.ejemplodagss.entidades`
   * Primary Key Type : `Long`

Repetir el proceso de creación de entidades JPA para las entidades `Anuncio` y `Comentario`

**Nota:** También es posible crear manualmente el _package_ Java  `es.uvigo.esei.dagss.ejemplodagss.entidades`  (desde `ejemploDAGSS > Source Packages [botón derecho] > New > Java Package` ) y después las clases `Nick`, `Anuncio`, `Comentario` (desde `ejemploDAGSS > Source Packages > esei.dagss.ejemplodagss.entidades [botón derecho] > New > Java Class`)

### Clase `Nick.java`
```java
@Entity
public class Nick implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nick;
    private String nombre;
    
    @Temporal(TemporalType.TIMESTAMP)   
    private Date creacion;

    public Nick() {
        this.creacion = Calendar.getInstance().getTime();
    }
       
    public Nick(String nick, String nombre) {
        this.nick = nick;
        this.nombre = nombre;
        this.creacion = Calendar.getInstance().getTime();
    }
...
}
```
* Añadir getters y setters para los atributos, `hashCode()`, `equals()` y `toString()`

### Clase `Anuncio.java`
```java
@Entity
@XmlRootElement
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
...
}
```
* Añadir getters y setters para los atributos, `hashCode()`, `equals()` y `toString()`

* Añadir los métodos `anadirComentario()` y `eliminarComentario()`
```java
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
```
### Clase `Comentario.java`
```java
@Entity
public class Comentario implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String texto;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    @XmlTransient  // Omite serializar el Anuncio
    @ManyToOne
    private Anuncio anuncio;

    @ManyToOne
    private Nick autor;

    public Comentario() {
        this.fecha = Calendar.getInstance().getTime();
    }

    public Comentario(String texto, Anuncio anuncio, Nick autor) {
        this.texto = texto;
        this.anuncio = anuncio;
        this.autor = autor;
        this.fecha = Calendar.getInstance().getTime();
    }
    ...
}
```
* Añadir getters y setters para los atributos, `hashCode()`, `equals()` y `toString()`

## Crear los EJB responsables de implementar los DAO que gestionarán las entidades
Sobre el proyecto `ejemploDAGSS`: `[botón derecho] > New > Other > Enterprise JavaBeans > Session Bean`
   * EJB Name: `NickDAO`
   * Package: `es.uvigo.esei.dagss.ejemplodagss.daos`
   * Session Type: `Stateless`

Repetir el proceso de creación del EJB para para la clase `AnuncioDAO`

**Nota:** También es posible crear manualmente el _package_ Java  `es.uvigo.esei.dagss.ejemplodagss.daos`  (desde `ejemploDAGSS > Source Packages [botón derecho] > New > Java Package` ) y después las clases `NickDAO` y `AnuncioDAO` (desde `ejemploDAGSS > Source Packages > esei.dagss.ejemplodagss.daos [botón derecho] > New > Java Class`)

### Clase `NickDAO.java`
```java
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
```

### Clase `AnuncioDAO.java`
```java
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
```

## Habilitar el framework JSF y el soporte de CDI

1. Habilitar la configuración de JSF
   Sobre el proyecto `ejemploDAGSS`: `[botón derecho] > Properties > Frameworks`
    * Seleccionar en _Used Frameworks_ : `Add > Java Server Faces`
        * _Libraries_ : Server Library : `JSF 2.2`
        * _Configuration_ : JSF Servlet URL Pattern : `/faces/*`
        * _Configuration_ : Preferred Page Language : `Facelets`
        * _Components_ > Seleccionar `PrimeFaces`

    En `ejemploDAGSS > Web Pages > WEB-INF` se creará el fichero de configuración `web.xml

2. Habilitar la configuración de CDI
   Sobre el proyecto `ejemploDAGSS`: `[botón derecho] > New > Other > Context and Dependence Injection > beans.xml (CDI Configuration File)`
      * File Name : `beans`
      * Folder: `WEB-INF`

    En `ejemploDAGSS > Web Pages > WEB-INF` se creará el fichero de configuración `beans.xml`


## Crear objetos de respaldo para JSF (Managed Beans de CDI)

Sobre el proyecto `ejemploDAGSS`: `[botón derecho] > New > Other > Java Server Faces > JSF Managed Bean`
   * Class Name: `NickControlador`
   * Package: `es.uvigo.esei.dagss.ejemplodagss.controladores`
   * Name: `nickControlador`
   * Score: `session`

También se puede crear manualmente el package `es.uvigo.esei.dagss.ejemplodagss.controladores` (desde `ejemploDAGSS > Source Packages [botón derecho] > New > Java Package` ) y después la clase `NickControlador` (desde `ejemploDAGSS > Source Packages > esei.dagss.ejemplodagss.controladores [botón derecho] > New > Java Class` )

### Clase `NickControlador.java`
```java
@Named(value = "nickControlador")
@SessionScoped
public class NickControlador implements Serializable {
    private List<Nick> nicks;
    private Nick nickActual;
    private String nombreNickBuscar;

    ...
}
```
* Añadir un constructor vacío y getters y setters para los atributos `nicks`, `nickActual` y `nombreNickBuscar`.

* Añadir un punto de injección para una referencia a un NickDAO
```java
    @Inject
    private NickDAO nickDAO;
```
* Añadir los siguientes métodos de inicialización
```java
    @PostConstruct
    public void inicializarDatosControlador() {
        nicks = nickDAO.buscarTodos();
        nickActual = inicializarNickActual();
    }

    private Nick inicializarNickActual() {
        Nick visitante = nickDAO.buscarPorNick("visitante");
        if (visitante == null) {  // No existe
            visitante = nickDAO.crear(new Nick("visitante", "Usuario Visitante (autocreado)"));
        }
        return visitante;
    }
```
* Añadir los métodos vinculados a las acciones sobre los componentes de la vista
```java
    public String doCrearNuevoNick() {
        this.nickActual = new Nick();
        return "nuevo_nick";
    }

    public String doCancelarNuevoNick() {
        inicializarNickActual();
        return "listado_nicks";
    }

    public String doGuardarNuevoNick() {
        nickDAO.crear(nickActual);
        nicks = nickDAO.buscarTodos(); // Refrescar listado
        return "listado_nicks";
    }
    
    public String doBuscarNombreNick() {
        nicks = nickDAO.buscarPorNombre(nombreNickBuscar);
        return "listado_nicks";
    }
    
    public String doSeleccionarNick(Nick nick) {
        nickActual = nick;
        return "listado_nicks";
    }
    
    public String doVerAnuncios(Nick nick) {
        nickActual = nick;
        return "listado_anuncios";
    }
```

## Crear las vistas JSF
Ajustar `index.xhtml`en `ejemploDAGSS > Web Pages` con el contenido

```xml
<?xml version='1.0' encoding='UTF-8' ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html">
    <h:head>
        <title>Ejemplo JSF (DAGSS-2017)</title>
    </h:head>
    <h:body>
        <h3> Ejemplo JSF (DAGSS-2017) </h3>

        <ul> 
            <li> <h:link outcome="listado_nicks" value="Gestión de Nicks" /> </li>
            <li> <h:link outcome="listado_anuncios" value="Gestión de Anuncios" /> </li>
        </ul>
    </h:body>
</html>

```


Sobre `ejemploDAGSS > Web Pages`: `[botón derecho] > New > Other > Java Server Faces > JSF Page`
   * File Name: `listado_nicks`
   * Options: marcar _Facelets_

Crear del mismo modo las vistas `nuevo_nick.xhtml` y `listado_anuncios.xhtml`

### Vista `listado_nicks.xhtml`
```xml
<?xml version='1.0' encoding='UTF-8' ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core">
    <h:head>
        <title>Gestión de Nicks</title>
    </h:head>
    <h:body>
        <h3> Gestión de Nicks </h3>

        <h:form>
            <h4> NICK ACTUAL </h4>
            <h:panelGrid columns="2" rendered="#{not empty nickControlador.nickActual}">
                <h:outputLabel value="Id: "/>
                <h:outputLabel value="#{nickControlador.nickActual.id}"/>

                <h:outputLabel value="Nick: "/>
                <h:outputLabel value="#{nickControlador.nickActual.nick}"/>

                <h:outputLabel value="Nombre: "/>
                <h:outputLabel value="#{nickControlador.nickActual.nombre}"/>

                <h:outputLabel value="F. creacion: "/>
                <h:outputLabel value="#{nickControlador.nickActual.creacion}">
                    <f:convertDateTime pattern="dd/MM/yyyy"/>
                </h:outputLabel>
            </h:panelGrid>

            <h:commandButton value="Crear nuevo Nick" action="#{nickControlador.doCrearNuevoNick()}" />

            <h4> BUSCAR NICKS </h4>
            <h:panelGrid columns="3">
                <h:outputLabel value="Buscar nick"/>
                <h:inputText value="#{nickControlador.nombreNickBuscar}"/>
                <h:commandButton value="Buscar" action="#{nickControlador.doBuscarNombreNick()}"/>
            </h:panelGrid>

            <h4> LISTADO de NICKS </h4>
            <h:dataTable value="#{nickControlador.nicks}"
                         var="nick">
                <h:column>
                    <f:facet name="header"> Id. </f:facet>
                    <h:outputLabel value="#{nick.id}"/>                                       
                </h:column>
                <h:column>
                    <f:facet name="header"> Nick </f:facet>
                    <h:commandLink value="#{nick.nick}"
                                   action="#{nickControlador.doSeleccionarNick(nick)}"/>
                </h:column>
                <h:column>
                    <f:facet name="header"> Nombre </f:facet>
                    <h:outputLabel value="#{nick.nombre}"/>                                       
                </h:column>
                <h:column>
                    <f:facet name="header"> F. creación </f:facet>
                    <h:outputLabel value="#{nick.creacion}">
                        <f:convertDateTime parent="dd/MM/yyyy"/>
                    </h:outputLabel>
                </h:column>
                <h:column>
                    <h:commandButton value="Seleccionar"
                                     action="#{nickControlador.doSeleccionarNick(nick)}"/>
                </h:column>
                <h:column>
                    <h:commandButton value="Ver anuncios"
                                     action="#{nickControlador.doVerAnuncios(nick)}"/>
                </h:column>
            </h:dataTable>

            <h:commandButton value="Volver" action="index"/>

        </h:form>

    </h:body>
</html>
```

### Vista `nuevo_nick.xhtml`
```xml
<?xml version='1.0' encoding='UTF-8' ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core">
    <h:head>
        <title>Nuevo Nick</title>
    </h:head>
    <h:body>
        <h3> Nuevo Nick </h3>

        <h:form>
            <h:panelGrid columns="3">
                <h:outputLabel value="Autor:" for="nick"/>
                <h:inputText id ="nick" value="#{nickControlador.nickActual.nick}" required="true"/>
                <h:message for="nick"/>

                <h:outputLabel value="Nombre:" for="nombre"/>
                <h:inputText id="nombre" value="#{nickControlador.nickActual.nombre}" required="true"/>
                <h:message for="nombre"/>

            </h:panelGrid>

            <h:commandButton immediate="true" value="Cancelar" action="#{nickControlador.doCancelarNuevoNick}" />
            <h:commandButton  value="Guardar" action="#{nickControlador.doGuardarNuevoNick}" />
        </h:form>
    </h:body>
</html>
```

## Vista `listado_anuncios.xhtml` incial
```xml
<?xml version='1.0' encoding='UTF-8' ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core">
    <h:head>
        <title>Gestión de Anuncios</title>
    </h:head>
    <h:body>
        <h3> Gestión de Anuncios </h3>
        <h:form>
            <p> Completar aquí </p>
            <ul>
                <li>Incluir un buscador de anuncios</li>
                <li>Incluir el listado de anuncios (inicalmente todos los del Nick actual, después el resultado de la búsqueda)</li>
            </ul>

            <h:commandButton value="Volver" action="index"/>
        </h:form>
    </h:body>
</html>
```
# ejemploDAGSS
Proyecto Java EE 7 de ejemplo para DAGSS 2017/18

# ESQUEMA
![Esquema del ejemplo](/doc/esquema.jpg?raw=true "Esquema del proyecto JSF + JAX-RS")


# PREVIO

* Instalación de NetBeans JEE con GlassFish (desde [https://netbeans.org/downloads/](https://netbeans.org/downloads/))
* Crear BD "pruebas_dagss" en MySQL
   ```
   $ mysql -u root -p    [pedirá la contraseña de MySQL]

   mysql> create database pruebas_dagss;
   mysql> grant all privileges on pruebas_dagss.* to dagss@localhost identified by "dagss";
   ```
* Copiar driver JDBC de MySQL en el directorio de librerias de GlassFish
   ```
   $ cp $HOME/netbeans-8.2/ide/modules/ext/mysql-connector-java-5.1.23-bin.jar \          
        $HOME/glassfish-4.1.1/glassfish/domains/domain1/lib/
   ```
* Descargar copia del proyecto desde GitHub
   ```
   git clone https://github.com/dagss2017/ejemploDAGSS.git
   ```

# DOCUMENTACION
* [Creación del proyecto con Netbeans paso a paso](/doc/pasos_netbeans.md)

# TAREAS
1. Descargar, ejecutar e inspeccionar el proyecto
2. Añadir las vistas para la visualización y gestión de los anuncios 
3. Añadir un API REST empleando JAX-RS

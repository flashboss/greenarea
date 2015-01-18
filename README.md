GREENAREA

PRE INSTALLATION:

- execute the deploy of mysql driver mysql-connector-java-5.1.34-bin.jar in wildfly 
  through the console in Runtime/Manage Deployments to work with mysql

- in the wildfly file $JBOSS_HOME/modules/system/layers/base/sun/jdk/main/module.xml add the row:

  <path name="com/sun/net/httpserver"/>

  With it you activate restlet. It is used by activiti 5.16 internally. If you cannot add it you must
  use activiti 5.15.1

- increase the timeout of transactions on $JBOSS_HOME/standalone/configuration/standalone-full.xml. Inside the module:
  <subsystem xmlns="urn:jboss:domain:transactions:2.0"> add at the end of the row: <coordinator-environment default-timeout="50300"/>

- if you use java 8 you must create a file in the /jre/lib directory of JVM named: jaxp.properties and add the row:

  javax.xml.accessExternalSchema = all

  or add the property -Djavax.xml.accessExternalSchema=all when you compile the projects that work with web service with maven


INSTALLATION:

mvn install -Pproduction ( use the current database)

mvn install -Pdevelopment ( clean all db data, except for activiti)

mvn install (start the production profile as default)

MYSQL CONFIGURATION:

connect to mysql with the command:

mysql -u root

increase the variable so the log of activiti works correctlyadding the row at the end of the file /usr/local/mysql/my.cnf

max_allowed_packet=33554432;

connect to mysql and verify the correct update of the variable with the command:

show variables;

/opt/glassfishv3/bin/asadmin -u admin -W PASSWORD undeploy olio-webapp
/opt/glassfishv3/bin/asadmin -u admin -W PASSWORD deploy --virtualservers server --contextroot olio-webapp --precompilejsp=true olio-webapp/target/olio-webapp.war

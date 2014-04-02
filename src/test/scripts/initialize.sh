cp /vagrant/src/test/asterisk/*.conf /etc/asterisk
/etc/init.d/asterisk restart

mysql --user=root --password=root < /vagrant/src/test/sql/create_db.sql
mysql --user=root --password=root < /vagrant/src/test/sql/provision_db.sql

/etc/init.d/tomcat stop
rm -rf /opt/apache-tomcat-7.0.52/webapps/ROOT
unzip /vagrant/target/blacktiger-2.0.0-SNAPSHOT.war -d /opt/apache-tomcat-7.0.52/webapps/ROOT
cp /vagrant/src/test/config/blacktiger.properties /opt/apache-tomcat-7.0.52
/etc/init.d/tomcat start
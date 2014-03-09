cp /vagrant/src/test/asterisk/*.conf /etc/asterisk
/etc/init.d/asterisk restart

mysql --user=root --password=root < /vagrant/src/test/sql/create_db.sql

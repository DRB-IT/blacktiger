import groovy.sql.Sql

def sql = Sql.newInstance("jdbc:mysql://192.168.50.2/telesal", "root", "root", "com.mysql.jdbc.Driver")
def key = "enckey";

//Create halls
for (i in 0..999) {
    def id = i.toString().padLeft(4, "0");
    def data = ["45", id,"Test City " + id,"","+450000" + id,"Test Admin","admin@test.dk","+451000" + id,"User Comment","Admin Comment","Admin",key];
    println "Create hall: " + data;
    sql.call("{call create_hall(?,?,?,?,?,?,?,?,?,?,?,?)}", data);
    
}

//Reset passwords
data = ["Test-12345", key];
println "Resetting passwords";
def affectedRows = sql.executeUpdate("update callers set sip_pw=AES_ENCRYPT(?, UNHEX(SHA(?)))", data);
println affectedRows + " passwords reset";
 

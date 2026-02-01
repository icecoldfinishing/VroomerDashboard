cd project
mvn clean package
docker build -t vroomer-tomcat .
docker run --rm -p 8888:8888 vroomer-tomcat
# Puis ouvre http://localhost:8888/
# Open http://localhost:8080/
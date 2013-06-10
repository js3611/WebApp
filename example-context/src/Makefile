CONTEXT =       ../webapps/ROOT
LIBDIR  =       $(CONTEXT)/WEB-INF/lib
#LIBDIRRESTFUL =	$(CONTEXT)/WEB-INF/lib/restful
CLSDIR  =       $(CONTEXT)/WEB-INF/classes
CLSDIRREST  =	$(CONTEXT)/WEB-INF/classes/restful
JARS    =       servlets.jar bean1.jar bean2.jar User.jar
CLASSES =       Simple.class Servlet_Postgres.class Login.class 
CLASSESREST = 	restful/Simple2.class restful/User.class 
CLASSPATH =	-cp /usr/lib/tomcat7/tomcat-7.0.29/lib/servlet-api.jar
MKBEAN =	/homes/dcw/bin/mkbean


all:    $(JARS) $(CLASSES) $(CLASSESREST)

clean:	/bin/rm -f $(JARS) $(CLASSES) $(CLASSESREST) $(CLSDIR)/* $(LIBDIR)/*

install:        $(JARS)  $(CLASSES) $(CLASSESREST)
	install -m600 $(JARS) $(LIBDIR)
	install -m600 $(CLASSES) $(CLSDIR)
	install -m600 $(CLASSESREST) $(CLSDIRREST)

servlets.jar:      $(CLASSES) $(CLASSESREST)
	jar cf servlets.jar $(CLASSES) $(CLASSESREST)

Simple.class:   Simple.java
	javac $(CLASSPATH) Simple.java

Servlet_Postgres.class:   Servlet_Postgres.java
	javac -cp /usr/lib/tomcat7/tomcat-7.0.29/lib/servlet-api.jar Servlet_Postgres.java

Login.class:	Login.java
	javac -cp /usr/lib/tomcat7/tomcat-7.0.29/lib/servlet-api.jar Login.java

restful/Simple2.class:   restful/Simple2.java restful/User.java
	javac $(CLASSPATH) restful/Simple2.java restful/User.java

restful/User.class:	restful/User.java
	javac restful/User.java

bean1.jar:	dcwbeans/bean1.class dcwbeans/bean1.mf 
	jar cfm bean1.jar dcwbeans/bean1.mf dcwbeans/bean1.class

dcwbeans/bean1.class:	dcwbeans/bean1.java
	javac -d . dcwbeans/bean1.java

bean2.jar:	dcwbeans/bean2.java
	$(MKBEAN) dcwbeans/bean2

User.jar:	restful/User.java
	$(MKBEAN) restful/User.java

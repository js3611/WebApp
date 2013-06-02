CONTEXT =	../../webapps/ROOT
DIR	=	$(CONTEXT)/WEB-INF/lib
JARS	=	bean1.jar
MKBEAN	=	/homes/dcw/bin/mkbean

all:	$(JARS)

clean:	/bin/rm -f $(JARS) */*.class

install:	$(JARS)
	install -m644 $(JARS) $(DIR)

bean1.jar:	bean1.java
	$(MKBEAN) bean1

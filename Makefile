JAVA   = java
JAVAC  = javac
JFLAGS = -g

PROG  ?= JBounce
SRCS   = ${PROG}.java\
         Ball.java\
         CapturedArea.java\
         Game.java\
         Grid.java\
         Sprite.java\
         StatusBar.java\
         Player.java\
         Wall.java\
         WallExtension.java

.SUFFIXES: .java .jar .class

all: ${PROG}.jar

${PROG}.jar: ${PROG}.class Manifest.txt
	jar cfm ${PROG}.jar Manifest.txt *.class

${PROG}.class: ${SRCS}
	${JAVAC} ${JFLAGS} ${SRCS}

clean:
	-rm *.jar *.class

test: ${PROG}.class
	${JAVA} -jar ${PROG}.jar

.PHONY: all clean

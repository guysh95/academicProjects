FLAGS = -ansi -Wall -Wextra -Werror -pedantic-errors
LIBS = -lm

all: main.o spmat.o
	gcc main.o spmat.o -o ex2 $(LIBS)
clean:
	rm -rf *.o ex2

spmat.o: spmat.c spmat.h
	gcc $(FLAGS) -c spmat.c

main.o: main.c spmat.h
	gcc $(FLAGS) -c main.c

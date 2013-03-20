#include <pthread.h>
#include <stdio.h>
#include <math.h>

int sequence[1000];
int currentIndex;
void *runner(void *param);

int main(int argc, char *argv[]) {
  pthread_t tid;
  pthread_attr_t attr;

  if (argc != 2) {
    fprintf(stderr, "usage: a.out <integer value>\n");
    return -1;
  }
  if (atoi(argv[1]) < 0) {
    fprintf(stderr, "%d must be >= 0\n", atoi(argv[1]));
    return -1;
  }

  pthread_attr_init(&attr);
  pthread_create(&tid, &attr, runner, argv[1]);
  pthread_join(tid, NULL);

  int i;
  for (i = 0; i < currentIndex; i++) {
    printf("%d\n", sequence[i]);
  }
}


void *runner(void *param) {
  int total = atoi(param);
  int a, b;
  
  if (total == 0) {
    return;
  }

  a = 0;
  sequence[0] = a;
  currentIndex = 1;

  if (total == 1) {
    return;
  }

  b = 1;
  sequence[1] = b;
  currentIndex = 2;

  while (currentIndex < total) {
    int newValue = a + b;
    sequence[currentIndex] = newValue;
    a = b;
    b = newValue;
    currentIndex++;
  }

  pthread_exit(0);
}


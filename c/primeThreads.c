#include <pthread.h>
#include <stdio.h>
#include <math.h>

int primes[1000];
int counter = 0;
void *runner(void *param);

int main(int argc, char *argv[]) {
  pthread_t tid;
  pthread_attr_t attr;

  if (argc != 2) {
    fprintf(stderr, "usage: a.out <integer value>\n");
    return -1;
  }
  if (atoi(argv[1]) < 2) {
    fprintf(stderr, "%d must be >= 2\n", atoi(argv[1]));
    return -1;
  }

  pthread_attr_init(&attr);
  pthread_create(&tid, &attr, runner, argv[1]);
  pthread_join(tid, NULL);

  int i;
  for (i = 0; i < counter; i++) {
    printf("%d\n", primes[i]);
  }
}

int isPrime(int target) {
  if (target == 2) {
    return 1;
  }

  int i;
  for (i = 2; i <= ceil(sqrt(target)); i++) {
    if (target % i == 0) {
      return 0;
    }
  }
        
  return 1;
}

void *runner(void *param) {
  int i;
  int max = atoi(param);
  for (i = 2; i <= max; i++) {
    if (isPrime(i)) {
      primes[counter] = i;
      counter++;
    }
  }

  pthread_exit(0);
}


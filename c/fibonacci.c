#include <sys/types.h>
#include <stdio.h>
#include <unistd.h>

int main() {
  int count;
  printf("Enter number of items to compute: ");

  scanf("%d",&count);
  pid_t pid = fork();
    
  if (pid < 0) {
    fprintf(stderr, "Fork failed.");
    return 1;
  }

  if (pid == 0) {
    // child process

    int previous = 0;
    int current = 1;
    int numberDone = 0;
    while (numberDone < count) {
      if (numberDone == 0) {
	printf("0");
      } else if (numberDone == 1) {
	printf("1");
      } else {
	int temp = previous + current;
	previous = current;
	current = temp;
	printf("%d", current);
      }

      printf("\n");
      numberDone++;
    }
  } else {
    wait(NULL);
    printf("Parent process: child complete.");
  }

  return 0;
}


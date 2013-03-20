#include <stdio.h>
#include <fcntl.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>

main() {
  printf("Enter file source: ");
  char fileSourceName[256];
  char *reply = fgets(fileSourceName, 256, stdin);

  // remove the terminal '\n' from the input
  char fileSourceNoNewline[256];
  int index = 0;
  while (reply[index] != '\n' && reply[index] != '\0') {
    fileSourceNoNewline[index] = reply[index];
    index++;
  }
  fileSourceNoNewline[index] = '\0';

  printf("You entered: %s\n", fileSourceNoNewline);

  char* fileNameStart = "/Users/masonwright/dropbox/OS/c-code/ch2/";
  char combinedInputName[512];
  strcpy(combinedInputName, fileNameStart);
  strcat(combinedInputName, fileSourceNoNewline);

  printf("Enter file destination: ");
  char fileDestinationName[256];
  reply = fgets(fileDestinationName, 256, stdin);

  // remove the terminal '\n' from the input
  char destinationNoNewline[256];
  index = 0;
  while (reply[index] != '\n' && reply[index] != '\0') {
    destinationNoNewline[index] = reply[index];
    index++;
  }
  destinationNoNewline[index] = '\0';

  printf("You entered: %s\n", destinationNoNewline);
 
  char combinedOutputName[512];
  strcpy(combinedOutputName, fileNameStart);
  strcat(combinedOutputName, destinationNoNewline);

  /*
    // this is the way to open a file without
    // using POSIX commands.
  FILE *file = fopen(combinedInputName, "r");
  if (file != NULL) {
    char line[128];
    fgets(line, 128, file);
    printf("%s", line);
    fclose(file);
  } else {
    perror(fileNameStart);
  }
  */
  
  int fileDescriptor = open(combinedInputName, O_RDONLY);
  if (fileDescriptor == -1 ) {
    printf("Error opening file.");
    return 1;
  }

  char inputText[256];
  int result = read(fileDescriptor, &inputText, 256);
  if (result == -1 ) {
    printf("Error reading input file.");
    close(fileDescriptor);
    return 1;
  }
  close(fileDescriptor);

  printf("File data:\n%s\n", inputText);

  fileDescriptor = open(combinedOutputName, O_WRONLY);
  if (fileDescriptor == -1 ) {
    printf("Error opening output file.");
    return 1;
  }

  result = write(fileDescriptor, inputText, 256);
  if (result == -1 ) {
    printf("Error writing to file.");
    close(fileDescriptor);
    return 1;
  }

  close(fileDescriptor);
  return 0;
}

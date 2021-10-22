#define  _POSIX_C_SOURCE 200809L
#include <stdio.h>
#include <sys/mman.h>
#include <fcntl.h>
#include <pthread.h>
#include <unistd.h>
#include <sys/stat.h>
#include <string.h>
#include <stdint.h>
#include <stdlib.h>
#include <time.h>

int filter_log();
typedef intptr_t ssize_t;
ssize_t getline(char **lineptr, size_t *n, FILE *stream);

int main(int argc,char *argv[]) {
	if(argc!=1){
		printf("This program must be named either trunc_log or filter_log\n");
		return 1;
	}

	if(strstr(argv[0],"trunc_log") != NULL){
		if (truncate("/data/data/com.whatsapp/files/Logs/whatsapp.log", 0) != 0){
			printf("Error truncating file: /data/data/com.whatsapp/files/Logs/whatsapp.log\n");
			return 1;
		}else{
			printf("File was truncated\n");
			return 0;
		}
	}
	if(strstr(argv[0],"filter_log") !=NULL){
		return filter_log();
	}
	printf("This program must be named either trunc_log or filter_log\n");
	return 1;
}

int filter_log(){
    FILE * whatsappFP;
    FILE * viewFP;
    char * line = NULL;
    size_t len = 0;
    ssize_t read;

    whatsappFP = fopen("/data/data/com.whatsapp/files/Logs/whatsapp.log", "r");
    if (whatsappFP == NULL) {
    	printf("Failure opening file: /data/data/com.whatsapp/files/Logs/whatsapp.log\n");
        return 1;
    }

    viewFP = fopen("/sdcard/.capture1.view", "w");
    if (viewFP == NULL) {
    	printf("Failure opening file: /sdcard/.capture1.view\n");
        return 1;
    }
    printf("Files were opened\n");
    while ((read = getline(&line, &len, whatsappFP)) != -1) {
    	if( strstr(line, "Remote:") ){
    		char * lineParsed = strstr(line, "Local: ");
        	printf("%s", lineParsed);
        	fputs(lineParsed, viewFP);
        	fputs("\n", viewFP);
    	}
    	if( strstr(line, "network medium type updated")) {
    		char * lineParsed = strstr(line, "Peer ");
        	printf("%s", lineParsed);
        	fputs(lineParsed, viewFP);
        	fputs("\n", viewFP);
    	}
    }
    printf("Closing file descriptors\n");
    fclose(whatsappFP);
    fclose(viewFP);
    if (line)
        free(line);
    return 0;
}


ssize_t getline(char **lineptr, size_t *n, FILE *stream) {
    size_t pos;
    int c;

    if (lineptr == NULL || stream == NULL || n == NULL) {
        errno = EINVAL;
        return -1;
    }

    c = getc(stream);
    if (c == EOF) {
        return -1;
    }

    if (*lineptr == NULL) {
        *lineptr = malloc(128);
        if (*lineptr == NULL) {
            return -1;
        }
        *n = 128;
    }

    pos = 0;
    while(c != EOF) {
        if (pos + 1 >= *n) {
            size_t new_size = *n + (*n >> 2);
            if (new_size < 128) {
                new_size = 128;
            }
            char *new_ptr = realloc(*lineptr, new_size);
            if (new_ptr == NULL) {
                return -1;
            }
            *n = new_size;
            *lineptr = new_ptr;
        }

        ((unsigned char *)(*lineptr))[pos ++] = c;
        if (c == '\n') {
            break;
        }
        c = getc(stream);
    }

    (*lineptr)[pos] = '\0';
    return pos;
}
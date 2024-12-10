#include <rpc/rpc.h>
#include "xdr.h"
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <dirent.h>
#include <sys/stat.h>
#include <string.h>

InfoFile *file_scan_1_svc(char **nomeFile, struct svc_req *rp) {
    static InfoFile infoResult;
    int fd;
    char c;
    infoResult.caratteri = 0;
    infoResult.parole = 0;
    infoResult.linee = 0;
    printf("chiamata effettuata");
    fd = open(*nomeFile, O_RDONLY);
    if(fd < 0){
        infoResult.caratteri = fd;
        infoResult.parole = fd;
        infoResult.linee = fd;
        perror("open fault");
        return (&infoResult);
    }
    printf("nome file ricevuto: %s\n", *nomeFile);
    //controllare se file di testo
    while(read(fd, &c, sizeof(char)) != 0){
        infoResult.caratteri++;
        if(c == ' ' || c == '\t') infoResult.parole++;
        else if(c == '\n'){infoResult.parole++; infoResult.linee++;}
    }
    printf("Caratteri: %i\n Parole: %i\n Linee: %i\n", infoResult.caratteri, infoResult.parole, infoResult.linee);
    close(fd);
    return (&infoResult);
}

Lista *dir_scan_1_svc(InputDir *inputDir, struct svc_req *rp) {
    static Lista lista;
    int i = 0;
    DIR *fd;
    struct dirent *dir_entry;
    struct stat buf;
    char path[1024]; // almeno il doppio di 255
    printf("chiamata effettuata\n");
    lista.numeroFile = 0;
    fd = opendir(inputDir->dir);
    if(fd == NULL){
        lista.numeroFile = -1;
        perror("opendir fault");
        return(&lista);
    }
    printf("Nome direttorio ricevuto: %s\n soglia: %i\n", inputDir->dir, inputDir->soglia);
    while((dir_entry = readdir(fd)) != NULL && lista.numeroFile < 8){
        strcpy(path, inputDir->dir);
        strcat(path, "/");
        strcat(path, dir_entry->d_name);
        if(stat(path, &buf) < 0){
            perror("stat fault");
        }
        //. e .. come trattarli? contare anche le directory?
        if(buf.st_size > inputDir->soglia){
            strcpy(lista.nomeFile[lista.numeroFile].lettera,dir_entry->d_name);
            lista.numeroFile++;
        }
    }
    printf("Numero file oltre soglia: %i\n", lista.numeroFile);
    for(i = 0; i < lista.numeroFile; i++){
        printf("Nome file: %s\n", lista.nomeFile[i].lettera);
    }
    closedir(fd);
    return (&lista);
}
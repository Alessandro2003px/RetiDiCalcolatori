#include <rpc/rpc.h>
#include "votaFattoreX.h"
#include <stdio.h>
#include <string.h>

int main(int args, char **argv){
    char *host;
    char com[2];
    CLIENT *c;
    Output *output;
    Input input;
    int i = 0;
    int *esito;

    if(args != 2){
        fprintf(stderr, "Usage: votaFattoreX host\n");
        exit(1);
    }
    host = argv[1];
    c = clnt_create(host, VOTAFATTOREXPROG, VOTAFATTOREXVERS, "udp");
    if(c == NULL){
        clnt_perror(c,host);
        exit(2);
    }
    printf("Premi (C) per classifica giudici, (V) per votare candidato, ^D per terminare\n");
    while(gets(com)!=NULL){
        if(strcmp(com,"C") == 0){
            output = classifica_giudici_1((void*)NULL, c);
            if(output == NULL){
                clnt_perror(c,host);
                exit(3);
            }
            for(i = 0; i<4; i++){
                printf("Giudice: %s, score: %d\n", output->giudice[i].nome, output->giudice[i].score);
            }
        }else if(strcmp(com,"V") == 0){
            printf("Inserisci nome candidato, ^D per terminare\n");
            while(gets(input.candidato)!=NULL){
                printf("Inserisci operazione (aggiunta | sottrazione), ^D per terminare\n");
                while(gets(input.operazione)!=NULL){
                    if(strcmp(input.operazione,"aggiunta") != 0
                    && strcmp(input.operazione,"sottrazione") != 0){
                        printf("operazione non ammessa\n");
                        printf("Inserisci operazione (aggiunta | sottrazione), ^D per terminare\n");
                        continue;
                    }else break;
                }
                esito = esprimi_voto_1(&input, c);
                if(esito == NULL){
                    clnt_perror(c,host);
                    exit(4);
                }
                if(*esito == -3){
                    printf("Candidato non presente\n");
                    printf("Inserisci nome candidato, ^D per terminare\n");
                    continue;
                }else if(*esito == -1){
                    printf("Non puoi levare voti al candidato scelto. Gia' a zero\n");
                    printf("Inserisci nome candidato, ^D per terminare\n");
                    continue;
                }else{
                    printf("Votato con successo\n");
                    break;
                }
            }
        }else{
            printf("Operazione non ammessa\n");
        }
        printf("Premi (C) per classifica giudici, (V) per votare candidato, ^D per terminare\n");
    }

    clnt_destroy(c);
    exit(0);
}
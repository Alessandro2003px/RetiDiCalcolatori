/* echo_client.c
 *	+include echo.h
 */

#include "xdr.h"
#include <rpc/rpc.h>
#include <stdio.h>
#define DIM 256

int main(int argc, char *argv[]) {

    CLIENT *cl;
    char  **echo_msg; // il risultato è un char*
    char   *server;
    char   *msg;

    char    select;
    InfoFile *infofile;
    InputDir inputdir;
    Lista *lista;

    if (argc < 2) {
        fprintf(stderr, "uso: %s host\n", argv[0]);
        exit(1);
    }

    server = argv[1];

    cl = clnt_create(server, REMOTE, REMOTEVERS, "udp");
    if (cl == NULL) {
        clnt_pcreateerror(server);
        exit(1);
    }   printf("--%s--\n");

    /* CORPO DEL CLIENT:
    /* ciclo di accettazione di richieste da utente ------- */
    msg = (char *)malloc(255*sizeof(char));
    int in;
    printf("scegli File(f)/Dir(d), EOF per terminare\n ");
    while (1==1) {
        scanf("%c",&select);
        if(select=='d'){
            printf("inserisci nome directory:\n");
            scanf("%s",msg);
            printf("inserisci intero per dimensione minima del file:\n");
            scanf("%d",&in);
            inputdir.soglia=in;
            inputdir.dir=msg;
            lista=dir_scan_1(&inputdir, cl);
            if (lista == NULL) {
                fprintf(stderr, "%s: %s restituisce una stringa nulla\n", argv[0], server);
                exit(1);
            } else if(&lista->numeroFile<0){
                printf("errore, numero file negativo");             
            } else {
                printf("ci sono %d file", &lista->numeroFile);
                for(int i=0;i<&lista->numeroFile;i++){
                    printf("%s",&lista->nomeFile[i]);
                }

            }
        }
        else if(select=='f'){
            printf("inserisci nome file:\n");
            //fflush(stdin);
            scanf("%*c");
            
            gets(msg);
            //fscanf("%s",msg);
            printf("--%s--\n", msg);
            fflush(stdout);
            infofile = file_scan_1(&msg, cl);
            printf("ciao\n");
            if (infofile == NULL) {
                fprintf(stderr, "%s: %s restituisce una stringa nulla\n", argv[0], server);
                exit(1);
            } else if(&infofile->caratteri<0) {
                printf("errore nello scan del file");
            } else {
                printf("il file ha:%d caratteri %d parole %d linee\n",infofile->caratteri,infofile->parole,infofile->linee);
            }
        }

        /* In questo caso abbiamo la stringa nulla. Si noti che potrebbe essere
           utilizzata per notificare una condizione di errore al livello applicativo
           dal server al client */
        
        printf("Dammi il messaggio (max 100 caratteri), EOF per terminare: ");

    } // while gets(msg)

    // Libero le risorse: memoria allocata con malloc e gestore di trasporto
    free(msg);
    clnt_destroy(cl);
    printf("Termino...\n");
    exit(0);
}

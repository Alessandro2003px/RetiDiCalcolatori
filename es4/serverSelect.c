#include <dirent.h>
#include <errno.h>
#include <fcntl.h>
#include <netdb.h>
#include <netinet/in.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
#include <sys/select.h>

#define DIM_BUFF 100
#define LENGTH_FILE_NAME 256
#define max(a, b) ((a) > (b) ? (a) : (b))

/*Funzione conteggio file in un direttorio*/
/********************************************************/
int conta_file(char *name)
{
    DIR *dir;
    struct dirent *dd;
    int count = 0;
    dir = opendir(name);
    if (dir == NULL)
        return -1;
    while ((dd = readdir(dir)) != NULL)
    {
        printf("Trovato il file %s\n", dd->d_name);
        count++;
    }
    /*Conta anche direttorio stesso e padre*/
    printf("Numero totale di file %d\n", count);
    closedir(dir);
    return count;
}
/********************************************************/
void gestore(int signo)
{
    int stato;
    printf("esecuzione gestore di SIGCHLD\n");
    wait(&stato);
}
/********************************************************/

int main(int argc, char **argv)
{
    int listenfd, connfd, udpfd, fd_file, nready, maxfdp1;
    const int on = 1;
    char buff[DIM_BUFF], name[LENGTH_FILE_NAME], packet[LENGTH_FILE_NAME];
    fd_set rset;
    int len, nread, nwrite, num, ris, port;
    struct sockaddr_in cliaddr, servaddr;

    /* CONTROLLO ARGOMENTI ---------------------------------- */
    if (argc != 2)
    {
        printf("Error: %s port\n", argv[0]);
        exit(1);
    }

    nread = 0;
    while (argv[1][nread] != '\0')
    {
        if ((argv[1][nread] < '0') || (argv[1][nread] > '9'))
        {
            printf("Terzo argomento non intero\n");
            exit(2);
        }
        nread++;
    }
    port = atoi(argv[1]);
    if (port < 1024 || port > 65535)
    {
        printf("Porta scorretta...");
        exit(2);
    }

    /* INIZIALIZZAZIONE INDIRIZZO SERVER E BIND ---------------------------- */
    memset((char *)&servaddr, 0, sizeof(servaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_addr.s_addr = INADDR_ANY;
    servaddr.sin_port = htons(port);
    printf("Server avviato\n");

    /* CREAZIONE SOCKET TCP ------------------------------------------------ */
    listenfd = socket(AF_INET, SOCK_STREAM, 0);
    if (listenfd < 0)
    {
        perror("apertura socket TCP ");
        exit(1);
    }
    printf("Creata la socket TCP d'ascolto, fd=%d\n", listenfd);

    if (setsockopt(listenfd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0)
    {
        perror("set opzioni socket TCP");
        exit(2);
    }
    printf("Set opzioni socket TCP ok\n");

    if (bind(listenfd, (struct sockaddr *)&servaddr, sizeof(servaddr)) < 0)
    {
        perror("bind socket TCP");
        exit(3);
    }
    printf("Bind socket TCP ok\n");

    if (listen(listenfd, 5) < 0)
    {
        perror("listen");
        exit(4);
    }
    printf("Listen ok\n");

    /* CREAZIONE SOCKET UDP ------------------------------------------------ */
    udpfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (udpfd < 0)
    {
        perror("apertura socket UDP");
        exit(5);
    }
    printf("Creata la socket UDP, fd=%d\n", udpfd);

    if (setsockopt(udpfd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0)
    {
        perror("set opzioni socket UDP");
        exit(6);
    }
    printf("Set opzioni socket UDP ok\n");

    if (bind(udpfd, (struct sockaddr *)&servaddr, sizeof(servaddr)) < 0)
    {
        perror("bind socket UDP");
        exit(7);
    }
    printf("Bind socket UDP ok\n");

    /* AGGANCIO GESTORE PER EVITARE FIGLI ZOMBIE -------------------------------- */
    signal(SIGCHLD, gestore);

    /* PULIZIA E SETTAGGIO MASCHERA DEI FILE DESCRIPTOR ------------------------- */
    FD_ZERO(&rset);
    maxfdp1 = max(listenfd, udpfd) + 1;

    /* CICLO DI RICEZIONE EVENTI DALLA SELECT ----------------------------------- */
    for (;;)
    {
        FD_SET(listenfd, &rset);
        FD_SET(udpfd, &rset);

        if ((nready = select(maxfdp1, &rset, NULL, NULL, NULL)) < 0)
        {
            if (errno == EINTR)
                continue;
            else
            {
                perror("select");
                exit(8);
            }
        }

        /* GESTIONE RICHIESTE DI GET DI UN FILE ------------------------------------- */
        if (FD_ISSET(listenfd, &rset))
        {
            printf("Ricevuta richiesta di get di un file\n");
            len = sizeof(struct sockaddr_in);
            if ((connfd = accept(listenfd, (struct sockaddr *)&cliaddr, &len)) < 0)
            {
                if (errno == EINTR)
                    continue;
                else
                {
                    perror("accept");
                    exit(9);
                }
            }

            if (fork() == 0)
            { /* processo figlio che serve la richiesta di operazione */
                close(listenfd);
                int n;
                printf("Dentro il figlio, pid=%i\n", getpid());
                /* non c'e' piu' il ciclo perche' viene creato un nuovo figlio */
                /* per ogni richiesta di file */
                if (read(connfd, &name, sizeof(name)) <= 0)
                {
                    perror("read");
                    break;
                }
                DIR *dir;
                struct dirent *dd;
                int count = 0;
                dir = opendir(name);
                char* buff;
                if(dir==NULL)
                    write(connfd, "N", 1);
                else {
                    write(connfd, "S", 1);
                     while ((dd = readdir(dir)) != NULL)
                    {
                        buff=dd->d_name;
                        if(strcmp(buff, ".") == 0 || strcmp(buff, "..") == 0) continue;
                        write(connfd, buff, strlen(buff) + 1);
                        count++;
                    }
                }

                /*la connessione assegnata al figlio viene chiusa*/
                printf("Figlio %i: termino\n", getpid());
                shutdown(connfd, 0);
                shutdown(connfd, 1);
                close(connfd);
                exit(0);
            }
        } /* fine gestione richieste di file */

        /* GESTIONE RICHIESTE DI CONTEGGIO ------------------------------------------ */
        if (FD_ISSET(udpfd, &rset))
        {
            printf("Ricevuta richiesta di conteggio file\n");

            len = sizeof(struct sockaddr_in);
            if (recvfrom(udpfd, packet, LENGTH_FILE_NAME, 0, (struct sockaddr *)&cliaddr, &len) < 0)
            {
                perror("recvfrom");
                continue;
            }
            char nome_f[250], parola[250];
            int i=0, j=0;
            while(packet[i]!=0){
                nome_f[i]=packet[i];
                i++;
            }
            nome_f[i] = '\0';
            i++;
            while(packet[i]!=0){
                parola[j]=packet[i];
                j++;
                i++;
            }
            parola[j] = '\0';
            printf("nome file: %s\n", nome_f);
            printf("parola da eliminare: %s\n", parola);
            int ck=0;
            int fd=open(nome_f, O_RDONLY);
            int ft = open("temp.txt", O_WRONLY | O_CREAT | O_TRUNC, 0777);
            //int ft=open(nome_f, O_WRONLY);    //se si usa da aggiungere logica per cambiare lunghezza file
            if(fd<0) {
                int num=-1;
                perror("Errore apertura file lettura:");
                if (sendto(udpfd, &num, sizeof(num), 0, (struct sockaddr *)&cliaddr, len) < 0)
                {
                    perror("sendto");
                    continue;
                }
                continue;
            }
            if(ft<0) {
                perror("Errore apertura file scrittura:");
                continue; //msg di errore anche qui
            }
            i=0;
            int count=0;
            char c, word[255];
            while(read(fd, &c, 1)>0) {
                if(c!=' ' && c != '\n') {
                    /*if(i > strlen(word)) { //???
                        write(ft, word, strlen(word));
                        i=0;
                        ck=1;
                    } else {*/
                        word[i] = c;
                        i++;
                    //}
                } else {
                    word[i]=0;
                    //printf("%s\n", word);
                    if((strcmp(word, parola) != 0)/* || (ck == 1)*/) {
                        if(write(ft, word, strlen(word))<0) {
                            perror("Errore scrittura : ");
                            continue;
                        }
                    }
                    else
                        count++;
                    write(ft, &c, 1);
                    i=0;
                    ck=0;
                }
            }
            //renaming file
            if(unlink(nome_f)<0) {
                perror("Errore unlink : ");
            }
            if(link("temp.txt",nome_f)<0) {
                perror("Errore link : ");
            }
            if(unlink("temp.txt")<0) {
                perror("Errore unlink : ");
            }
            close(fd);
            close(ft);
            if (sendto(udpfd, &count, sizeof(count), 0, (struct sockaddr *)&cliaddr, len) < 0)
            {
                perror("sendto");
                continue;
            }
        } /* fine gestione richieste di conteggio */

    } /* ciclo for della select */
    /* NEVER ARRIVES HERE */
    exit(0);
}

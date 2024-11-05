#include <arpa/inet.h>
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
#include <unistd.h>

int main(int argc, char **argv) {
    int sd, port, len, num1;
    const int on = 1;
    struct sockaddr_in cliaddr, servaddr;
    struct hostent *clienthost;
    char buf[256];

    if (argc != 2) {
        printf("Error: %s port\n", argv[0]);
        exit(1);
    } else {
        num1 = 0;
        while (argv[1][num1] != '\0') {
            if ((argv[1][num1] < '0') || (argv[1][num1] > '9')) {
                printf("Secondo argomento non intero\n");
                printf("Error: %s port\n", argv[0]);
                exit(2);
            }
            num1++;
        }
        port = atoi(argv[1]);
        if (port < 1024 || port > 65535) {
            printf("Error: %s port\n", argv[0]);
            printf("1024 <= port <= 65535\n");
            exit(2);
        }
    }

    memset((char *)&servaddr, 0, sizeof(servaddr));
    servaddr.sin_family      = AF_INET;
    servaddr.sin_addr.s_addr = INADDR_ANY;
    servaddr.sin_port        = htons(port);

    sd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sd < 0) {
        perror("creazione socket ");
        exit(1);
    }
    printf("Server: creata la socket, sd=%d\n", sd);

    if (setsockopt(sd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0) {
        perror("set opzioni socket ");
        exit(1);
    }
    printf("Server: set opzioni socket ok\n");

    if (bind(sd, (struct sockaddr_in *)&servaddr, sizeof(servaddr)) < 0) {
        perror("bind socket ");
        exit(1);
    }
    printf("Server: bind socket ok\n");

    char c;
    int err=-1, l=0, max;
    int file = NULL;
    while(1) {
        l=0;
        max=0;
        len = sizeof(struct sockaddr_in);
        if (recvfrom(sd, buf, sizeof(buf), 0, (struct sockaddr_in *)&cliaddr, &len) < 0) {
            //buf[255]='/0';
            printf("[%s]", buf);
            perror("recvfrom ");
            continue;
        }
        buf[255]='/0';
        printf("[%s]\n", buf);
        clienthost = gethostbyaddr((char *)&cliaddr.sin_addr, sizeof(cliaddr.sin_addr), AF_INET);
        if (clienthost == NULL) {
            // Se non c'è nome logico (es. no DNS), usiamo l'indirizzo IP
            printf("Operazione richiesta da: %s %i\n", inet_ntoa(cliaddr.sin_addr),
                   (unsigned)ntohs(cliaddr.sin_port));
        } else {
            printf("Operazione richiesta da: %s %i\n", clienthost->h_name,
                   (unsigned)ntohs(cliaddr.sin_port));
        }
        if((file=open(buf, O_RDONLY))<0) {
            perror("File non leggibile!!");
            if (sendto(sd, &err, sizeof(int), 0, (struct sockaddr_in *)&cliaddr, len) < 0) {
                perror("sendto ");
                continue;

            } 
            continue;
        }
        while(read(file, &c, 1)>0) {
            if(c!=' '&&c!='\n') {
                l++;
                if(l>max) {
                    max=l;
                }
            }
            else
                l=0;
        }
        
        if (sendto(sd, &max, sizeof(int), 0, (struct sockaddr_in *)&cliaddr, len) < 0) {
            perror("sendto ");
            continue;
        }
        close(file);
    }
}
#include <rpc/rpc.h>
#include "votaFattoreX.h"
#include <stdio.h>
#include <string.h>

#define NUM_CANDIDATI 6
#define greater(a,b) (a > b) ? 1 : 0

struct Table{
    char candidato[20];
    char giudice[20];
    char categoria;
    char nome_file[20];
    char fase;
    int voti;
};

static struct Table table[NUM_CANDIDATI];
static int inizializzato = 0;
static char giudici[4][20] = {"Pippo", "Pluto", "Topolino", "Paperino"};

void inizializza(){
    strcpy(table[0].candidato, "User0");
    strcpy(table[0].giudice, "Pippo");
    table[0].categoria = 'U';
    strcpy(table[0].nome_file, "User0Profile.txt");
    table[0].fase = 'A';
    table[0].voti = 0;

    strcpy(table[1].candidato, "User1");
    strcpy(table[1].giudice, "Pluto");
    table[1].categoria = 'D';
    strcpy(table[0].nome_file, "User1Profile.txt");
    table[1].fase = 'B';
    table[1].voti = 0;

    strcpy(table[2].candidato, "User2");
    strcpy(table[2].giudice, "Topolino");
    table[2].categoria = 'O';
    strcpy(table[0].nome_file, "User2Profile.txt");
    table[2].fase = 'S';
    table[2].voti = 0;

    strcpy(table[3].candidato, "User3");
    strcpy(table[3].giudice, "Paperino");
    table[3].categoria = 'B';
    strcpy(table[3].nome_file, "User3Profile.txt");
    table[3].fase = 'A';
    table[3].voti = 0;

    strcpy(table[4].candidato, "User4");
    strcpy(table[4].giudice, "Pippo");
    table[4].categoria = 'U';
    strcpy(table[4].nome_file, "User4Profile.txt");
    table[4].fase = 'A';
    table[4].voti = 0;

    strcpy(table[5].candidato, "User5");
    strcpy(table[5].giudice, "Pippo");
    table[5].categoria = 'U';
    strcpy(table[5].nome_file, "User55Profile.txt");
    table[5].fase = 'A';
    table[5].voti = 0;
}

Output *classifica_giudici_1_svc(void *v, struct svc_req *rqstp){
    if(!inizializzato){ inizializza(); inizializzato = 1;}
    static Output output;
    int i = 0, j = 0;
    for(i; i < 4; i++){
        output.giudice[i].score = 0;
        strcpy(output.giudice[i].nome,giudici[i]);
        for(j = 0; j < NUM_CANDIDATI; j++){
            if(strcmp(output.giudice[i].nome,table[j].giudice) == 0)
                output.giudice[i].score += table[j].voti;
        }
    }

    //ordinamento
    int ordinato = 0;
    i = 0;
    Giudice temp;
    while(!ordinato){
        ordinato = 1;
        for(i = 0; i < 3; i++){
            if(greater(output.giudice[i+1].score, output.giudice[i].score)){
                //swap
                temp = output.giudice[i];
                output.giudice[i] = output.giudice[i+1];
                output.giudice[i+1] = temp;
                ordinato = 0;
            }
        }
    }

    return &output;
}

int *esprimi_voto_1_svc(Input *input, struct svc_req *rqstp){
    if(!inizializzato){ inizializza(); inizializzato = 1;}

    int i = 0;
    static int esito = -3;
    esito = -3;

    for(i; i < NUM_CANDIDATI; i++){
        if(strcmp(input->candidato, table[i].candidato) == 0){
            esito = -2;
            if(strcmp(input->operazione, "sottrazione") == 0){
                if(table[i].voti == 0) esito = -1;
                else{
                    table[i].voti--;
                    esito = 0;
                }
            }else if(strcmp(input->operazione, "aggiunta") == 0){
                table[i].voti++;
                esito = 0;
            }
            return &esito;
        }
    }
    return &esito;
}
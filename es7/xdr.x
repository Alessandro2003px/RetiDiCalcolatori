struct NomeFile{char lettera[255];};
struct Lista{
    int numeroFile;
    NomeFile nomeFile[8];
};

struct InfoFile{
    int caratteri;
    int parole;
    int linee;
};

struct InputDir{
    string dir <255>;
    int soglia;
};

program REMOTE{
    version REMOTEVERS{
        InfoFile FILE_SCAN(string) = 1;
        Lista DIR_SCAN(InputDir) = 2;
    } = 1;
} = 0x20000015;

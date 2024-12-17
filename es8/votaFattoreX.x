struct Input{
    char candidato[20];
    char operazione[12];
};

struct Giudice{
	char nome[20];
    int score;
};

struct Output {
	Giudice giudice[4];
};


program VOTAFATTOREXPROG {
	version VOTAFATTOREXVERS{
		Output  CLASSIFICA_GIUDICI(void) = 1;
		int ESPRIMI_VOTO(Input) = 2;
	} = 1;
} = 0x20000013;
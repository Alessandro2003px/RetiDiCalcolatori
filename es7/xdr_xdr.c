/*
 * Please do not edit this file.
 * It was generated using rpcgen.
 */

#include "xdr.h"

bool_t
xdr_NomeFile (XDR *xdrs, NomeFile *objp)
{
	register int32_t *buf;

	int i;
	 if (!xdr_vector (xdrs, (char *)objp->lettera, 255,
		sizeof (char), (xdrproc_t) xdr_char))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_Lista (XDR *xdrs, Lista *objp)
{
	register int32_t *buf;

	int i;
	 if (!xdr_int (xdrs, &objp->numeroFile))
		 return FALSE;
	 if (!xdr_vector (xdrs, (char *)objp->nomeFile, 8,
		sizeof (NomeFile), (xdrproc_t) xdr_NomeFile))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_InfoFile (XDR *xdrs, InfoFile *objp)
{
	register int32_t *buf;

	 if (!xdr_int (xdrs, &objp->caratteri))
		 return FALSE;
	 if (!xdr_int (xdrs, &objp->parole))
		 return FALSE;
	 if (!xdr_int (xdrs, &objp->linee))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_InputDir (XDR *xdrs, InputDir *objp)
{
	register int32_t *buf;

	 if (!xdr_string (xdrs, &objp->dir, 255))
		 return FALSE;
	 if (!xdr_int (xdrs, &objp->soglia))
		 return FALSE;
	return TRUE;
}
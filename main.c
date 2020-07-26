/*
 * main.c
 *
 *  Created on: Jun 11, 2020
 *      Author: Guy Shnaider
 */
#include "spmat.h"
#include<stdio.h>
#include<assert.h>
#include<stdlib.h>
#include<time.h>
#include <stddef.h>
#include <math.h>
#include <string.h>

int nonZeroCount(int dim, FILE *input);
int sub(double *b0, double *b1, int dim);
void norm(double *b1, int dim);
void copy (double *b0, double *b1, int dim);

int main(int argc, char* argv[]){
	FILE *inputMat, *inputVec, *outputVec;
	int dim, vecDim, one, i, isClose;
	double *row, *b0, *b1, *vctrPoint;
	spmat *matrix;

	assert(argc > 3 && argc < 6);

	inputMat = fopen(argv[1], "r");
	assert(inputMat != NULL);

	assert((int)fread(&dim, sizeof(int), 1, inputMat) == 1);
	assert((int)fread(&dim, sizeof(int), 1, inputMat) == 1);


	/* b0 */

	if(argc == 4){ /* need to create random b0 */
		srand(time(NULL));

		b0 = (double*)malloc(dim*sizeof(double));
		assert(b0 != NULL);

		vctrPoint = b0;
		for(i = 0; i < dim; i++){
			*vctrPoint = rand();
			vctrPoint++;
		}
		one = 1;
	}
	else{ /* b0 is given */
		inputVec = fopen(argv[2], "r");
		assert(inputVec != NULL);

		assert((int)fread(&one, sizeof(int), 1, inputVec) == 1);
		assert((int)fread(&vecDim, sizeof(int), 1, inputVec) == 1);

		b0 = (double*)malloc(vecDim*sizeof(double));
		assert(b0 != NULL);

		assert((int)fread(b0, sizeof(double), vecDim, inputVec) == vecDim);
	}


	/* implementation check */

	if(strcmp(argv[argc-1], "-list") == 0){
		matrix = spmat_allocate_list(dim);
	}
	else{
        if(strcmp(argv[argc-1], "-array") == 0){
            matrix = spmat_allocate_array(dim, nonZeroCount(dim, inputMat));
        }
    }

    row = (double*)malloc(dim*sizeof(double));
    assert(row != NULL);

	/* reading input to spmat */

	for(i = 0; i < dim; i++){
		assert((int)fread(row, sizeof(double), dim, inputMat) == dim);
		matrix->add_row(matrix, row, i);
	}

    /* calculating eigen vector */

	b1 = (double*)calloc(dim, sizeof(double));
	assert(b1 != NULL);

    isClose = 0;
	while(isClose == 0){
		matrix->mult(matrix, b0, b1);
		norm(b1, dim);
		isClose = sub(b0, b1, dim);
		if(isClose == 0)
			copy(b0, b1, dim);
	}

	/* writing to output file */

	outputVec = fopen(argv[argc-2], "w");
	assert(outputVec != NULL);
	fwrite(&one, sizeof(int), 1, outputVec);
	fwrite(&dim, sizeof(int), 1, outputVec);
	fwrite(b1, sizeof(double), dim, outputVec);


	/* freeing memory and closing files */

	fclose(inputMat);
	fclose(outputVec);
	if(argc == 5)
		fclose(inputVec);
	matrix->free(matrix);
	free(b0);
	free(b1);
	free(row);

	return 0;
}

/*
 * counting non zero values in matrix
 */
int nonZeroCount(int dim, FILE *input){
    double *currentRow;
	int count = 0;
	int j;
	double *rowEnd;

    currentRow = (double*)malloc(dim*sizeof(double));
    assert(currentRow != NULL);

	for(j = 0; j < dim; j++){
		assert((int)fread(currentRow, sizeof(double), dim, input) == dim);
		rowEnd = currentRow + dim;
		for( ;currentRow < rowEnd; currentRow++){
			if(*currentRow != 0.0)
				count++;
		}
		currentRow -= dim;
	}

	rewind(input);
    assert((int)fread(&dim, sizeof(int), 1, input) == 1);
    assert((int)fread(&dim, sizeof(int), 1, input) == 1);

	free(currentRow);
	return count;
}

int sub(double *b0, double *b1, int dim){
	double sigma;
	int i;
	sigma = 0.0000077;

	for (i=0; i<dim; i++){
		if (*b1 - *b0 >sigma){
			return 0;
		}
		if (*b0 - *b1 > sigma){
			return 0;
		}
		b1++;
		b0++;
	}

	return 1;

}


void norm(double *b1, int dim){
	int i;
	double* original;
	double nrm;

	nrm = 0;
	original = b1;
	for (i=0; i<dim; i++){
		nrm +=  *b1 * *b1;
		b1++;
	}
	nrm = sqrt(nrm);
	b1 = original;
	for (i=0; i<dim; i++){
		*b1 = *b1/nrm;
		b1++;
	}
}

void copy (double *b0, double *b1, int dim){

	int i;

	for (i=0; i<dim; i++){
		*b0 = *b1;
		b0++;
		b1++;
	}
}

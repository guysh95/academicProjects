/*
 * spmat.c
 *
 *  Created on: May 13, 2020
 *      Author: Guy Shnaider
 */
#include "spmat.h"
#include<stdio.h>
#include<assert.h>
#include<stdlib.h>
#include<time.h>
#include <stddef.h>
#include <math.h>

/*lists*/
void add_row_list (struct _spmat *A, const double *row, int i);
void free_list(struct _spmat *A);
void mult_list(const struct _spmat *A, const double *v, double *result);


typedef double DATA;
struct linked_list {
    DATA d;
    int column;
    struct linked_list *next;
};

typedef struct linked_list ELEMENT;
typedef ELEMENT *LINK;

void add_row_list (struct _spmat *A, const double *row, int i) {
	ELEMENT *tail, *head = NULL;
	LINK *row_lst;
	int column, n;
    row_lst = A->private;
    n = A->n;
    for(column = 0; column < n; column++){
        if (*row != 0.0){  /* if current value is nonzero*/
            if (head == NULL){
                head = (ELEMENT*)malloc(sizeof(ELEMENT));
                assert(head != NULL);
                head->d = *row;
                head->column = column;
                tail = head;
            }
            else{
                tail->next = (ELEMENT*)malloc(sizeof(ELEMENT));
                assert(tail != NULL);
                tail = tail->next;
                tail->d = *row;
                tail->column = column;
            }
        }
        row++;
    }
    tail->next = NULL;
    row_lst[i] = head;
}

void free_list(struct _spmat *A){
	ELEMENT *tail, *head;
	LINK *row_lst;
	int j, n;
	row_lst = A->private;
	n = A->n;
	for(j = 0; j < n; j++){
		head = row_lst[j];
		if (head != NULL){
			tail = head->next;
			while (tail != NULL){
				free(head);
				head = tail;
				tail = head->next;
			}
			free(head);
		}
	}
	free(row_lst);
	free(A);
}

void mult_list(const struct _spmat *A, const double *v, double *result){
	ELEMENT *head;
	LINK *row_lst;
	int n, j;
	row_lst = A->private;
	n = A->n;
	for (j = 0; j < n; j++){
		head = row_lst[j];
		if (head==NULL){
			result[j] = 0;
		}
		else{
			while (head!=NULL){
				result[j] += head->d * v[head->column];
				head = head->next;
			}
		}
	}
}

spmat* spmat_allocate_list(int n){
	spmat *mat;
	LINK *row_lst;

	mat = (spmat*)malloc(sizeof(spmat));
	assert(mat != NULL);
	row_lst = malloc(n*sizeof(LINK));
	assert(row_lst != NULL);

	mat->add_row = &add_row_list;
	mat->n = n;
	mat->free = &free_list;
	mat->mult = &mult_list;
	mat->private = row_lst;

	return mat;
}

/*arrays*/
void add_row_array (struct _spmat *A, const double *row, int i);
void free_array(struct _spmat *A);
void mult_array(const struct _spmat *A, const double *v, double *result);

typedef struct {
	double *values;
	int *colind;
	int *rowptr;
	double *current; /*additional pointer for values*/
	int *currentCol; /*additional pointer for colind*/
	int *currentRow;
	/*int SeqLenNoneValRow = 0;  keeping track of the length of last seen sequence of all zero rows*/
	int index; /* keeping track of how many values were inserted to values & colind*/
	int nnz; /* counter for number of vals*/
}compMat;



spmat* spmat_allocate_array(int n, int nnz){

	/*allocating room for the implementation*/
	compMat *matImp;
	spmat *mat;

    matImp = (compMat*)malloc(sizeof(compMat));
    assert(matImp != NULL);

	matImp->values = (double*)malloc(nnz*sizeof(double));
	assert(matImp->values != NULL);
	matImp->colind = (int*)malloc(nnz*sizeof(int));
	assert(matImp->colind != NULL);
	matImp->rowptr = (int*)malloc((n+1)*sizeof(int));
	assert(matImp->rowptr != NULL);

	matImp->current = matImp->values;
	matImp->currentCol = matImp->colind;
	matImp->currentRow = matImp->rowptr;
	matImp->nnz = nnz;
	matImp->index = 0;

	/*creating spmat to point at implementation */
	mat = (spmat*)malloc(sizeof(spmat));
    assert(mat != NULL);


	mat->n = n;
	mat->private = matImp;
	mat->add_row = &add_row_array;
	mat->free = &free_array;
	mat->mult = &mult_array;

	return mat;
}

void add_row_array (struct _spmat *A, const double *row, int i){
	const double* p = row;
	int col, n = A->n;
	compMat *mat = A->private;
    int count = mat->index;
    int nnz = mat->nnz;
	*(mat->currentRow) = count;
	mat->currentRow++;

	/* values treatment + colind treatment */
	for(col = 0 ; col < n ; ++col){
		if(*p != 0.0){
			*(mat->current) = *p;
			(mat->current)++;
			*(mat->currentCol) = col;
			(mat->currentCol)++;
			count++;
		}
		p++;
	}

    mat->index = count;

	if((i+1) == n)
		*(mat->currentRow) = nnz;

}

void free_array(struct _spmat *A){
	compMat *mat;
	mat = A->private;
	free(mat->rowptr);
	free(mat->colind);
	free(mat->values);
	free(mat);
	free(A);
}


void mult_array(const struct _spmat *A, const double *v, double *result){
	compMat *mat = A->private;
	int nnz = mat->nnz;
	int column;
	double *valPointer = mat->values;
	int *columnPointer = mat->colind;
	int *rowPtr = mat->rowptr;
	int row = 0;
	int i;
	int curRowLen;

	while(*rowPtr != nnz){
		curRowLen = *(rowPtr+1) - *(rowPtr);
		for(i = 0; i < curRowLen; i++){
			column = *columnPointer;
			result[row] += (*valPointer) * v[column];
			++columnPointer;
			++valPointer;
		}
		row++;
		rowPtr++;
	}

}

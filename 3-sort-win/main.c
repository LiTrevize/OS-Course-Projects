#include <stdio.h>
#include <windows.h>


DWORD Nums[1000]; /* data is shared by the thread(s) */

typedef struct {
    int Start;
    int End;
    int Mid;
} Parameter;

/* bubble sort */
DWORD WINAPI Sort(PVOID Param) {
    Parameter P = *(Parameter *) Param;
    DWORD tmp;
    for (int j = P.End; j >= P.Start; --j) {
        for (int i = P.Start; i < j; ++i) {
            if (Nums[i] > Nums[i + 1]) {
                tmp = Nums[i];
                Nums[i] = Nums[i + 1];
                Nums[i + 1] = tmp;
            }
        }
    }
    return 0;
}

DWORD WINAPI Merge(PVOID Param) {
    Parameter P = *(Parameter *) Param;
    int i, j, k;
    int im = P.Mid - P.Start;
    int jm = P.End - P.Mid + 1;
    DWORD *a = malloc(sizeof(DWORD) * im);
    DWORD *b = malloc(sizeof(DWORD) * jm);
    for (i = 0; i < im; ++i)
        a[i] = Nums[i];
    for (j = 0; j < jm; ++j)
        b[j] = Nums[i + j];
    i = j = k = 0;
    while (i < im && j < jm) {
        if (a[i] < b[j]) {
            Nums[k] = a[i];
            i++;
        } else {
            Nums[k] = b[j];
            j++;
        }
        k++;
    }
    if (i == im) {
        while (j < jm)
            Nums[k++] = b[j++];
    } else {
        while (i < im)
            Nums[k++] = a[i++];
    }
    free(a);
    free(b);
    return 0;
}


int main(int argc, char *argv[]) {
    DWORD ThreadId[3];
    HANDLE ThreadHandle[3];
    Parameter Param[3];

    int i, n;
    printf("Enter the length of the array:");
    scanf("%d", &n);

    if (n < 0) {
        fprintf(stderr, "an integer >= 0 is required \n");
        return -1;
    } else if (n > 1000) {
        fprintf(stderr, "an integer <= 1000 is required \n");
        return -1;
    }
    printf("Enter all the numbers:\n");
    for (int i = 0; i < n; ++i) {
        scanf("%d", &Nums[i]);
    }

    Param[0].Start = 0;
    Param[0].End = n / 2 - 1;
    Param[1].Start = n / 2;
    Param[1].End = n - 1;

    // sort thread
    for (i = 0; i < 2; i++) {
        ThreadHandle[i] = CreateThread(NULL, 0, Sort, &Param[i], 0, &ThreadId[i]);
    }
    for (int i = 0; i < 2; ++i) {
        WaitForSingleObject(ThreadHandle[i], INFINITE);
        CloseHandle(ThreadHandle[i]);
    }
    // merge thread
    Param[2].Start = 0;
    Param[2].End = n - 1;
    Param[2].Mid = n / 2;
    ThreadHandle[2] = CreateThread(NULL, 0, Merge, &Param[2], 0, &ThreadId[2]);
    WaitForSingleObject(ThreadHandle[2], INFINITE);
    CloseHandle(ThreadHandle[2]);

    // print result
    for (int i = 0; i < n; ++i) {
        printf("%d ", Nums[i]);
    }
    printf("\n");
}
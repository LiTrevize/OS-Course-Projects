#include <stdio.h>
#include <stdlib.h>
#include <windows.h>
#include "buffer.h"

/* the buffer */
buffer_item buffer[BUFFER_SIZE];
int idx;
HANDLE lock, empty, full;

int insert_item(buffer_item item) {
    WaitForSingleObject(empty, INFINITE);
    WaitForSingleObject(lock, INFINITE);
    buffer[++idx] = item;
    ReleaseMutex(lock);
    ReleaseSemaphore(full, 1, NULL);
    return 0;
}

int remove_item(buffer_item *item) {
    WaitForSingleObject(full, INFINITE);
    WaitForSingleObject(lock, INFINITE);
    *item = buffer[idx--];
    ReleaseMutex(lock);
    ReleaseSemaphore(empty, 1, NULL);
    return 0;
}

void *producer(void *param) {
    buffer_item item;
    srand(GetCurrentThreadId());
    while (TRUE) {
        /* sleep for a random period of time */
        Sleep(rand() % 1000);
        /* generate a random number */
        item = rand();
        if (insert_item(item))
            fprintf(stderr, "report error condition");
        else
            printf("producer produced %d\n", item);
    }
}

void *consumer(void *param) {
    buffer_item item;
    srand(GetCurrentThreadId());
    while (TRUE) {
        /* sleep for a random period of time */
        Sleep(rand() % 1000);
        if (remove_item(&item))
            fprintf(stderr, "report error condition");
        else
            printf("consumer consumed %d\n", item);
    }
}

int main(int argc, char *argv[]) {
    /* 1. Get command line arguments argv[1],argv[2],argv[3] */
    DWORD sleeptime = atoi(argv[1]);
    DWORD pnum = atoi(argv[2]);
    DWORD cnum = atoi(argv[3]);

    /* 2. Initialize buffer */
    idx = -1;
    lock = CreateMutex(NULL, FALSE, NULL);
    empty = CreateSemaphore(NULL, BUFFER_SIZE, BUFFER_SIZE, NULL);
    full = CreateSemaphore(NULL, 0, BUFFER_SIZE, NULL);

    /* 3. Create producer thread(s) */
    for (int i = 0; i < pnum; ++i) {
        CreateThread(NULL, 0, producer, NULL, 0, NULL);
    }
    /* 4. Create consumer thread(s) */
    for (int i = 0; i < cnum; ++i) {
        CreateThread(NULL, 0, consumer, NULL, 0, NULL);
    }

    /* 5. Sleep */
    Sleep(sleeptime);
    /* 6. Exit */
    return 0;
}
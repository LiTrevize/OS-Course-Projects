/**
 * Simple shell interface program.
 *
 * Operating System Concepts - Tenth Edition
 * Copyright John Wiley & Sons - 2018
 */

#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <malloc.h>
#include <sys/wait.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/stat.h>

#define MAX_LINE        80 /* 80 chars per line, per command */
#define READ_END 0
#define WRITE_END 1

/**
* After reading user input, the steps are:
* (1) fork a child process
* (2) the child process will invoke execvp()
* (3) if command included &, parent will invoke wait()
*/

void log_msg(const char *filename, const char *msg) {
    if (access(filename, 0) == 0)
        remove(filename);
    int tmp = open(filename, O_WRONLY | O_CREAT, S_IRWXU);
    write(tmp, msg, strlen(msg));
    close(tmp);
}

int main(void) {
    char *args[MAX_LINE / 2 + 1] = {};    /* command line (of 80) has max of 40 arguments */
    int should_run = 1;
    char buf[MAX_LINE], history_buf[MAX_LINE] = "";
    int i, argid, curcid;
    int concurrent;
    char filename[MAX_LINE];
    int fd = -1, savefd;
    int fds[2];
    int redirect; // 0: none, 1: output, 2: input
    int usepipe;
    int pipeid;
    pid_t pid;

    while (should_run) {
        printf("osh>");
        fflush(stdout);

        // user input
        if (fgets(buf, MAX_LINE, stdin) != NULL) {
            i = 0;
            argid = 0;
            curcid = 0;
            usepipe = 0;
            redirect = 0;
            concurrent = 0;
            
            // if enter nothing
            if (buf[0] == '\n') continue;
            // exit
            if (strcmp(buf, "exit\n") == 0) {
                should_run = 0;
                continue;
            }
            // execute history command
            if (strcmp(buf, "!!\n") == 0) {
                if (history_buf[0] == 0) {
                    printf("No commands in history.\n");
                    continue;
                } else {
                    strcpy(buf, history_buf);
                    printf("%s", buf);
                }
            }
            strcpy(history_buf, buf);
            // replace last \n by space
            buf[strlen(buf) - 1] = ' ';
            while (buf[i]) {
                if (buf[i] == '&')
                    concurrent = 1;
                if (buf[i] == '|') {
                    usepipe = 1;
                    pipeid = argid;
                }
                if (buf[i] == ' ') {
                    // check if last char is space
                    if (curcid != 0) { // not space
                        args[argid][curcid] = 0;
                        argid++;
                    }
                    curcid = 0;

                } else {
                    if (args[argid] == NULL) {
                        args[argid] = (char *) malloc(MAX_LINE * sizeof(char));
                    }
                    args[argid][curcid++] = buf[i];
                }
                i++;
            }
            // remove &
            if (strcmp(args[argid - 1], "&") == 0) {
                argid--;
                free(args[argid]);
            }
            // redirection

            // output redirection
            if (argid >= 2 && strcmp(args[argid - 2], ">") == 0) {
                redirect = 1;
                strcpy(filename, args[argid - 1]);
                argid -= 2;
                savefd = dup(STDOUT_FILENO);
                if (access(filename, 0) == 0)
                    remove(filename);
                fd = open(filename, O_WRONLY | O_CREAT, S_IRWXU);
                // printf("%s %d\n", filename, fd);
                if (fd != -1)
                    dup2(fd, STDOUT_FILENO);
            }
            // input redirection
            if (argid >= 2 && strcmp(args[argid - 2], "<") == 0) {
                redirect = 2;
                strcpy(filename, args[argid - 1]);
                argid -= 2;
                if (access(filename, 0) != 0) {
                    printf("Error: %s does not exist.", filename);
                    continue;
                }
                fd = open(filename, O_RDONLY);
                savefd = dup(STDIN_FILENO);
                // printf("%s %d\n", filename, fd);
                if (fd != -1)
                    dup2(fd, STDIN_FILENO);
                close(fd);
            }
            // set the last arg to null
            if (args[argid] != NULL) {
                free(args[argid]);
                args[argid] = NULL;
            }


            // create child process
            pid = fork();
            if (pid < 0) { /* error occurred */
                fprintf(stderr, "Fork Failed");
                return 1;
            } else if (pid == 0) { /* child process */
                if (concurrent == 1) {
                    pid = fork();
                    if (pid < 0) { /* error occurred */
                        fprintf(stderr, "Fork Failed");
                        return 1;
                    } else if (pid == 0) { /* grandchild process */
                        // execute
                        execvp(args[0], args);
                    } else { /*  child process */
                        // exit. make a orphan process for init process to wait
                        exit(0);
                    }
                } else if (usepipe == 1) {
                    // execute the right command
                    // parse command
                    // args+pipeid+1

                    /* create the pipe */
                    if (pipe(fds) == -1) {
                        fprintf(stderr, "Pipe failed");
                        return 1;
                    }

                    // grandchild process
                    // execute the left command
                    pid = fork();
                    if (pid < 0) { /* error occurred */
                        fprintf(stderr, "Fork Failed");
                        return 1;
                    } else if (pid == 0) { /* grandchild process */
                        // redirect output to pipe
                        close(fds[READ_END]);
                        dup2(fds[WRITE_END], STDOUT_FILENO);

                        // simple parser
                        if (args[pipeid] != NULL) {
                            free(args[pipeid]);
                            args[pipeid] = NULL;
                        }

                        // execute
                        execvp(args[0], args);


                    } else { /*  child process */
                        // redirect input to pipe
//                        savefd = dup(STDIN_FILENO);
                        close(fds[WRITE_END]);
                        dup2(fds[READ_END], STDIN_FILENO);
//                        close(fds[READ_END]);

                        wait(NULL);

                        // execute command
                        execvp((args + pipeid + 1)[0], args + pipeid + 1);

                    }
                } else {
                    // execute command
                    execvp(args[0], args);
                }



                // if concurrent, exit
//                exit(0);
            } else { /* parent process */
                if (usepipe == 1) {
//                    /* close the unused end of the pipe */
//                    close(fds[READ_END]);
//                    /* write to the pipe */
//                    write(fds[WRITE_END], buf + i + 2, strlen(buf) - i - 1);
//                    /* close the write end of the pipe */
//                    close(fds[WRITE_END]);
//                    dup2(savefd, STDIN_FILENO);
                }


                // wait for child
                waitpid(pid, NULL, 0);
                // stdout change back
                if (redirect == 1) {
                    close(fd);
                    dup2(savefd, STDOUT_FILENO);
                } else if (redirect == 2) {
                    dup2(savefd, STDIN_FILENO);
                }
            }
        }

    }

    // free space
    for (int j = 0; j < MAX_LINE / 2 + 1; ++j) {
        if (args[j] != NULL) {
            free(args[j]);
            args[j] = NULL;
        }
    }

    return 0;
}

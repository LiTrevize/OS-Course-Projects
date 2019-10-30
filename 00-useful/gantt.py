import numpy as np
import matplotlib.pyplot as plt
import matplotlib.patches as mpatch


class Gantt:
    def __init__(self, job, time, caption):
        self.job = job
        self.time = time
        self.caption = caption

    def draw(self):
        fig = plt.figure(figsize=(10, 1))
        ax = plt.subplot()
        plt.xlim((0, sum(self.time)))
        plt.ylim((0, 1))

        my_x_ticks = [sum(self.time[:i]) for i in range(len(self.time)+1)]
        my_y_ticks = []
        plt.xticks(my_x_ticks, fontsize=15)
        plt.yticks(my_y_ticks)
        # ax.xaxis.set_ticks_position('bottom')

        x = 0
        for i, t in enumerate(self.time):
            rect = mpatch.Rectangle([x, 0], t, 1, color='black', fill=False)
            ax.add_patch(rect)
            plt.text(x+t/2., 0.5, self.job[i],
                     fontsize=18, ha='center', va='center')
            x += t
        plt.tight_layout()
        plt.savefig('./fig/'+self.caption+'.png')
        plt.show()

if __name__ == "__main__":
    job = input('Job id: ')
    time = input('Duration: ')
    caption = input('Caption: ')
    job = ['P'+i for i in job.split()]
    time = [int(i) for i in time.split()]
    g = Gantt(job,time,caption)
    g.draw()



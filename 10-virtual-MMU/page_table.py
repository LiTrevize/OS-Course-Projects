import random

a=list(range(256))
random.shuffle(a)
f=open("page_table.txt",'w')
for i in range(256):
    f.write(str(a[i])+'\n')
f.close
f.close()
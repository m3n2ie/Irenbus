import random

with open('DailyRidershipByRoute_filtered.csv', 'r') as r, open('DailyRidershipByRoute_shuffled.csv', 'w') as w:
    data = r.readlines()
    header, rows = data[0], data[1:]
    random.shuffle(rows)
    rows = '\n'.join([row.strip() for row in rows])
    w.write(header + rows)


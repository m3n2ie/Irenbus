import csv
import datetime

def normalize(x, min, max):
    y = (x - min) / (max - min)
    return y

with open('DailyRidershipByRoute_shuffled.csv', mode='r') as csv_file:
    csv_reader = csv.DictReader(csv_file)

    for row in csv_reader:
        with open('DailyRidershipByRoute-Normalized-Dataset.csv', mode='a', newline='') as file:
                file_writer = csv.writer(file)

                route = normalize((int)(row['route']), 1, 22)
                dayOfWeek = normalize((int)(row['dayOfWeek']), 0, 6)
                dayOfMonth = normalize((int)(row['dayOfMonth']), 1, 31)
                dayType = row['dayType']
                boardings = normalize((int)(row['rides']), 1, 45177)

                file_writer.writerow([route,
                                      dayOfWeek,
                                      dayOfMonth,
                                      dayType,
                                      boardings])  # route, dayOfWeek, dayOfMonth, dayType, boardings

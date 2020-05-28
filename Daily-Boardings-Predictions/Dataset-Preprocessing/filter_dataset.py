import csv
import datetime

with open('DailyRidershipByRoute_formatted.csv', mode='r') as csv_file:
    csv_reader = csv.DictReader(csv_file)

    for row in csv_reader:
        with open('DailyRidershipByRoute_filtered.csv', mode='a', newline='') as file:
                file_writer = csv.writer(file)

                if((int)(row['rides']) > 0): #skips rows with zero boardings
                    daytpe = '1'

                    if (row['daytype'] != "W"):
                        daytpe = 0

                    file_writer.writerow([row['route'],
                                          datetime.datetime.strptime(row['date'], "%Y-%m-%d").weekday(),
                                          datetime.datetime.strptime(row['date'], "%Y-%m-%d").day,
                                          daytpe,
                                          row['rides']]) # route, dayOfWeek, dayOfMonth, dayType, boardings

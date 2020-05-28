import csv
import datetime

import pandas as pd
df = pd.read_csv (r'DailyRidershipByRoute_shuffled.csv')

# column stats
route_mean = df['route'].mean()
route_std_div = df['route'].std()
# column stats
dayOfWeek_mean = df['dayOfWeek'].mean()
dayOfWeek_std_div = df['dayOfWeek'].std()
# column stats
dayOfMonth_mean = df['dayOfMonth'].mean()
dayOfMonth_std_div = df['dayOfMonth'].std()
# column stats
dayType_mean = df['dayType'].mean()
dayType_std_div = df['dayType'].std()
# column stats
boardings_mean = df['rides'].mean()
boardings_std_div = df['rides'].std()


def standardize(x, mean, std_div):
    y = (x - mean) / std_div
    return y

with open('DailyRidershipByRoute_shuffled.csv', mode='r') as csv_file:
    csv_reader = csv.DictReader(csv_file)

    for row in csv_reader:
        with open('DailyRidershipByRoute-Standardized-Dataset.csv', mode='a', newline='') as file:
                file_writer = csv.writer(file)

                route = standardize((int)(row['route']), route_mean, route_std_div)
                dayOfWeek = standardize((int)(row['dayOfWeek']), dayOfWeek_mean, dayOfWeek_std_div)
                dayOfMonth = standardize((int)(row['dayOfMonth']), dayOfMonth_mean, dayOfMonth_std_div)
                dayType = standardize((int)(row['dayType']), dayType_mean, dayType_std_div)
                boardings = standardize((int)(row['rides']), boardings_mean, boardings_std_div)

                file_writer.writerow([route,
                                      dayOfWeek,
                                      dayOfMonth,
                                      dayType,
                                      boardings])  # route, dayOfWeek, dayOfMonth, dayType, boardings
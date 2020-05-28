import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt

new_data_unscaled = pd.read_csv('DailyRidershipByRoute-Unscaled-Dataset.csv')

#sns.pairplot(new_data_unscaled)

sns.regplot(x="route", y="boardings", data=new_data_unscaled);

plt.show()
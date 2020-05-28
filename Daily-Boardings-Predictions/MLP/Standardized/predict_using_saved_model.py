import pandas as pd
from keras.models import load_model

model = load_model('trained_model_stdz.h5')

import pandas as pd
df = pd.read_csv (r'DailyRidershipByRoute_shuffled.csv')

# column stats
boardings_mean = df['rides'].mean()
boardings_std_div = df['rides'].std()

def destandardize(y, mean, std_div):
    return (y*std_div)+mean

# Load the data we make to use to make a prediction
X = pd.read_csv("New-Data-to-Predict.csv").values

# Make a prediction with the neural network
prediction = model.predict(X)
print(prediction)

print((int)(destandardize(prediction, boardings_mean, boardings_std_div)))


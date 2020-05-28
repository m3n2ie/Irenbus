import pandas as pd
from keras.models import load_model

model = load_model('trained_model_unsc.h5')

# Load the data we make to use to make a prediction
X = pd.read_csv("New-Data-to-Predict.csv").values

# Make a prediction with the neural network
prediction = model.predict(X)
print((int)(prediction))

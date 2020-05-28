import pandas as pd
from keras.models import load_model

model = load_model('trained_model_norm.h5')

def denormalize(y, min, max):
    return (y*(max-min))+min

# Load the data we make to use to make a prediction
X = pd.read_csv("New-Data-to-Predict.csv").values

# Make a prediction with the neural network
prediction = model.predict(X)
print(prediction)

print((int)(denormalize(prediction, 1, 45177)))


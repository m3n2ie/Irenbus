import pandas as pd
from keras.models import Sequential
from keras.layers import *

training_data_df = pd.read_csv("Normalized-Dataset-for-Training.csv")

X = training_data_df.drop('boardings', axis=1).values
Y = training_data_df[['boardings']].values

# Define the model
model = Sequential()
model.add(Dense(50, input_dim=4, activation='relu'))
model.add(Dense(100, activation='relu'))
model.add(Dense(50, activation='relu'))
model.add(Dense(1, activation='linear'))
model.compile(loss='mean_squared_error', optimizer='adam', metrics=['accuracy'])

import time
start = time.clock()

# Train the model
history = model.fit(
    X,
    Y,
    validation_split=0.33,
    batch_size=10,
    epochs=500,
    shuffle=True,
    verbose=2
)

end = time.clock()
print("Training time: ", time.strftime("%H:%M:%S", time.gmtime( end - start )))

# Load the separate test data set
test_data_df = pd.read_csv("Normalized-Dataset-for-Testing.csv")

X_test = test_data_df.drop('boardings', axis=1).values
Y_test = test_data_df[['boardings']].values

test_error_rate = model.evaluate(X_test, Y_test, verbose=0)
print("The mean squared error (MSE) for the test data set is: {}".format(test_error_rate))

import matplotlib.pyplot as plt
# list all data in history
print(history.history.keys())
# summarize history for loss
plt.plot(history.history['loss'])
plt.plot(history.history['val_loss'])
plt.title('model loss')
plt.ylabel('loss')
plt.xlabel('epoch')
plt.legend(['train', 'test'], loc='upper left')
plt.show()

# Save the model to disk
model.save("trained_model_norm.h5")
print("Model saved to disk.")
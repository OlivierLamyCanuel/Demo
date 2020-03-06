#! /usr/bin/python3
# @Author: Olivier Lamy-Canuel

import numpy as np
from NeuralNertwork import NeuralNetwork


def main():

    nn = NeuralNetwork(datapath='cifar10.pkl', seed=0, lr=0.001, batch_size=150)
    train = nn.train_loop(50)
    np.savetxt('train_accuracy.csv', train['train_accuracy'], delimiter=';')
    np.savetxt('validation_accuracy.csv', train['validation_accuracy'], delimiter=';')
    np.savetxt('train_loss.csv', train['train_loss'], delimiter=';')
    np.savetxt('validation_loss.csv', train['validation_loss'], delimiter=';')
    test_loss, test_accuracy = nn.evaluate()
    print("Test loss : " + str(test_loss))
    print("Accuracy : " + str(test_accuracy))


if __name__ == "__main__":
    main()

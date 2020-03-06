#! /usr/bin/python3
# @Author: Olivier Lamy-Canuel

import pickle
import numpy as np


class NeuralNetwork(object):
    def __init__(self,
                 hidden_dims=(512, 256),
                 datapath=None,
                 n_classes=10,
                 epsilon=1e-6,
                 lr=7e-4,
                 batch_size=1000,
                 seed=None,
                 activation="relu",
                 init_method="glorot"
                 ):

        # Initializing params
        self.hidden_dims = hidden_dims
        self.n_hidden = len(hidden_dims)
        self.datapath = datapath
        self.n_classes = n_classes
        self.lr = lr
        self.batch_size = batch_size
        self.init_method = init_method
        self.seed = seed
        self.activation_str = activation
        self.epsilon = epsilon
        self.train_logs = {'train_accuracy': [], 'validation_accuracy': [], 'train_loss': [], 'validation_loss': []}

        # File and data
        self.datapath = datapath
        self.load_data()


    def load_data(self):

        if self.datapath is not None:
            print('Loading ' + self.datapath + '...')
            u = pickle._Unpickler(open(self.datapath, 'rb'))
            u.encoding = 'latin1'
            self.train, self.valid, self.test = u.load()
        else:
            self.train, self.valid, self.test = None, None, None


    def initialize_weights(self, dims):
        print("Initializing weights...")
        if self.seed is not None:
            np.random.seed(self.seed)

        r = np.random.RandomState(self.seed)
        self.weights = {}
        all_dims = [dims[0]] + list(self.hidden_dims) + [dims[1]]

        for layer_n in range(1, self.n_hidden + 2):
            n_c = all_dims[layer_n-1]
            self.weights[f"b{layer_n}"] = np.zeros((1, all_dims[layer_n]))
            self.weights[f"W{layer_n}"] = r.uniform(-1/np.sqrt(n_c), 1/np.sqrt(n_c), (n_c, all_dims[layer_n]))

    def relu(self, x, grad=False):

        if grad:
            return np.where(x > 0, 1, 0)
        else:
            return np.where(x > 0, x, 0)

    def sigmoid(self, x, grad=False):
        if grad:
            return np.exp(-x) / (np.exp(-x) + 1)**2
        else:
            return 1/(1 + np.exp(-x))

    def tanh(self, x, grad=False):
        if grad:
            return 1 - ((np.exp(x) - np.exp(-x))**2 / (np.exp(x) + np.exp(-x))**2)
        else:
            return (np.exp(x) - np.exp(-x)) / (np.exp(x) + np.exp(-x))

    def activation(self, x, grad=False):
        if self.activation_str == "relu":
            return self.relu(x, grad)
        elif self.activation_str == "sigmoid":
            return self.sigmoid(x, grad)
        elif self.activation_str == "tanh":
            return self.tanh(x, grad)
        else:
            raise Exception("invalid")
            return 0

    def softmax_vect(self, x):
        return np.exp(x) / np.sum(np.exp(x), axis=0)

    def softmax(self, x):
        x = x - np.max(x)
        if len(np.shape(x)) == 2:
            return np.apply_along_axis(self.softmax_vect, 1, x)
        else:
            return self.softmax_vect(x)

    def forward(self, x):
        cache = {"Z0": x}

        for layer_n in range(1, self.n_hidden+1):

            w = self.weights[f"W{layer_n}"].T
            z = cache[f"Z{layer_n-1}"].T
            b = self.weights[f"b{layer_n}"]
            cache[f"A{layer_n}"] = (w @ z).T + b
            cache[f"Z{layer_n}"] = self.activation(cache[f"A{layer_n}"])

        w = self.weights[f"W{self.n_hidden+1}"].T
        z = cache[f"Z{self.n_hidden}"].T
        b = self.weights[f"b{self.n_hidden+1}"]

        cache[f"A{self.n_hidden+1}"] = (w @ z).T + b
        cache[f"Z{self.n_hidden+1}"] = self.softmax(cache[f"A{self.n_hidden+1}"])
        return cache

    def backward(self, cache, labels):
        grads = {}
        # grads is a dictionary with keys dAm, dWm, dbm, dZ(m-1), dA(m-1), ..., dW1, db1
        loss = cache[f"Z{self.n_hidden+1}"] - labels
        size = len(labels)
        grads[f"dA{self.n_hidden+1}"] = loss
        grads[f"dW{self.n_hidden+1}"] = 1/size * (grads[f"dA{self.n_hidden+1}"].T @ cache[f"Z{self.n_hidden}"]).T
        grads[f"db{self.n_hidden+1}"] = 1/size * np.sum(loss, axis=0, keepdims=True)

        for layer_n in range(self.n_hidden, 0, -1):

            grads[f"dZ{layer_n}"] = (grads[f"dA{layer_n + 1}"] @ self.weights[f"W{layer_n + 1}"].T)
            grads[f"dA{layer_n}"] = self.activation(cache[f"A{layer_n}"], True) * grads[f"dZ{layer_n}"]
            grads[f"dW{layer_n}"] = 1/size * (grads[f"dA{layer_n}"].T @ cache[f"Z{layer_n-1}"]).T
            grads[f"db{layer_n}"] = 1/size *np.sum(grads[f"dA{layer_n}"], axis=0, keepdims=True)

        return grads

    def update(self, grads):
        for layer in range(1, self.n_hidden + 2):
            self.weights[f"W{layer}"] -= self.lr*grads[f"dW{layer}"]
            self.weights[f"b{layer}"] -= self.lr*grads[f"db{layer}"]

    def one_hot(self, y):
        tmp = np.zeros((len(y), self.n_classes), dtype=np.int8)

        for i in range(len(y)):
            tmp[i, int(y[i])] = 1

        return tmp

    def loss(self, prediction, labels):
        prediction[np.where(prediction < self.epsilon)] = self.epsilon
        prediction[np.where(prediction > 1 - self.epsilon)] = 1 - self.epsilon
        return -1/len(labels) * np.sum(labels*np.log(prediction))

    def compute_loss_and_accuracy(self, X, y):
        one_y = self.one_hot(y)
        cache = self.forward(X)
        predictions = np.argmax(cache[f"Z{self.n_hidden + 1}"], axis=1)
        accuracy = np.mean(y == predictions)
        loss = self.loss(cache[f"Z{self.n_hidden + 1}"], one_y)
        return loss, accuracy, predictions

    def train_loop(self, n_epochs):
        X_train, y_train = self.train
        y_onehot = self.one_hot(y_train)
        dims = [X_train.shape[1], y_onehot.shape[1]]
        self.initialize_weights(dims)

        n_batches = int(np.ceil(X_train.shape[0] / self.batch_size))

        for epoch in range(n_epochs):
            for batch in range(n_batches):
                minibatchX = X_train[self.batch_size * batch:self.batch_size * (batch + 1), :]
                minibatchY = y_onehot[self.batch_size * batch:self.batch_size * (batch + 1), :]
                cache = self.forward(minibatchX)
                grads = self.backward(cache, minibatchY)
                self.update(grads)

            X_train, y_train = self.train
            train_loss, train_accuracy, _ = self.compute_loss_and_accuracy(X_train, y_train)
            X_valid, y_valid = self.valid
            valid_loss, valid_accuracy, _ = self.compute_loss_and_accuracy(X_valid, y_valid)

            self.train_logs['train_accuracy'].append(train_accuracy)
            self.train_logs['validation_accuracy'].append(valid_accuracy)
            self.train_logs['train_loss'].append(train_loss)
            self.train_logs['validation_loss'].append(valid_loss)
            print("Epoch " + str(epoch) + " - Loss : " + str(valid_loss) + " - Accuracy : " + str(valid_accuracy))
        return self.train_logs

    def evaluate(self):
        print("Computing accuracy on test data : ")
        X_test, y_test = self.test
        test_loss, test_accuracy, _ = self.compute_loss_and_accuracy(X_test, y_test)
        return test_loss, test_accuracy

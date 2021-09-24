from hilo_io import *
from hilo_data import *

import scipy.stats as scistats
import matplotlib.pyplot as plt
import numpy as np

bit_results, bit_results_batch_size = results("bitResults_2021-08-24_13-09-34.txt", NRP_PATH)
model_results, model_results_batch_size = results("modelResults_2021-08-24_13-09-34.txt", NRP_PATH)


# Calculate speedup by dividing the results with eachother
def plot_results():
    x = np.arange(1000.0, 11_000.0, 1000.0)

    fig = plt.figure()
    ax = fig.add_subplot(111)

    br = focus_on_n_in_tuple(bit_results, TIME_N)
    mr = focus_on_n_in_tuple(model_results, TIME_N)

    for i in range(0, bit_results_batch_size):
        rb = []
        for row in br:
            rb.append(row[0][i])
        ax.scatter(x, rb, s=5, c="g", marker="o")

    for i in range(0, model_results_batch_size):
        rm = []
        for row in mr:
            rm.append(row[0][i])
        ax.scatter(x, rm, s=5, c="b", marker="s")

    plt.legend(loc='upper left')
    plt.show()


def speedup():
    br = focus_on_n_in_tuple(bit_results, TIME_N)
    mr = focus_on_n_in_tuple(model_results, TIME_N)

    speedup_values = []

    for row_index, row in enumerate(br):
        speedup_row = []
        for column_index, column in enumerate(row):
            speedup_column = []
            for batch_index, batch in enumerate(column):
                speedup_column.append(mr[row_index][column_index][batch_index]/batch)
            speedup_row.append(speedup_column)
        speedup_values.append(speedup_row)

    flattened_speedup_values = flatten_cube(speedup_values)
    print(flattened_speedup_values)
    print(np.mean(flattened_speedup_values))
    print("Min:", min(flattened_speedup_values), " Max:", max(flattened_speedup_values))


def ttesting():
    result = scistats.ttest_ind(flatten_cube_of_tuples(bit_results, HYPERVOLUME_N), flatten_cube_of_tuples(model_results, HYPERVOLUME_N))
    print(result)


def main():
    print(bit_results)
    print(model_results)

    ttesting()
    speedup()
    # plot_results()


main()

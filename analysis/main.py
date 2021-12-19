from hilo_io import *
from hilo_data import *

import scipy.stats as scistats
import matplotlib.pyplot as plt
import numpy as np

# START NRP 5-cus-25-req-63-sa RESULTS
# eval_start = 10_000
# eval_end = 30_000
# eval_step = 10_000
#
# pop_start = 500
# pop_end = 2000
# pop_step = 500
#
# bit_results, bit_results_batch_size = results("bitResults_2021-11-04_19-02-44.txt", NRP_PATH + "\\5-cus-25-req-63-sa")
# model_results, model_results_batch_size = results("modelResults_2021-11-04_19-02-44.txt", NRP_PATH + "\\5-cus-25-req-63-sa")
# END NRP 5-cus-25-req-63-sa RESULTS

# START NRP 25-cus-50-req-203-sa SMALL EVALS RESULTS
eval_start = 1000
eval_end = 10_000
eval_step = 1000

pop_start = 200
pop_end = 1000
pop_step = 200

bit_results, bit_results_batch_size = results("bitResults_2021-08-24_13-09-34.txt", NRP_PATH + "\\25-cus-50-req-203-sa")
model_results, model_results_batch_size = results("modelResults_2021-08-24_13-09-34.txt", NRP_PATH + "\\25-cus-50-req-203-sa")
# END NRP 25-cus-50-req-203-sa SMALL EVALS RESULTS

# START NRP 25-cus-50-req-203-sa BIG EVALS RESULTS
# eval_start = 10_000
# eval_end = 50_000
# eval_step = 10_000
#
# pop_start = 1000
# pop_end = 1000
# pop_step = 1000
#
# bit_results, bit_results_batch_size = results("bitResults_2021-10-28_17-48-38.txt", NRP_PATH + "\\25-cus-50-req-203-sa")
# model_results, model_results_batch_size = results("modelResults_2021-10-28_17-48-38.txt", NRP_PATH + "\\25-cus-50-req-203-sa")
# END NRP 25-cus-50-req-203-sa BIG EVALS RESULTS

# Calculate speedup by dividing the results with eachother
def plot_nrp():
    x = np.arange(eval_start, eval_end + eval_step, eval_step)

    fig = plt.figure()
    plt.xlabel("Amount of evaluations with start value " + str(eval_start) + ", end value " + str(eval_end) + ", and step value " + str(eval_step))
    plt.ylabel("Time in nanoseconds")

    br = focus_on_n_in_tuple(bit_results, TIME_N)
    mr = focus_on_n_in_tuple(model_results, TIME_N)

    curpop = pop_start
    for index, column in enumerate(np.array(br).transpose(1, 0, 2)):
        line = []
        for values in column:
            line.append(values.mean())
        plt.plot(x, line, color=(0.0+(0.1*index), 1.0-(0.1*index), 0.0+(0.1*index)), label="enc-p"+str(curpop))
        curpop += pop_step

    curpop = pop_start
    for index, column in enumerate(np.array(mr).transpose(1, 0, 2)):
        line = []
        for values in column:
            line.append(values.mean())
        plt.plot(x, line, color=(0.0+(0.1*index), 0.0+(0.1*index), 1.0-(0.01*index)), label="model-p"+str(curpop))
        curpop += pop_step

    plt.legend()
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
    print("Average speedup:", np.mean(flattened_speedup_values))
    print("Min:", min(flattened_speedup_values), " Max:", max(flattened_speedup_values))


def ttesting():
    flattened_br = np.array(flatten_cube_of_tuples(bit_results, HYPERVOLUME_N))
    nozeroes_br = flattened_br[flattened_br != 0.0]

    flattened_mr = np.array(flatten_cube_of_tuples(model_results, HYPERVOLUME_N))
    nozeroes_mr = flattened_mr[flattened_mr != 0.0]

    result = scistats.ttest_ind(nozeroes_br, nozeroes_mr)
    print(result)


def main():
    print(bit_results)
    print(model_results)

    ttesting()
    speedup()
    plot_nrp()


main()

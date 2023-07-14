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
# bit_results, bit_results_batch_size = nrp_results("bitResults_2021-11-04_19-02-44.txt", NRP_PATH + "\\5-cus-25-req-63-sa")
# model_results, model_results_batch_size = nrp_results("modelResults_2021-11-04_19-02-44.txt", NRP_PATH + "\\5-cus-25-req-63-sa")
# END NRP 5-cus-25-req-63-sa RESULTS

# START NRP 25-cus-50-req-203-sa SMALL EVALS RESULTS
# eval_start = 1000
# eval_end = 10_000
# eval_step = 1000
#
# pop_start = 200
# pop_end = 1000
# pop_step = 200
#
# bit_results, bit_results_batch_size = nrp_results("bitResults_2021-08-24_13-09-34.txt", NRP_PATH + "\\25-cus-50-req-203-sa")
# model_results, model_results_batch_size = nrp_results("modelResults_2021-08-24_13-09-34.txt", NRP_PATH + "\\25-cus-50-req-203-sa")
# END NRP 25-cus-50-req-203-sa SMALL EVALS RESULTS

# START NRP 25-cus-50-req-203-sa BIG EVALS RESULTS
eval_start = 10_000
eval_end = 50_000
eval_step = 10_000

pop_start = 1000
pop_end = 1000
pop_step = 1000

bit_results, bit_results_batch_size = nrp_results("bitResults_2021-10-28_17-48-38.txt", NRP_PATH + "\\25-cus-50-req-203-sa")
model_results, model_results_batch_size = nrp_results("modelResults_2021-10-28_17-48-38.txt", NRP_PATH + "\\25-cus-50-req-203-sa")
# END NRP 25-cus-50-req-203-sa BIG EVALS RESULTS

# Calculate speedup by dividing the results with eachother
# def plot_nrp():
#     x = np.arange(eval_start, eval_end + eval_step, eval_step)
#
#     fig = plt.figure()
#     plt.xlabel("Amount of evaluations with start value " + str(eval_start) + ", end value " + str(eval_end) + ", and step value " + str(eval_step))
#     plt.ylabel("Time in nanoseconds")
#
#     br = focus_on_n_in_tuple(bit_results, TIME_N)
#     mr = focus_on_n_in_tuple(model_results, TIME_N)
#
#     curpop = pop_start
#     for index, column in enumerate(np.array(br).transpose(1, 0, 2)):
#         line = []
#         for values in column:
#             line.append(values.mean())
#         plt.plot(x, line, color=(0.0+(0.1*index), 1.0-(0.1*index), 0.0+(0.1*index)), label="enc-p"+str(curpop))
#         curpop += pop_step
#
#     curpop = pop_start
#     for index, column in enumerate(np.array(mr).transpose(1, 0, 2)):
#         line = []
#         for values in column:
#             line.append(values.mean())
#         plt.plot(x, line, color=(0.0+(0.1*index), 0.0+(0.1*index), 1.0-(0.01*index)), label="model-p"+str(curpop))
#         curpop += pop_step
#
#     plt.legend()
#     plt.show()


def plot_nrp_boxplots_individual():
    fig, ax = plt.subplots()

    ax.set_title("Total time taken")
    ax.set_xlabel("Different models with results from the encoding and the model")
    ax.set_ylabel("Time in nanosecond(s)")

    to_plot = []
    labels = []
    colors = []

    # cureval = eval_start
    # for row in focus_on_n_in_tuple(bit_results, TIME_N):
    curpop = pop_start
    for column in focus_on_n_in_tuple(bit_results, TIME_N)[0]:
        to_plot.append(column)
        labels.append("enc-" + str(curpop) + "p")
        colors.append('lightblue')
        curpop += pop_step
    # cureval += eval_step

    # cureval = eval_start
    # for row in focus_on_n_in_tuple(model_results, TIME_N):
    curpop = pop_start
    for column in focus_on_n_in_tuple(model_results, TIME_N)[0]:
        to_plot.append(column)
        labels.append("mod-" + str(curpop) + "p")
        colors.append('beige')
        curpop += pop_step
    # cureval += eval_step

    bp = ax.boxplot(to_plot, patch_artist=True)
    for patch, color in zip(bp['boxes'], colors):
        patch.set(facecolor = color)
    ax.set_xticklabels(labels)
    plt.show()

    # PART TWO
    fig, ax = plt.subplots()

    ax.set_title("Total time taken")
    ax.set_xlabel("Different models with results from the encoding and the model")
    ax.set_ylabel("Time in nanosecond(s)")

    to_plot = []
    labels = []
    colors = []

    cureval = eval_start
    for row in focus_on_n_in_tuple(bit_results, TIME_N):
        curpop = pop_start
        # for column in focus_on_n_in_tuple(bit_results, TIME_N)[0]:
        to_plot.append(row[0])
        labels.append("enc-" + str(cureval) + "e")
        colors.append('lightblue')
        curpop += pop_step
        cureval += eval_step

    cureval = eval_start
    for row in focus_on_n_in_tuple(model_results, TIME_N):
        # curpop = pop_start
        # for column in focus_on_n_in_tuple(model_results, TIME_N)[0]:
        to_plot.append(row[0])
        labels.append("mod-" + str(cureval) + "e")
        colors.append('beige')
        # curpop += pop_step
        cureval += eval_step

    bp = ax.boxplot(to_plot, patch_artist=True)
    for patch, color in zip(bp['boxes'], colors):
        patch.set(facecolor=color)
    ax.set_xticklabels(labels)
    plt.show()


def plot_nrp_boxplots_combined_pop():
    fig, ax = plt.subplots()

    ax.set_title("Total time taken")
    ax.set_xlabel("Different models with results from the encoding and the model")
    ax.set_ylabel("Time in nanosecond(s)")

    labels_bit = []
    to_plot_bit = []
    cureval = eval_start
    for row in focus_on_n_in_tuple(bit_results, TIME_N):
        curpop = pop_start
        combined_columns = []
        for column in row:
            combined_columns.extend(column)
            curpop += pop_step
        to_plot_bit.append(combined_columns)
        labels_bit.append("e" + str(cureval/1_000) + "k")
        cureval += eval_step

    labels_model = []
    to_plot_model = []
    cureval = eval_start
    for row in focus_on_n_in_tuple(model_results, TIME_N):
        curpop = pop_start
        combined_columns = []
        for column in row:
            combined_columns.extend(column)
            curpop += pop_step
        to_plot_model.append(combined_columns)
        labels_model.append("m" + str(cureval/1_000) + "k")
        cureval += eval_step

    to_plot = [sub[item] for item in range(len(to_plot_model))
                      for sub in [to_plot_bit, to_plot_model]]
    labels = [sub[item] for item in range(len(labels_model))
                      for sub in [labels_bit, labels_model]]

    bp = ax.boxplot(to_plot, patch_artist=True)
    alternating = True
    for patch in bp['boxes']:
        if alternating:
            patch.set(facecolor='lightblue')
        else:
            patch.set(facecolor='beige')
        alternating = not alternating
    ax.set_xticklabels(labels)
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


def calc_mean_and_std_per_eval():
    br = focus_on_n_in_tuple(bit_results, TIME_N)
    mr = focus_on_n_in_tuple(model_results, TIME_N)

    for row_index, row in enumerate(br):
        print("evaluations: {}".format(eval_start * (row_index + 1)))
        br_time_array = []
        mr_time_array = []
        for column_index, column in enumerate(row):
            # br_time_array.extend(column)
            # mr_time_array.extend(mr[row_index][column_index])
            br_time_array.extend(map(divide_ns_to_s, column))
            mr_time_array.extend(map(divide_ns_to_s, mr[row_index][column_index]))

            # PRINTS FOR SHOWING THAT POPSIZE IS IRRELEVANT AS BIGGER JUST MEANS EACH MEMBER WILL BE EVALUATED LESS
            # print("popsize: {}".format(pop_start * (column_index + 1)))
            # print("ENCODING, MEAN: {}, STD: {}".format(np.mean(column), np.std(column)))
            # print("BASELINE, MEAN: {}, STD: {}".format(np.mean(mr[row_index][column_index]), np.std(mr[row_index][column_index])))
            # print("")
        # PRINTS FOR SHOWING COMBINED MEANS AND STD FOR TIME PER EVALUATION SIZE
        print("ENCODING, MEAN: {}, STD: {}".format(np.mean(br_time_array), np.std(br_time_array)))
        print("BASELINE, MEAN: {}, STD: {}".format(np.mean(mr_time_array), np.std(mr_time_array)))
        print(scistats.ttest_ind(br_time_array, mr_time_array))
        print("")
    # time_br_flat = np.array(flatten_cube_of_tuples(bit_results, TIME_N))
    # time_mr_flat = np.array(flatten_cube_of_tuples(model_results, TIME_N))
    # print(scistats.ttest_ind(time_br_flat, time_mr_flat))


def main():
    # print(bit_results)
    # print(model_results)

    calc_mean_and_std_per_eval()
    ttesting()
    # speedup()
    # plot_nrp_boxplots_individual()
    # plot_nrp_boxplots_combined_pop()

main()

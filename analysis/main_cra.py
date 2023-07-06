from hilo_io import *
from hilo_data import *

import scipy.stats as scistats
import matplotlib.pyplot as plt
import numpy as np

# modela_encores = cra_results("encodingResults2023-07-06_13-19-12.txt", CRA_PATH + "\\slim-results\\modela")
# modela_mdeores = cra_results("mdeoResults2023-07-06_13-19-31.txt", CRA_PATH + "\\slim-results\\modela")
#
# modelb_encores = cra_results("encodingResults2023-07-06_13-21-36.txt", CRA_PATH + "\\slim-results\\modelb")
# modelb_mdeores = cra_results("mdeoResults2023-07-06_13-22-27.txt", CRA_PATH + "\\slim-results\\modelb")
#
# modelc_encores = cra_results("encodingResults2023-07-06_13-24-31.txt", CRA_PATH + "\\slim-results\\modelc")
# modelc_mdeores = cra_results("mdeoResults2023-07-06_13-28-08.txt", CRA_PATH + "\\slim-results\\modelc")
#
modeld_encores = cra_results("encodingResults2023-07-06_13-31-57.txt", CRA_PATH + "\\slim-results\\modeld")
modeld_mdeores = cra_results("mdeoResults2023-07-06_14-04-24.txt", CRA_PATH + "\\slim-results\\modeld")

modeld_encores = cra_results("encodingResults2023-07-06_15-27-29.txt", CRA_PATH + "")
modeld_mdeores = cra_results("mdeoResults2023-07-06_16-04-56.txt", CRA_PATH + "")


modela_encores = cra_results("encodingResults2021-11-03_18-12-54.txt", CRA_PATH + "\\modela")
modela_mdeores = cra_results("mdeoResults2021-11-03_18-15-31.txt", CRA_PATH + "\\modela")

modelb_encores = cra_results("encodingResults2021-11-03_19-15-06.txt", CRA_PATH + "\\modelb")
modelb_mdeores = cra_results("mdeoResults2021-11-03_19-19-50.txt", CRA_PATH + "\\modelb")

modelc_encores = cra_results("encodingResults2021-11-03_16-41-34.txt", CRA_PATH + "\\modelc")
modelc_mdeores = cra_results("mdeoResults2021-11-03_16-54-54.txt", CRA_PATH + "\\modelc")

# modeld_encores = cra_results("encodingResults2021-11-04_10-35-12.txt", CRA_PATH + "\\modeld")
# modeld_mdeores = cra_results("mdeoResults2021-11-04_11-40-27.txt", CRA_PATH + "\\modeld")

modele_encores = cra_results("encodingResults2021-11-03_17-13-15.txt", CRA_PATH + "\\modele")
modele_mdeores = cra_results("mdeoResults2021-11-03_21-54-32.txt", CRA_PATH + "\\modele")


def show_plot(aspect, description, title):
    # fig, ax = plt.subplots()
    #
    # ax.set_title(title)
    # ax.set_xlabel("Different models with results from the encoding and MDEOptimiser")
    # ax.set_ylabel(description)
    # bp1 = ax.boxplot([modela_encores[aspect], modela_mdeores[aspect],
    #             modelb_encores[aspect], modelb_mdeores[aspect],
    #             modelc_encores[aspect], modelc_mdeores[aspect]], patch_artist=True)
    # alternating = True
    # for patch in bp1['boxes']:
    #     if alternating:
    #         patch.set(facecolor='lightblue')
    #     else:
    #         patch.set(facecolor='beige')
    #     alternating = not alternating
    #
    # ax.set_xticklabels(["A-Encoding", "A-MDEOptimiser", "B-Encoding", "B-MDEOptimiser", "C-Encoding", "C-MDEOptimiser"])
    # plt.show()

    # Part two
    fig, ax = plt.subplots()

    ax.set_title(title)
    ax.set_xlabel("Averages for different models with both results from the encoding and MDEOptimiser")
    ax.set_ylabel(description)
    bp1 = ax.boxplot([modeld_encores[aspect], modeld_mdeores[aspect],
                modele_encores[aspect], modele_mdeores[aspect]], patch_artist=True)
    alternating = True
    for patch in bp1['boxes']:
        if alternating:
            patch.set(facecolor='lightblue')
        else:
            patch.set(facecolor='beige')
        alternating = not alternating
    ax.set_xticklabels(["D-Encoding", "D-MDEOptimiser", "E-Encoding", "E-MDEOptimiser"])
    plt.show()


def main():
    show_plot("timeTaken", "Time in second(s)", "Overall time taken")
    show_plot("copy", "Time in nanosecond(s)", "Time for copying the population")
    show_plot("mutation", "Time in nanosecond(s)", "Time for mutating the population")
    show_plot("evaluation", "Time in nanosecond(s)", "Time for evaluating members of the population")
    show_plot("matching", "Time in nanosecond(s)", "Time for matching a Henshin rule to a member of the population")

    print("TTESTING A", scistats.ttest_ind(modela_encores["bestFitness"], modela_mdeores["bestFitness"]))
    print("TTESTING B", scistats.ttest_ind(modelb_encores["bestFitness"], modelb_mdeores["bestFitness"]))
    print("TTESTING C", scistats.ttest_ind(modelc_encores["bestFitness"], modelc_mdeores["bestFitness"]))
    print("TTESTING D", scistats.ttest_ind(modeld_encores["bestFitness"], modeld_mdeores["bestFitness"]))
    print("TTESTING E", scistats.ttest_ind(modele_encores["bestFitness"], modele_mdeores["bestFitness"]))

main()
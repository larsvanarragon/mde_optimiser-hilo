import re

NRP_PATH = "..\\problems\\nrp\\results\\experiments"
CRA_PATH = "..\\problems\\cra\\results"


def cra_results(filename, relative_path):
    file = open(relative_path + "\\" + filename)

    returnDict = {}
    returnDict["timeTaken"] = []
    returnDict["bestFitness"] = []
    returnDict["matching"] = []
    returnDict["mutation"] = []
    returnDict["copy"] = []
    returnDict["evaluation"] = []
    returnDict["entireMutation"] = []


    for line in file:
        if "AVERAGES" in line:
            return returnDict
        if line is '':
            continue
        if "ModelInstance" in line:
            returnDict["ModelInstance"] = line.split(" ")[1]
            continue
        cra_parse_line(line, returnDict)


def cra_parse_line(line, dict):
    for part in line.split(","):
        if part is "":
            continue
        data = re.findall(r'[-]?[\d]+[.]?[\d]*[E]?[\d]*', part)
        if len(data) is not 1:
            print("ERROR DATA EMPTY, DATA:", data, "FROM PART", part)
        data = float(data[0])

        if "timeTaken" in part:
            dict["timeTaken"].append(data)
        elif "bestFitness" in part:
            dict["bestFitness"].append(data)
        elif "matching" in part:
            dict["matching"].append(data)
        elif "mutation" in part:
            dict["mutation"].append(data)
        elif "copy" in part:
            dict["copy"].append(data)
        elif "evaluation" in part:
            dict["evaluation"].append(data)
        elif "entireMutation" in part:
            dict["entireMutation"].append(data)
        else:
            print("ERROR CAN'T PARSE DATA PART")


def nrp_results(filename, relative_path):
    file = open(relative_path + "\\" + filename)
    result = []

    for line in file:
        if "END" in line:
            return result, len(result[0][0])
        if line is '':
            continue
        result.append(nrp_line_to_results(line))

    return result, len(result[0][0])


def nrp_line_to_results(line):
    result = []

    line = line.strip(' ')
    for batch_results in line.split(','):
        if batch_results.isspace():
            continue

        batch = []
        for r in batch_results.split('-'):
            time_hv_tuple = (nrp_read_time(r), nrp_read_hypervolume(r))
            batch.append(time_hv_tuple)
        result.append(batch)

    return result


def nrp_read_time(string_tuple):
    if string_tuple.isspace():
        return

    result = ""
    reading = False

    for char_index in range(len(string_tuple)):
        if string_tuple[char_index] is not 't' and not reading:
            continue
        elif not reading:
            reading = True
            continue

        if string_tuple[char_index].isdigit() and reading:
            result += string_tuple[char_index]
        elif reading:
            return int(result)


def nrp_read_hypervolume(string_tuple):
    if string_tuple.isspace():
        return

    result = ""
    reading = False

    for char_index in range(len(string_tuple)):
        if string_tuple[char_index] is not 'v' and string_tuple[char_index-1] is not 'h' and not reading:
            continue
        elif not reading:
            reading = True
            continue

        if (string_tuple[char_index].isdigit() or string_tuple[char_index] is '.') and reading:
            result += string_tuple[char_index]
        elif reading:
            # return result
            return float(result)

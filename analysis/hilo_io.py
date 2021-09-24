
NRP_PATH = "..\\problems\\nrp\\results\\experiments"


def results(filename, relative_path):
    file = open(relative_path + "\\" + filename)
    result = []

    for line in file:
        if "END" in line:
            return result, len(result[0][0])
        if line is '':
            continue
        result.append(line_to_results(line))

    return result, len(result[0][0])


def line_to_results(line):
    result = []

    line = line.strip(' ')
    for batch_results in line.split(','):
        if batch_results.isspace():
            continue

        batch = []
        for r in batch_results.split('-'):
            time_hv_tuple = (read_time(r), read_hypervolume(r))
            batch.append(time_hv_tuple)
        result.append(batch)

    return result


def read_time(string_tuple):
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


def read_hypervolume(string_tuple):
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

TIME_N = 0
HYPERVOLUME_N = 1


# result_data must be a 3d array of tuples of min size n + 1.
def flatten_cube_of_tuples(data, n):
    result = []

    for row in data:
        for column in row:
            for tuple in column:
                result.append(tuple[n])

    return result


def flatten_cube(data):
    result = []

    for row in data:
        for column in row:
            for value in column:
                result.append(value)

    return result


def focus_on_n_in_tuple(data, n):
    result = []

    for row in data:
        new_row = []
        for column in row:
            new_column = []
            for tuple in column:
                new_column.append(tuple[n])
            new_row.append(new_column)
        result.append(new_row)

    return result


def divide_ns_to_s(nanoseconds):
    return nanoseconds/1_000_000_000


def divide_ns_to_millisecond(nanoseconds):
    return nanoseconds/1_000_000


def divide_ns_to_microsecond(nanoseconds):
    return nanoseconds/1_000

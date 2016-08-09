# Coder Night 8/2016 code

# code jam Problem A. Bot Trust
# https://code.google.com/codejam/contest/975485/dashboard

from os import remove
from itertools import izip
import timing

def read_input_file(filename):
    cases = []
    # Read all of file and delete top row (number of rows: who cares?)
    with open(filename) as f:
        lines = f.readlines()
        lines = lines[1:]
    # Build a list of cases with each case a list of moves (a list of lists).
    for line in lines:
        case = []
        case_list = line.split(' ')
        # Remove number of moves from each case (who cares)?
        case_list = case_list[1:]
        for s, i in izip(case_list[::2], case_list[1::2]):
            case.append([s, int(i)])
        cases.append(case)
    return cases


def write_file(filename, output):
    with open(filename, 'a') as f:
        f.write(output)


def remove_file(filename):
    try:
        remove(filename)
    except OSError:
        pass


def move_robot(r, p, clock_time, robot):
    lasttime = robot[r][1]
    extra_movement_time = abs(robot[r][0] - p) - (clock_time - lasttime)
    if extra_movement_time < 0:
        extra_movement_time = 0
    clock_time = clock_time + extra_movement_time + 1
    robot[r] = [p, clock_time]
    return robot, clock_time


def simulate_cases(cases_list):
    number_of_cases = 0
    string_output = ""
    for test_case in cases_list:
        number_of_cases += 1
        time = 0
        # For each case, initialize dict with both robots at position one at time zero.
        robot = {'O': [1, 0], 'B': [1, 0]}
        for move in test_case:
            robot, time = move_robot(move[0], move[1], time, robot)
        #print 'Case #%s: %s\n' % (number_of_cases, time)
        string_output += ('Case #%s: %s\n' % (number_of_cases, time))
    return string_output

if __name__ == '__main__':
    # Input data and initialize
    #cases_list = read_input_file('A-small-practice.in')
    #output_filename = 'A-small-practice.out'
    cases_list = read_input_file('A-large-practice.in')
    output_filename = 'A-large-practice.out'
    remove_file(output_filename)
    output_as_string = simulate_cases(cases_list)
    write_file(output_filename, output_as_string)

        # test cases
        # expect 6
        # test_case = [['O', 2], ['B', 1], ['B', 2], ['O', 4]]
        # expect 92
        #test_case = [['O', 5], ['O', 8], ['B', 100]]
        # expect 4
        #test_case = [['B', 2], ['B', 1]]
        # expect 1
        #test_case = [['B', 1]]




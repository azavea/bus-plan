"""
Get student ride times from solver output
"""

import os
import sys
import pandas as pd


def get_route_data(route_input):
    """
    get a dataset of route-stop combinations and associated ride times
    """

    def replace_missing_stops(route_subset):
        """
        Add in rows for between nodes that were not routed due to an exception
        (rare)
        """
        prev = 0
        before_row = None
        rs = route_subset
        for index, r in route_subset.iterrows():
            if r.route_sequence - 1 != prev:
                new_row = before_row
                new_row.origin_id = new_row.destination_id
                new_row.destination_id = r.origin_id
                new_row.route_sequence = r.route_sequence - 1
                rs = rs.append(new_row)
            before_row = r
            prev += 1
        rs = rs.sort_values('route_sequence')
        return rs[['origin_id', 'time']]

    rt = pd.read_csv(route_input).sort_values(
        ['route_id', 'route_sequence', 'stop_sequence'])
    rt = rt[rt.origin_id != rt.destination_id]
    rt_school = rt[rt['destination_id'].str.startswith('school')]
    # get the last leg of trip
    rt_school = rt_school.groupby(['route_id']).last().reset_index()
    rt_school = rt_school[['route_id', 'time']]
    rt_school.columns = ['route_id', 'arrival_time']
    rt_pickup = rt[rt.stop_sequence == 0]
    rt_pickup = rt_pickup.groupby(['route_id']).apply(
        replace_missing_stops).reset_index().drop('level_1', 1)
    rt_out = pd.merge(rt_pickup, rt_school, how='left', on='route_id')
    rt_out['duration'] = rt_out['arrival_time'] - rt_out['time']
    return rt_out[['route_id', 'origin_id', 'duration']].astype(str)


def get_student_stop_data(student_stop_input):
    """
    Get a DataFrame of students and their assigned route-stop combinations
    """
    with open(student_stop_input) as f:
        st = []
        for line in f:
            ln = line.split(',')
            stop = ln[0:2]
            for student in ln[2:]:
                st.append(stop + [student.rstrip('\n')])
    df = pd.DataFrame(st, columns=['route_id', 'origin_id', 'student_id'])
    return df


def get_student_ride_times(route_input, student_stop_input):
    """
    Join students to stops for a dataset if student ride times
    """
    route_times = get_route_data(route_input)
    student_times = get_student_stop_data(student_stop_input)
    student_ride_times = pd.merge(student_times, route_times, how='left')
    return student_ride_times.drop_duplicates('student_id')


def write_student_ride_times(route_input, student_stop_input, outfile):
    student_ride_times = get_student_ride_times(
        route_input, student_stop_input)
    student_ride_times.to_csv(outfile, index=False)


def write_student_ride_times_from_dir(plan_dir):
    route_input = os.path.join(plan_dir, 'OUTPUT_router.csv')
    student_stop_input = os.path.join(
        plan_dir, 'OUTPUT_solver_student_assignment.csv')
    output_file = os.path.join(
        plan_dir, 'OUTPUT_analysis_student_ridetimes.csv')
    student_ride_times = get_student_ride_times(
        route_input, student_stop_input)
    student_ride_times.to_csv(output_file, index=False)

if __name__ == '__main__':
    #
    # example run : $ python student_ride_times.py <router output csv>
    #   <student-stop assignment csv> <output ride time csv>
    #

    if len(sys.argv) != 4:
        print('[ ERROR ] you must supply 3 arguments: <router output csv>, ' +
              '<student-stop assignment csv>, <output ride time csv>')
        sys.exit(1)
    else:
        write_student_ride_times(sys.argv[1], sys.argv[2], sys.argv[3])

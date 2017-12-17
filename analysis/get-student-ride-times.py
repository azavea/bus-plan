"""
Get student ride times from solver output
"""

import sys
import pandas as pd

def get_route_data(route_input):
    rt = pd.read_csv(route_input).sort_values(['route_id', 'route_sequence', 'stop_sequence'])
    rt_school = rt.groupby(['route_id']).last().reset_index()
    rt_school = rt_school[['route_id', 'time']]
    rt_school.columns = ['route_id', 'arrival_time']
    rt_pickup = rt[rt.stop_sequence==0]
    rt_pickup = rt_pickup[['route_id', 'origin_id', 'time']]
    rt = pd.merge(rt_pickup, rt_school, how='left', on='route_id')
    rt['duration'] = rt['arrival_time'] - rt['time']
    return rt[['route_id','origin_id', 'duration']].astype(str)

def get_student_stop_data(student_stop_input):
    st = []
    with open(student_stop_input) as f:
        for line in f:
            l = line.split(',')
            stop = l[0:2]
            for student in l[2:]:
                st.append(stop + [student.rstrip('\n')])
    df = pd.DataFrame(st, columns=['route_id', 'origin_id', 'student_id'])
    return df

def get_student_ride_times(route_input, student_stop_input):
    route_times = get_route_data(route_input)
    student_times = get_student_stop_data(student_stop_input)
    return pd.merge(student_times, route_times)

def main(route_input, student_stop_input, outfile):
    student_ride_times = get_student_ride_times(route_input, student_stop_input)
    student_ride_times.to_csv(outfile, index=False)


if __name__ == '__main__':
    #
    # example run : $ python get_student_ride_times.py <router output csv>
    #   <student-stop assignment csv> <output ride time csv>
    #

    if len(sys.argv) != 4:
        print('[ ERROR ] you must supply 3 arguments: <router output csv>, ' +
        '<student-stop assignment csv>, <output ride time csv>')
        sys.exit(1)
    else:
        main(sys.argv[1], sys.argv[2], sys.argv[3])

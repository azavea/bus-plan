"""
Process router/solver output into interpretable 
datasets for analysis
"""
import os
import sys
import pandas as pd
import time as tm
from datetime import datetime
from functools import reduce

import student_ride_times as srt
import map_solver as ms
import drive_distances as dr


def timestamp_routes(plan_dir):
    """Add a timestamp column to a new version of router output"""
    input_file = os.path.join(plan_dir, 'OUTPUT_router.csv')
    output_file = os.path.join(
        plan_dir, 'OUTPUT_analysis_routes_with_timestamps.csv')
    df = pd.read_csv(input_file)

    def unix_to_ts(unix):
        ts = datetime.fromtimestamp(unix)
        return ts.strftime('%Y-%m-%d %H:%M:%S')
    df['date_time'] = df['time'].apply(unix_to_ts)
    df.to_csv(output_file, index=False)


def simplify_routes(plan_dir):
    """Create a simlified version of route dataset including only start
    and stop points"""
    input_file = os.path.join(
        plan_dir, 'OUTPUT_analysis_routes_with_timestamps.csv')
    output_file = os.path.join(
        plan_dir, 'OUTPUT_analysis_routes_simplified.csv')

    df = pd.read_csv(input_file)
    df_start = df.groupby(['route_id', 'route_sequence']).first().reset_index()
    df_start['arrive_or_depart'] = 'depart'
    df_end = df.groupby(['route_id', 'route_sequence']).last().reset_index()
    df_end['arrive_or_depart'] = 'arrive'

    df_startstop = pd.concat([df_end, df_start]).sort_values(
        by=['route_id', 'route_sequence', 'stop_sequence'])
    df_startstop.to_csv(output_file, index=False)


def write_summary_table(plan_dir):
    """Create a table with summary stats on each route"""
    # read route milege and router output csvs
    route_mileage = pd.read_csv(os.path.join(
        plan_dir, 'OUTPUT_analysis_bus_mileage.csv'))
    plan = pd.read_csv(os.path.join(plan_dir, 'OUTPUT_router.csv'))

    # read or cinstruct student ride time df
    student_ride_times_csv = os.path.join(
        plan_dir, 'OUTPUT_analysis_student_ridetimes.csv')
    if os.path.isfile(student_ride_times_csv):
        ride_times_df = pd.read_csv(student_ride_times_csv)
    else:
        route_input = os.path.join(plan_dir, 'OUTPUT_router.csv')
        student_stop_input = os.path.join(
            plan_dir, 'OUTPUT_solver_student_assignment.csv')
        ride_times_df = srt.get_student_ride_times(
            route_input, student_stop_input)

    # Gather summary stats for each route and join into one df
    rtg = ride_times_df.groupby('route_id')
    df_list = [rtg['origin_id'].nunique().reset_index(),
               rtg['student_id'].nunique().reset_index(),
               rtg['duration'].median().reset_index(),
               rtg['duration'].max().reset_index(),
               rtg['duration'].mean().reset_index()]
    summary_table = reduce(lambda x, y: pd.merge(x, y, on='route_id'), df_list)
    summary_table.columns = ['Route Number', '# Stops', '# Riders',
                             'Median Ride Time', 'Maximum Ride Time', 'Average Ride Time']
    to_minutes = lambda x: x / 60
    summary_table['Median Ride Time'] = summary_table[
        'Median Ride Time'].apply(to_minutes)
    summary_table['Maximum Ride Time'] = summary_table[
        'Maximum Ride Time'].apply(to_minutes)
    summary_table['Average Ride Time'] = summary_table[
        'Average Ride Time'].apply(to_minutes)

    # merge drive time and ridership summary tables into one
    summary_table = pd.merge(route_mileage, summary_table, on='Route Number')
    output_file = os.path.join(plan_dir, 'OUTPUT_analysis_summary_table.csv')
    summary_table.to_csv(output_file, index=False)


def main(base_dir, cost_matrix_csv):
    """Write all necessary results datasets from solver and router outputs"""

    dirs = list(filter(lambda x: os.path.isdir(
        os.path.join(base_dir, x)), os.listdir(base_dir)))
    # also try the base directory, enabling this script to
    # work as a one-off on one plan
    dirs += [base_dir]

    # generate the cost matrix once
    cost_matrix = dr.get_cost_matrix(cost_matrix_csv)

    for plan_dir in dirs:
        if 'OUTPUT_router.csv' in os.listdir(plan_dir):
            # Add timestamps to each plan
            timestamp_routes(plan_dir)
            # Create a simplified bus routes csv with only the beginning and
            # end of each leg
            simplify_routes(plan_dir)
            # Create a csv with ride times for each student in the plan
            srt.write_student_ride_times_from_dir(plan_dir)
            # CSV with with mileage for each route in the plan
            dr.write_route_mileage(plan_dir, cost_matrix)
            # Generate map and animation of plan
            routes = os.path.join(plan_dir, 'OUTPUT_router.csv')
            ms.save_maps(routes)
            # Create summary table
            write_summary_table(plan_dir)
            # TODO: OUTPUT_analysis_student_walk_distances.csv


if __name__ == '__main__':
    if len(sys.argv) != 3:
        print(
            '[ ERROR ] you must supply 2 arguments: <plan directory>, <cost matrix csv>')
        sys.exit(1)
    else:
        main(sys.argv[1], sys.argv[2])

import os

import pandas as pd
import numpy as np
import seaborn as sns
import matplotlib.pyplot as plt

import map_solver as ms
import student_ride_times as srt


class BusPlan():
    def __init__(self, routes, stop_assignment, total_students=1056):
        self.route_csv = routes
        self.stop_assignment_csv = stop_assignment
        self.total_students = total_students

        self.ride_times_df = srt.get_student_ride_times(routes, stop_assignment)
        self.ride_times = np.round(pd.to_numeric(
            self.ride_times_df['duration']) / 60)
        self.plan = ms.get_csv(routes)
        self.routed_students = self.get_routed_students()
        self.bus_times = self.get_route_times()

        # subsets
        self.garages = self.plan[self.plan['origin_type'] == 'garage']
        self.schools = self.plan[self.plan['destination_type'] == 'school']
        self.stops = self.plan[self.plan['origin_type'] == 'stop']

        # metrics
        self.bus_metrics = self.get_bus_metrics()
        self.student_metrics = self.get_student_metrics()

    # get school bus/route related metrics
    def get_bus_metrics(self):
        durations = list(self.bus_times.values())
        return {
            'Total buses': self.plan['route_id'].nunique(),
            'Total bus stops': self.stops.groupby(['route_id', 'origin_id']).ngroups,
            'Unique bus stops': self.stops['origin_id'].nunique(),
            'Garages': self.garages['origin_id'].nunique(),
            'Total active bus time (hours)': sum(durations) / 60,
            'Average route time (minutes)': np.mean(durations)
        }

    # get route times for all students
    def get_route_times(self):
        route_durations = {}
        for route in self.plan['route_id'].unique():
            time = self.plan[self.plan['route_id'] == route]['time']
            route_durations[route] = (time.max() - time.min()) / 60
        return route_durations

    # get a count of all students included in this plan
    def get_routed_students(self):
        with open(self.stop_assignment_csv) as f:
            return sum([len(line.split(',')) - 2 for line in f])

    # calculate student related metrics
    def get_student_metrics(self):
        times = np.round(self.ride_times.describe())
        return {
            'Students left behind': self.total_students - self.routed_students,
            'Average ride time': times['mean'],
            'Median ride time': self.ride_times.median(),
            'Standard deviation ride time': times['std'],
            'Maximum ride time': times['max']
        }

    # output static map of plan
    def map_plan(self):
        return ms.map(self.route_csv)

    # density plot of student ride times
    def plot_student_ride_time(self):
        sns.set_style(('whitegrid'))
        plt.figure(figsize=(10, 4))
        dens = sns.kdeplot(self.ride_times, shade=True, color="b")
        dens.set(xlabel='Ride time (minutes)')
        dens.set_title('Proposed distribution of student ride times')
        # Output each plot
        return dens

# creat a dict with plans for each run


def get_all_plans(directory):
    runs = os.listdir(directory)
    bus_plans = {}
    for r in runs:
        run_path = os.path.join(directory, r)
        if os.path.isdir(run_path):
            routes = os.path.join(run_path, 'OUTPUT_router.csv')
            student_assignment = os.path.join(
                run_path, 'OUTPUT_solver_student_assignment.csv')
            bus_plans[r] = BusPlan(routes, student_assignment)
    return bus_plans

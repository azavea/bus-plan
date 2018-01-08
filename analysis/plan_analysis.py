"""
Analyze entire set of potential bus routing plans
"""

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
        self.walk_threshold = self.get_walk_threshold()

        self.ride_times_df = srt.get_student_ride_times(routes, stop_assignment)
        self.ride_times = np.round(pd.to_numeric(
            self.ride_times_df['duration']) / 60)
        self.plan = ms.get_csv(routes)
        self.routed_students = self.get_routed_students()
        self.max_ride_times = self.ride_times_df.sort_values('duration').groupby(
            ['route_id']).last().reset_index()[['route_id', 'duration']]
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
            return sum([len(line.split(',')) - 1 for line in f])

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

    # get walk distance walk distance maximum from directory name
    def get_walk_threshold(self):
        head, tail = os.path.split(os.path.split(self.route_csv)[0])
        s = tail[-9:-6]
        try:
            return float(s)
        except ValueError:
            return 'existing plan'

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


def get_all_plans(directory):
    '''
    Create a dict with a plan for each run
    '''
    runs = os.listdir(directory)
    bus_plans = {}
    for r in runs:
        try:
            run_path = os.path.join(directory, r)
            if os.path.isdir(run_path):
                routes = os.path.join(run_path, 'OUTPUT_router.csv')
                student_assignment = os.path.join(
                    run_path, 'OUTPUT_solver_student_assignment.csv')
                bus_plans[r] = BusPlan(routes, student_assignment)
        except ValueError:
            print('Failed to caluclate student ride time metrics for run ' + r)
    return bus_plans


def scheduled_ride_times(student_file, output_file=None):
    '''
    Get existing scheduled ride times from student file
        -input: chester-students-with-cm-cm-reference.csv
    '''
    students = pd.read_csv(student_file)

    def get_time(s):
        return datetime.strptime(s, '%H:%M:%S')

    # get total ride time
    students['drop_off'] = students['Drop.Off.Time'].apply(get_time)
    students['pick_up'] = students['Pick.Up.Time'].apply(get_time)
    students['scheduled_ride_time'] = (
        students['drop_off'] - students['pick_up']).dt.total_seconds()

    # some records are erroneous, show a pick up time of 1AM, remove those records
    students = students[students['scheduled_ride_time'] > 0]
    # select appropriate fields, rename columns
    students = students[['compass_id', 'Run.Trip.Number', 'stop_id_cm_reference',
                         'scheduled_ride_time']]
    students = students.rename(columns={'Run.Trip.Number': 'route'})
    # to match the otherdatasets
    students['compass_id'] = 'student_' + students['compass_id']
    students['stop_id_cm_reference'] = 'stop_' + \
        students['stop_id_cm_reference'].astype(str)

    if output_file != None:
        students.to_csv(output_file, index_label=False)
    else:
        return students


def summary_table(proposed_plans, existing_plan):
    '''
    Create a summary table data frame
    '''
    def get_results_for_plan(plan, scenario=None):
        sm = pd.DataFrame.from_dict(plan.student_metrics, 'index').transpose()
        bm = pd.DataFrame.from_dict(plan.bus_metrics, 'index').transpose()
        r = pd.concat([sm, bm], 1, ignore_index=True)
        if scenario == None:
            r['scenario'] = plan.walk_threshold
        else:
            r['scenario'] = scenario
        return r
    results = get_results_for_plan(existing_plan, 'existing')
    for k, v in proposed_plans.items():
        results = pd.concat([results, get_results_for_plan(v)], 0, ignore_index=True)
    results.columns = list(existing_plan.student_metrics.keys()) + \
        list(existing_plan.bus_metrics.keys()) + ['scenario']
    return results


def stop_eligibility_counts(eligibility_file):
    with open(eligibility_file) as f:
        return np.array([(len(line.split(',')) - 1) for line in f])


def comparative_ride_times_plot(bus_pWithoutlans, selections, existing_plan):
    '''
    Create a density plot with ride time density for a number of bus bus_plans
    '''
    fig, ax = plt.subplots(figsize=(10, 5))
    colors = {'0.25 mi': '#6497b1', '0.4 mi': '#005b96', '0.5 mi': '#03396c',
              '1.0 / 0.5 mi': '#011f4b', 'existing': '#CD0000'}
    df = pd.DataFrame({'0.25 mi': bus_plans[selections[0]].ride_times,
                       '0.4 mi': bus_plans[selections[1]].ride_times,
                       '0.5 mi': bus_plans[selections[2]].ride_times,
                       '1.0 / 0.5 mi': bus_plans[selections[3]].ride_times,
                       'existing': existing_plan.ride_times}).melt()
    grouped = df.groupby('variable')
    for key, group in grouped:
        group.plot(ax=ax, kind='kde', y='value', label=key, color=colors[key])
    plt.title('Student ride time distirbutions accross all scenarios')
    return plt


def summary_stats_bar_plots(bus_plans, existing_plan):
    '''
    Create a set of facetted bar plots showing the differences in performance
    metrics among all different scenarios
    '''
    st = summary_table(bus_plans, existing_plan)
    mn = st.groupby('scenario').mean()
    mn = mn.drop(['Students left behind', 'Garages',
                  'Standard deviation ride time'], 1)
    return mn.plot.barh(subplots=True, sharex=False, figsize=(14, 18),
                        layout=(4, 2), grid=False, legend=False)

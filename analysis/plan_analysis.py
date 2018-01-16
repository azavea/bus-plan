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
import drive_distances as dr


class BusPlan():
    """ One set of bus routes

    Summary information for a comprehensive bus plan

    Attributes:
        route_csv: path to the router output csv
        stop_assignment_csv: path to student assignment (solver output) csv
        total_students: total number of students being routed in the plan
        walk_threshold: the max distance students are allowed to walk in this
            scenario
        ride_times_df: Pandas DataFrame with ride time for each student
        ride_times: array of student ride times
        plan: Pandas df with full router output
        routed_students: total number of students routed under this plan
        maximum_ride_time: the maximum ride time (seconds) among all students
        bus_times: dict of route ids and associated total drive times
        pal: color palette dict with a fo color each route
        drive_distances: dict of total miles driven for each route in plan
        garages: router output subset for segments starting at a garage
        schools: router output subset for segments ending at a schools
        stops: router output subset for segments starting at a stop
        bus_metrics: dict of summary statistics about the route
        student_metrics: dict of summary statistics about students
    """

    def __init__(self, directory, total_students=1056,
                 cost_matrix=None):
        """Create BusPlan from router output and student assignment output"""
        # input files
        self.route_csv = os.path.join(
            directory, 'OUTPUT_router.csv')
        self.stop_assignment_csv = os.path.join(
            directory, 'OUTPUT_solver_student_assignment.csv')
        self.walk_dist_csv = os.path.join(
            directory, 'OUTPUT_analysis_student_walk_dists.csv')

        # infor
        self.total_students = total_students
        self.walk_threshold = self.get_walk_threshold()

        # datasets
        self.ride_times_df = srt.get_student_ride_times(
            self.route_csv, self.stop_assignment_csv)
        self.plan = ms.get_csv(self.route_csv)

        self.walk_dist_df = pd.read_csv(self.walk_dist_csv)
        self.walk_dist_df['mileage'] = (self.walk_dist_df['distance'] * 3.28085) / 5280
        self.walk_dist_filtered_df = self.filter_walk_dists()
        self.walk_distances = np.array(self.walk_dist_df['mileage'])
        self.walk_distances_filtered = np.array(self.walk_dist_filtered_df['mileage'])

        self.ride_times = np.round(pd.to_numeric(
            self.ride_times_df['duration']) / 60)
        self.routed_students = self.get_routed_students()
        self.max_ride_times = self.ride_times_df.sort_values(
            'duration').groupby(['route_id']).last().reset_index()[['route_id', 'duration']]
        self.bus_times = self.get_route_times()
        self.pal = ms.get_palette(self.plan)
        if (cost_matrix is not None):
            self.drive_distances = dr.route_distances(self.plan, cost_matrix)
        else:
            self.drive_distances = None

        # subsets
        self.garages = self.plan[self.plan['origin_type'] == 'garage']
        self.schools = self.plan[self.plan['destination_type'] == 'school']
        self.stops = self.plan[self.plan['origin_type'] == 'stop']

        # metrics
        self.bus_metrics = self.get_bus_metrics()
        self.student_metrics = self.get_student_metrics()

    def get_bus_metrics(self):
        """get school bus/route related metrics"""
        durations = list(self.bus_times.values())
        return {
            'Total buses': self.plan['route_id'].nunique(),
            'Total bus stops': self.stops.groupby(
                ['route_id', 'origin_id']).ngroups,
            'Unique bus stops': self.stops['origin_id'].nunique(),
            'Garages': self.garages['origin_id'].nunique(),
            'Total active bus time (hours)': sum(durations) / 60,
            'Average route time (minutes)': np.mean(durations),
            'Total mileage': np.sum(list(self.drive_distances.values()))
        }

    def filter_walk_dists(self):
        thresholds = {'existing plan': 1.5, 0.25: 0.25,
                      0.4: 0.4, 0.5: 0.5, 0.82: 0.82, 1.0: 0.5}
        return self.walk_dist_df[self.walk_dist_df['mileage'] < thresholds[self.walk_threshold]]

    def get_route_times(self):
        """get route times for all students"""
        route_durations = {}
        for route in self.plan['route_id'].unique():
            time = self.plan[self.plan['route_id'] == route]['time']
            route_durations[route] = (time.max() - time.min()) / 60
        return route_durations

    def get_routed_students(self):
        """get a count of all students included in this plan"""
        with open(self.stop_assignment_csv) as f:
            lines = [line.split(',') for line in f]
            return sum(map(lambda x: len(x) - 2, lines))

    def get_student_metrics(self):
        """calculate student related metrics"""
        times = np.round(self.ride_times.describe())
        return {
            'Average ride time': times['mean'],
            'Median ride time': self.ride_times.median(),
            'Standard deviation ride time': times['std'],
            'Maximum ride time': times['max']
        }

    def get_walk_threshold(self):
        """get walk distance walk distance maximum from directory name"""
        head, tail = os.path.split(os.path.split(self.route_csv)[0])
        s = tail[-9:-6]
        try:
            return float(s)
        except ValueError:
            return 'existing plan'

    def map(self):
        """output static map of plan"""
        return ms.static_map(self.plan, self.pal)

    def animate(self):
        """output animation of plan"""
        return ms.animate(self.plan, self.pal)

    def pct_routed_to_existing(self, existing_bus_plan):
        """Find the percentage of students picked up at their original stop"""
        p = self.ride_times_df[['origin_id', 'student_id']].rename(
            index=str, columns={'origin_id': 'new'})
        e = existing_bus_plan.ride_times_df[['origin_id', 'student_id']].rename(
            index=str, columns={'origin_id': 'orig'})
        d = pd.merge(p, e, on='student_id')
        t = (d['new'] == d['orig']).value_counts()
        return t.get(True) / len(d)

    def plot_student_ride_time(self):
        """density plot of student ride times"""
        sns.set_style(('whitegrid'))
        plt.figure(figsize=(10, 4))
        dens = sns.kdeplot(self.ride_times, shade=True, color="b")
        dens.set(xlabel='Ride time (minutes)')
        dens.set_title('Proposed distribution of student ride times')
        # Output each plot
        return dens


def get_all_plans(directory, cost_matrix):
    """
    Create a dict with a plan for each run
    """
    runs = os.listdir(directory)
    bus_plans = {}
    for r in runs:
        try:
            run_path = os.path.join(directory, r)
            if os.path.isdir(run_path):
                bus_plans[r] = BusPlan(run_path, cost_matrix=cost_matrix)
        except (ValueError, FileNotFoundError, AttributeError):
            print('Failed to caluclate student ride time metrics for run ' + r)
    return bus_plans


def scheduled_ride_times(student_file, output_file=None):
    """
    Get existing scheduled ride times from student file

    Args:
        student_file: Student datset (csv)
        output_file: output csv of scheduled ride times
    """
    students = pd.read_csv(student_file)

    def get_time(s):
        return datetime.strptime(s, '%H:%M:%S')

    # get total ride time
    students['drop_off'] = students['Drop.Off.Time'].apply(get_time)
    students['pick_up'] = students['Pick.Up.Time'].apply(get_time)
    students['scheduled_ride_time'] = (
        students['drop_off'] - students['pick_up']).dt.total_seconds()

    # some records are erroneous, show a pick up time of 1AM, remove those
    # records
    students = students[students['scheduled_ride_time'] > 0]
    # select appropriate fields, rename columns
    students = students[['compass_id', 'Run.Trip.Number',
                         'stop_id_cm_reference', 'scheduled_ride_time']]
    students = students.rename(columns={'Run.Trip.Number': 'route'})
    # to match the otherdatasets
    students['compass_id'] = 'student_' + students['compass_id']
    students['stop_id_cm_reference'] = 'stop_' + \
        students['stop_id_cm_reference'].astype(str)

    if output_file is not None:
        students.to_csv(output_file, index_label=False)
    else:
        return students


def summary_table(proposed_plans, existing_plan):
    """
    Create a ry table data frame
    """
    def get_results_for_plan(plan, scenario=None):
        sm = pd.DataFrame.from_dict(plan.student_metrics, 'index').transpose()
        bm = pd.DataFrame.from_dict(plan.bus_metrics, 'index').transpose()
        r = pd.concat([sm, bm], 1, ignore_index=True)
        if scenario is None:
            r['scenario'] = plan.walk_threshold
        else:
            r['scenario'] = scenario
        return r
    results = get_results_for_plan(existing_plan, 'existing')
    for k, v in proposed_plans.items():
        results = pd.concat([results, get_results_for_plan(v)], 0,
                            ignore_index=True)
    results.columns = list(existing_plan.student_metrics.keys()) + \
        list(existing_plan.bus_metrics.keys()) + ['scenario']
    return results


def stop_eligibility_counts(eligibility_file):
    """
    Get array eligible stop counts for all students in the dataset
    """
    with open(eligibility_file) as f:
        return np.array([(len(line.split(',')) - 1) for line in f])


def comparative_dist_plot(bus_plans, selections, existing_plan,
                          attribute):
    """
    Create a density plot with ride time density for a number of bus bus_plans
    """
    attributes = {'ride': ['ride_times', 'ride time'],
                  'walk': ['walk_distances', 'walk distance'],
                  'walk_filter': ['walk_distances_filtered', 'walk distance']}
    a = attributes[attribute]
    colors = {'student-walk-0.25m': '#d0d1e6', 'student-walk-0.40m': '#a6bddb',
              'student-walk-0.50m': '#74a9cf', 'student-walk-0.82m': '#045a8d',
              'student-walk-0.50m-or-1.0m': '#2b8cbe', 'existing': '#CD0000'}
    fig, ax = plt.subplots(figsize=(10, 5))
    dfs = [pd.DataFrame({'var': s[:-5], 'val': getattr(bus_plans[s], a[0])})
           for s in selections]
    dfs = dfs + [pd.DataFrame({'var': 'existing', 'val': getattr(existing_plan, a[0])})]
    grouped = pd.concat(dfs).groupby('var')
    for key, group in grouped:
        group.plot(ax=ax, kind='kde', y='val', label=key, color=colors[key])
    plt.title('Student {} distirbutions across all scenarios'.format(a[1]))
    return plt


def summary_stats_bar_plots(bus_plans, existing_plan):
    """
    Create a set of facetted bar plots showing the differences in performance
    metrics among all different scenarios
    """
    st = summary_table(bus_plans, existing_plan)
    mn = st.groupby('scenario').mean()
    mn = mn.drop(['Garages', 'Standard deviation ride time'], 1)
    return mn.plot.barh(subplots=True, sharex=False, figsize=(14, 18),
                        layout=(4, 3), grid=False, legend=False)


def student_stop_eligibility_plots(input_directory):
    """
    Create a distribution plot of the number of stop options for
    """
    f = [stop_eligibility_counts(os.path.join(
        input_directory, 'student-stop-eligibility-{}.csv'.format(s)))
        for s in ['25', '40', '50', '100', '82']]
    fig, ax = plt.subplots(figsize=(12, 7))
    colors = {'0.25 mi': 'green', '0.4 mi': 'blue',
              '0.5 mi': 'red', '1.0 / 0.5 mi': 'black',
              '0.82 mi': 'orange'}
    df = pd.DataFrame({'0.25 mi': f[0], '0.4 mi': f[1],
                       '0.5 mi': f[2], '1.0 / 0.5 mi': f[3],
                       '0.82 mi': f[4]}).melt()
    grouped = df.groupby('variable')
    for key, group in grouped:
        group.plot(ax=ax, kind='kde', y='value', label=key, color=colors[key])
    plt.title('Comparative distributions of candidate' +
              ' stop counts (by scenario)')
    return plt

"""
Get drive distances of all routes within a plan
"""

import os
import numpy as np
import pandas as pd


class RouteDists:

    def __init__(self, id):
        self.id = id
        self.mileage = 0.0
        self.live_mileage = 0.0

    def calc_mileage(self, legs, dists):
        for l in legs:
            self.mileage += dists[l] / 1609.34
            if not l[0].startswith('garage') or l[1].startswith('garage'):
                self.live_mileage += dists[l] / 1609.34
        return [self.mileage, self.live_mileage]


def get_route_mileage(plan_dir, cost_matrix_csv):
    """Get total and live bus mileage as a dictionary"""
    plan = pd.read_csv(os.path.join(
        plan_dir, 'OUTPUT_router.csv')).sort_values(by=['time'])

    routes = {}
    for n, g in plan[['route_id', 'origin_id']].drop_duplicates().groupby('route_id'):
        ln = g['origin_id'].tolist() + [g['origin_id'].tolist()[0]]
        if len(ln) > 3:
            routes[n] = [(ln[i], ln[i + 1]) for i in range(0, len(ln) - 1)]

    cm = np.genfromtxt(cost_matrix_csv, delimiter=',', dtype=None, names=True)
    cost_matrix = {(r[0].decode(), r[1].decode()): r[3] for r in cm}

    # return a dict of routes and associated distances
    route_mileage_dict = {k: RouteDists(k).calc_mileage(
        v, cost_matrix) for k, v in routes.items()}
    return route_mileage_dict


def write_route_mileage(plan_dir, cost_matrix_csv):
    """Write route mileage data to a csv"""
    route_mileage_dict = get_route_mileage(plan_dir, cost_matrix_csv)
    for k, v in route_mileage_dict.items():
        route_mileage_dict[k] = [k] + v
    route_mileage_df = pd.DataFrame.from_dict(route_mileage_dict, 'index')
    route_mileage_df.columns = ['Route Number', 'Miles', 'Live Miles']
    route_mileage_df.insert(0, 'No.', range(1, len(route_mileage_df) + 1))
    output_file = os.path.join(plan_dir, 'OUTPUT_analysis_bus_mileage.csv')
    route_mileage_df.to_csv(output_file, index=False)

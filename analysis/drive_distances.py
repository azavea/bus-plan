"""
Get drive distances of all routes within a plan
"""

import numpy as np
import pandas as pd


class RouteDists:

    def __init__(self, id):
        self.id = id
        self.mileage = 0.0

    def calc_mileage(self, legs, dists):
        for l in legs:
            self.mileage += dists[l] / 1609.34
        return self.mileage


def cost_matrix(cost_matrix_csv):
    # get cost matrix as dict
    #   { (origin_id, destination_id): distance }
    cm = np.genfromtxt(cost_matrix_csv, delimiter=',',
                       dtype=None, names=True)
    return {(r[0].decode(), r[1].decode()): r[3] for r in cm}


def route_distances(plan, dists):
    # get all routes as dict
    #   {route_id: [(origin_id, destination_id),(origin_id, destination_id)..]}
    routes = {}
    for n, g in plan[['route_id', 'origin_id']].drop_duplicates().groupby('route_id'):
        ln = g['origin_id'].tolist() + [g['origin_id'].tolist()[0]]
        if len(ln) > 3:
            routes[n] = [(ln[i], ln[i + 1]) for i in range(0, len(ln) - 1)]

    # return a dict of routes and associated distances
    return {k: RouteDists(k).calc_mileage(v, dists) for k, v in routes.items()}

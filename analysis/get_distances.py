"""
Get drive distances of all routes within a plan
"""

import numpy as np


class Route:
    def __init__(self, id):
        self.id = id
        self.mileage = 0.0

    def calc_mileage(self, legs, dists):
        for l in legs:
            self.mileage += dists[l] / 1609.34
        return self.mileage


def route_distances(plan, cost_matrix):

    # get cost matrix as dict
    #   { (origin_id, destination_id): distance }
    cm = np.genfromtxt(cost_matrix, delimiter=',', dtype=None, names=True)
    dists = {(r[0].decode(), r[1].decode()): r[3] for r in cm}

    # get all routes as dict
    #   {route_id: [(origin_id, destination_id),(origin_id, destination_id)..]}
    routes = {}
    with open(plan) as pl:
        for line in pl:
            l = [l.rstrip() for l in line.split(',') + [line.split(',')[1]]]
            s = [(l[i], l[i + 1]) for i in range(1, len(l) - 1)]
            if len(l) > 3:
                routes[l[0]] = [i for i in s if i[0] != i[1]]

    # return a dict of routes and associated distances
    return {k: Route(k).calc_mileage(v, dists) for k, v in routes.items()}

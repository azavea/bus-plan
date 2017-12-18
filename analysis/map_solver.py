"""
Map routes from route csv
"""

import folium
import pandas as pd
import re
import sys
from randomcolor import RandomColor
import geopandas as gpd
from shapely.geometry import Point
import time as tm
from datetime import *


def add_route(df, line_name, map):
    coords, stops = get_all_coords(df, line_name)
    color = RandomColor().generate()
    fg = folium.FeatureGroup(name = 'Route ' + str(line_name))
    fg.add_child(folium.PolyLine(coords, weight=3, opacity=0.75, color=color))
    for point in stops:
        if point == stops[0]:
            fc= [3, '#006600']
        elif point == stops[-1]:
            fc= [3, '#CC0000']
        else:
            fc= [3, '#FFFFFF']
        cm = folium.CircleMarker(point, color=color, fill=True,
            fill_color=fc[1], fill_opacity=1, radius=fc[0])
        fg.add_child(cm)
    map.add_child(fg)


def create_basemap(df):
    center = [df['y'].mean(), df['x'].mean()]
    m = folium.Map(center, zoom_start=10, tiles='CartoDB positron')
    bounds = [[df['y'].min(), df['x'].min()], [df['y'].max(), df['x'].max()]]
    m.fit_bounds(bounds)
    return m


def get_csv(routes):
    df = pd.read_csv(routes).sort_values(['route_id','route_sequence','stop_sequence'])
    outfile = re.sub('.csv', '.html', routes)
    return df, outfile


def get_all_coords(df, line_name):
    line = df[df.route_id == line_name]
    stops = line[line.stop_sequence==0].append(line[-1:])
    coords = [[row[1]['y'], row[1]['x']] for row in line.iterrows()]
    stops = [[row[1]['y'], row[1]['x']] for row in stops.iterrows()]
    return coords, stops


def map_plan(routes, write = False):
    df, outfile = get_csv(routes)
    m = create_basemap(df)
    plot_all(df, m)
    folium.LayerControl().add_to(m)
    if write:
        m.save(outfile)
        return m
    else:
        return m


def plot_all(df, map):
    lines = df.route_id.unique()
    for line in lines:
        add_route(df, line, map)


def main(routes):
    map_plan(routes, write = True)


if __name__ == '__main__':
    #
    # example run : $ python map_solver.py <input_plan.csv>
    #

    if len(sys.argv) != 2:
        print('[ ERROR ] you must supply 1 arguments: <input_plan.csv>')
        sys.exit(1)
    else:
        main(sys.argv[1])

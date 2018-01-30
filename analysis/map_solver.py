"""
Map routes from route csv
"""

import folium
from folium import plugins
import pandas as pd
import re
import sys
from randomcolor import RandomColor
import geopandas as gpd
from shapely.geometry import Point
import time as tm
from datetime import datetime
import pytz
import numpy as np


def animate(df, pal):
    """Generate an animation of the route plan"""

    def get_route_animations(lines):
        features = [{'type': 'Feature',
                     'geometry': {'type': 'LineString',
                                  'coordinates': line['coordinates']},
                     'properties': {
                         'times': line['dates'],
                         'style': {
                             'color': line['color'],
                             'weight': 3,
                             'opacity': 0.75}}
                     } for line in lines]
        return plugins.TimestampedGeoJson(
            {'type': 'FeatureCollection', 'features': features},
            period='PT1M', add_last_point=False, auto_play=False, loop=True)

    def p(df, route_no, pal):
        os = df[df.route_id == route_no]
        obj = {}
        obj['coordinates'] = [[row.x, row.y] for index, row in os.iterrows()]
        obj['dates'] = [row.timestamp for index, row in os.iterrows()]
        obj['route'] = [route_no]
        obj['color'] = pal[route_no]
        return obj

    animation_map = create_basemap(df)
    lines = [p(df, route, pal) for route in df['route_id'].unique()]
    get_route_animations(lines).add_to(animation_map)
    return animation_map


def static_map(df, pal):
    """Generate a static map of all routes in the plan"""

    def map_all(df, pal, m):
        def c(df, line_name):
            line = df[df.route_id_tofrom == line_name]
            stops = line[line.stop_sequence == 0].append(line[-1:])
            coords = [[row[1]['y'], row[1]['x']] for row in line.iterrows()]
            stops = [[row[1]['y'], row[1]['x']] for row in stops.iterrows()]
            return coords, stops

        # original
        def a(df, line_name, pal, m):

            coords, stops = c(df, line_name)

            if line_name in pal.keys():
                color = pal[line_name]
            else:
                color = '#000000'

            fg = folium.FeatureGroup(name='Route ' + str(line_name))
            fg.add_child(folium.PolyLine(coords, weight=3, opacity=0.75, color=color))
            for point in stops:
                # if point == stops[0]:
                #     fc = [4, '#006600']
                if point == stops[-1]:
                    fc = [4, '#CC0000']
                else:
                    fc = [2, '#FFFFFF']
                cm = folium.CircleMarker(
                    point, color=color, fill=True,
                    fill_color=fc[1], fill_opacity=1, radius=fc[0])
                fg.add_child(cm)

            m.add_child(fg)

        lines = df.route_id_tofrom.unique()
        fg = folium.FeatureGroup(name='Toggle to and from garages')
        for line in lines:
            if (line.startswith('to') or line.startswith('from')):
                coords, stops = c(df, line)
                import re
                color = pal[line.split('garage ')[1]]
                fg.add_child(folium.PolyLine(coords, weight=3, opacity=0.75,
                                             color=color))
                for point in stops:
                    if point == stops[0]:
                        fc = [4, '#006600']
                    # if point == stops[-1]:
                    #     fc = [4, '#CC0000']
                    else:
                        fc = [2, '#FFFFFF']
                    cm = folium.CircleMarker(
                        point, color=color, fill=True,
                        fill_color=fc[1], fill_opacity=1, radius=fc[0])
                    fg.add_child(cm)
        m.add_child(fg)

        for line in lines:
            if not (line.startswith('to') or line.startswith('from')):
                a(df, line, pal, m)

    static_map = create_basemap(df)
    map_all(df, pal, static_map)
    folium.LayerControl().add_to(static_map)
    return static_map


# Helper functions:

def create_basemap(df):
    """Generate a basemap"""
    center = [df['y'].mean(), df['x'].mean()]
    m = folium.Map(center, zoom_start=10, tiles='CartoDB positron')
    bounds = [[df['y'].min(), df['x'].min()], [df['y'].max(), df['x'].max()]]
    m.fit_bounds(bounds)
    return m


def get_csv(routes):
    """Read and formate router output"""

    def t(unix_time):
        time = datetime.fromtimestamp(unix_time)
        return time.strftime('%Y-%m-%dT%H:%M:%S')

    df = pd.read_csv(routes).sort_values(
        ['route_id', 'route_sequence', 'stop_sequence'])
    df['timestamp'] = df['time'].map(t)
    df['origin_type'] = [x.split('_')[0] for x in df['origin_id']]
    df['destination_type'] = [x.split('_')[0] for x in df['destination_id']]
    df = df[(df.origin_type != 'garage') | (df.destination_type != 'garage')]
    df['route_id'] = df['route_id'].astype(str)
    df['route_id_tofrom'] = np.where(
        (df['origin_type'] == 'garage'), 'from garage ' + df['route_id'], df['route_id'])
    df['route_id_tofrom'] = np.where(
        (df['destination_type'] == 'garage'), 'to garage ' + df['route_id_tofrom'], df['route_id_tofrom'])
    return df


def get_palette(df):
    """Create color pallette so that static map and animation routes match"""
    each_route = list(df['route_id'].unique())
    pal = {r: RandomColor().generate()[0] for r in each_route}
    return pal


# Main:
def main(routes):
    df = get_csv(routes)
    pal = get_palette(df)
    static_map(df, pal).save(re.sub('.csv', '_map.html', routes))
    animate(df, pal).save(re.sub('.csv', '_animation.html', routes))


if __name__ == '__main__':
    #
    # example run : $ python map_solver.py <input_plan.csv>
    #

    if len(sys.argv) != 2:
        print('[ ERROR ] you must supply 1 arguments: <input_plan.csv>')
        sys.exit(1)
    else:
        main(sys.argv[1])

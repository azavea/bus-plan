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


def initialize(routes):
    df = get_csv(routes)
    pal = get_palette(df)
    return df, pal


def visualize_plan(routes):
    df, pal = initialize(routes)
    animation = animate_plan(df, pal)
    static = map_plan(df, pal)
    return {'map': static, 'animation': animation}


def animate_plan(df, pal):
    animation_map = create_basemap(df)
    get_route_animations(
        [parse_route_object(df, route, pal)
         for route in df['route_id'].unique()]).add_to(animation_map)
    return animation_map


def map_plan(df, pal):
    static_map = create_basemap(df)
    map_all(df, pal, static_map)
    folium.LayerControl().add_to(static_map)
    return static_map


def add_route(df, line_name, pal, m):
    coords, stops = get_all_coords(df, line_name)
    color = pal[line_name]
    fg = folium.FeatureGroup(name='Route ' + str(line_name))
    fg.add_child(folium.PolyLine(coords, weight=3, opacity=0.75, color=color))
    for point in stops:
        if point == stops[0]:
            fc = [3, '#006600']
        elif point == stops[-1]:
            fc = [3, '#CC0000']
        else:
            fc = [3, '#FFFFFF']
        cm = folium.CircleMarker(
            point, color=color, fill=True,
            fill_color=fc[1], fill_opacity=1, radius=fc[0])
        fg.add_child(cm)
    m.add_child(fg)


def create_basemap(df):
    center = [df['y'].mean(), df['x'].mean()]
    m = folium.Map(center, zoom_start=10, tiles='CartoDB positron')
    bounds = [[df['y'].min(), df['x'].min()], [df['y'].max(), df['x'].max()]]
    m.fit_bounds(bounds)
    return m


def get_csv(routes):
    df = pd.read_csv(routes).sort_values(
        ['route_id', 'route_sequence', 'stop_sequence'])
    df['timestamp'] = df['time'].map(unix_to_timestamp)
    df['origin_type'] = [x.split('_')[0] for x in df['origin_id']]
    df['destination_type'] = [x.split('_')[0] for x in df['destination_id']]
    return df


def get_all_coords(df, line_name):
    line = df[df.route_id == line_name]
    stops = line[line.stop_sequence == 0].append(line[-1:])
    coords = [[row[1]['y'], row[1]['x']] for row in line.iterrows()]
    stops = [[row[1]['y'], row[1]['x']] for row in stops.iterrows()]
    return coords, stops


def get_palette(df):
    each_route = df['route_id'].unique()
    pal = {r: RandomColor().generate()[0] for r in each_route}
    return pal  # , each_route


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


def parse_route_object(df, route_no, pal):
    os = df[df.route_id == route_no]
    obj = {}
    obj['coordinates'] = [[row.x, row.y] for index, row in os.iterrows()]
    obj['dates'] = [row.timestamp for index, row in os.iterrows()]
    obj['route'] = [route_no]
    obj['color'] = pal[route_no]
    return obj


def map_all(df, pal, m):
    lines = df.route_id.unique()
    for line in lines:
        add_route(df, line, pal, m)


def unix_to_timestamp(unix_time):
    time = datetime.fromtimestamp(unix_time).replace(tzinfo=pytz.utc)
    time = time.astimezone(pytz.timezone('America/New_York'))
    return time.strftime('%Y-%m-%dT%H:%M:%S')


def main(routes):
    visualizations = visualize_plan(routes)
    visualizations['map'].save(re.sub('.csv', '_map.html', routes))
    visualizations['animation'].save(re.sub('.csv', '_animation.html', routes))


if __name__ == '__main__':
    #
    # example run : $ python map_solver.py <input_plan.csv>
    #

    if len(sys.argv) != 2:
        print('[ ERROR ] you must supply 1 arguments: <input_plan.csv>')
        sys.exit(1)
    else:
        main(sys.argv[1])

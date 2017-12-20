'''
PROJECT: School Bus Routing

DESCRIPTION: create dataset of students and eligible stops
'''

import sys
import csv
import pandas as pd
import geopandas as gpd
from shapely.geometry import Point


def find_eligible_stops(one_student, all_stops):
    eligible_stops = [one_student.id[0],
                      one_student.grade[0], one_student.stop_id[0]]
    for i in range(len(all_stops)):
        one_stop = one_row_geodf(all_stops, i)
        if one_stop.id[0] != eligible_stops[2]:
            dist = one_student.distance(one_stop)[0]
            if dist <= 5280:
                eligible_stops.append(one_stop.id[0])
    return eligible_stops


def find_eligible_stops_each_student(all_students, all_stops):
    eligibility = []
    for i in range(len(all_students)):
        one_student = one_row_geodf(all_students, i)
        eligible_stops = find_eligible_stops(one_student, all_stops)
        eligibility.append(eligible_stops)
    return eligibility


def get_nodes_pas(student_nodes_csv, stop_nodes_csv):
    # Student nodes
    student_nodes = pd.read_csv(student_nodes_csv)
    student_nodes_gdf = to_geodataframe(student_nodes, 'Y', 'X')
    student_nodes_pas = to_pasouth(student_nodes_gdf)
    # Stop nodes
    stop_nodes = get_stopdata(stop_nodes_csv)
    stop_nodes_pas = to_pasouth(stop_nodes)
    return student_nodes_pas, stop_nodes_pas


def get_student_data(student_table):
    dat = pd.read_csv(student_table)
    dat['full_address'] = dat['Student.Residential.Street.Address'] + ', ' + \
        dat['Student.Residential.City'] + ' ' + \
        dat['Student.Residential.State']
    addr = list(dat['full_address'])
    return dat, addr


def get_stopdata(stop_table):
    dat = pd.read_csv(stop_table)
    stops = dat[dat.type == 'stop']
    stops_gpd = to_geodataframe(stops, 'Y', 'X')
    return stops_gpd


def one_row_geodf(geo_df, row):
    return geo_df.loc[row:row, ].reset_index(drop=True)


def to_geodataframe(dat, lat, lon):
    geometry = [Point(xy) for xy in zip(dat[lat], dat[lon])]
    crs = {'init': 'epsg:4326'}
    geo_df = gpd.GeoDataFrame(dat, crs=crs, geometry=geometry)
    return geo_df


def to_pasouth(gdf):
    gdf['geometry'] = gdf['geometry'].to_crs(epsg=2272)
    return gdf


def write_to_csv(input_list, output_file):
    with open(output_file, 'w', newline='') as csv_file:
        w = csv.writer(csv_file, delimiter=',')
        for i in input_list:
            w.writerow(i)


def main(student_nodes_csv, stop_nodes_csv, output_eligibility_csv):
    students, stops = get_nodes_pas(student_nodes_csv, stop_nodes_csv)
    eligibility = find_eligible_stops_each_student(students, stops)
    write_to_csv(eligibility, output_eligibility_csv)
    return eligibility


if __name__ == '__main__':
    #
    # example run : $ python find_eligible_stops.py <input_student_nodes.csv>
    #   <input_stop_nodes.csv> <output_eligibility_matrix.csv
    #

    if len(sys.argv) != 4:
        print('[ ERROR ] you must supply 3 arguments: <input_student_nodes' +
              '.csv>, <input_stop_nodes.csv>, <output_eligibility_matrix.csv')
        sys.exit(1)
    else:
        main(sys.argv[1], sys.argv[2], sys.argv[3])

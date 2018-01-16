"""
Create CSVs of student walk distances to their assigned stops
"""

import os
import sys

from subprocess import call


def walk_distances(code_directory, input_dir, output_dir):
    '''
    Run main scala class

    Args:
        code_directory: directory of bus plan sbt project
        input_directory: directory containing cost matrix nodes, student
            nodes and graphs
        output_directory: output directory with subfolders for each run
    '''
    os.chdir(code_directory)
    args = ['otp/runMain',
            'com.azavea.busplan.routing.GenerateStudentWalkDistances',
            os.path.join(
                input_dir, 'chester-cost-matrix-nodes_no_duplicates.csv'),
            os.path.join(input_dir, 'chester-student-nodes.csv'),
            os.path.join(
                output_dir, 'OUTPUT_analysis_student_ridetimes.csv'),
            os.path.join(input_dir, 'graph_withStudents.obj'),
            os.path.join(
                output_dir, 'OUTPUT_analysis_student_walk_dists.csv')
            ]
    call('sbt "{}"'.format(' '.join(args)), shell=True)


def main(code_directory, input_directory, output_directory):
    for d in os.listdir(output_directory):
        sub_d = os.path.join(output_directory, d)
        if os.path.isdir(sub_d):
            walk_distances(code_directory, input_directory, sub_d)


if __name__ == '__main__':

    if len(sys.argv) != 4:
        print('[ ERROR ] you must supply 3 arguments: <code_directory>' +
              ' <input_directory> ' + '<output_directory>')
        sys.exit(1)
    else:
        main(sys.argv[1], sys.argv[2], sys.argv[3])

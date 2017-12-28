import os
import argparse
from subprocess import call
from multiprocessing import Pool, TimeoutError
import signal

class BusPlanConfig():
    def __init__(self,
                 code_dir,
                 cost_matrix_csv,
                 student_dataset_csv,
                 student_stop_eligibility_csv,
                 garage_count_csv,
                 solver_output_routes_csv,
                 solver_output_student_assignment_csv,
                 cost_matrix_nodes_csv,
                 graph_with_students_obj,
                 graph_without_students_obj,
                 router_output_csv,
                 analysis_output_student_ridetimes_csv):
        self.code_dir = code_dir
        self.cost_matrix_csv = cost_matrix_csv
        self.student_dataset_csv = student_dataset_csv
        self.student_stop_eligibility_csv = student_stop_eligibility_csv
        self.garage_count_csv  = garage_count_csv
        self.solver_output_routes_csv = solver_output_routes_csv
        self.solver_output_student_assignment_csv = solver_output_student_assignment_csv
        self.cost_matrix_nodes_csv = cost_matrix_nodes_csv
        self.graph_with_students_obj = graph_with_students_obj
        self.graph_without_students_obj = graph_without_students_obj
        self.router_output_csv = router_output_csv
        self.analysis_output_student_ridetimes_csv = analysis_output_student_ridetimes_csv


scenarios = { "student-walk-0.25m" : "25",
              "student-walk-0.40m" : "40",
              "student-walk-0.50m" : "50",
              "student-walk-0.50m-or-1.0m" : "100" }

def get_student_stop_eligibility_csv(scenario):
    return "student-stop-eligibility-{}.csv".format(scenarios[scenario])

def call_sbt(args):
    call('sbt "{}"'.format(' '.join(args)), shell=True)

def run_config(config):
    os.chdir(config.code_dir)

    # Run the solver
    call_sbt(["planner/runMain",
              "com.azavea.BusPlanner",
              config.cost_matrix_csv,
              config.student_dataset_csv,
              config.student_stop_eligibility_csv,
              config.garage_count_csv,
              config.solver_output_routes_csv,
              config.solver_output_student_assignment_csv])

    # Run the router
    call_sbt(["otp/runMain",
              "com.azavea.busplan.routing.GenerateRoutesFromSolver",
              config.cost_matrix_nodes_csv,
              config.solver_output_routes_csv,
              config.solver_output_student_assignment_csv,
              config.graph_with_students_obj,
              config.graph_without_students_obj,
              config.router_output_csv])

    # Run the analysis
    call(["python",
          "./analysis/student_ride_times.py",
          config.router_output_csv,
          config.solver_output_student_assignment_csv,
          config.analysis_output_student_ridetimes_csv])

def run(data_dir, output_dir, code_dir):
    def d(p):
        return os.path.join(data_dir, p)

    if not os.path.isdir(output_dir):
        os.makedirs(output_dir)

    # Gather input files
    cost_matrix_csv = d("cost_matrix.csv")
    student_dataset_csv = d("student_dataset.csv")
    garage_count_csv = d("garages.csv")
    cost_matrix_nodes_csv = d("cost_matrix_nodes.csv")
    graph_with_students_obj = d("graph_withStudents.obj")
    graph_without_students_obj = d("graph_withoutStudents.obj")

    confs = []
    for scenario in list(scenarios.keys())[:1]:
        student_stop_eligibility_csv = d(get_student_stop_eligibility_csv(scenario))

        # Do 4 runs of each scenario
        for x in range(0, 2):
            run_output_dir = os.path.join(output_dir, "{}_run{}".format(scenario, x))
            if not os.path.isdir(run_output_dir):
                os.makedirs(run_output_dir)

            def o(p):
                return os.path.join(run_output_dir, p)

            solver_output_routes_csv = o("OUTPUT_solver_routes.csv")
            solver_output_student_assignment_csv = o("OUTPUT_solver_student_assignment.csv")
            router_output_csv = o("OUTPUT_router.csv")
            analysis_output_student_ridetimes_csv = o("OUTPUT_analysis_student_ridetimes.csv")


            confs.append(BusPlanConfig(code_dir,
                                       cost_matrix_csv,
                                       student_dataset_csv,
                                       student_stop_eligibility_csv,
                                       garage_count_csv,
                                       solver_output_routes_csv,
                                       solver_output_student_assignment_csv,
                                       cost_matrix_nodes_csv,
                                       graph_with_students_obj,
                                       graph_without_students_obj,
                                       router_output_csv,
                                       analysis_output_student_ridetimes_csv))

    # Deal with Ctrl-C weirdness
    # https://stackoverflow.com/questions/11312525/catch-ctrlc-sigint-and-exit-multiprocesses-gracefully-in-python
    original_sigint_handler = signal.signal(signal.SIGINT, signal.SIG_IGN)
    pool = Pool(processes=len(confs))
    signal.signal(signal.SIGINT, original_sigint_handler)

    try:
        res = pool.map_async(run_config, confs, chunksize=1)
        res.wait(None)
    except KeyboardInterrupt:
        print("Caught KeyboardInterrupt, terminating workers")
        pool.terminate()
    else:
        print("Normal termination")
        pool.close()
    pool.join()

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Run Bus Plan.')
    parser.add_argument('data_dir',
                        metavar='DATA_DIR',
                        help='Base data directory that holds all properly named input files.')
    parser.add_argument('output_dir',
                        metavar='OUTPUT_DIR',
                        help='Directory for storing output files.')
    parser.add_argument('code_dir',
                        metavar='CODE_DIR',
                        help='Directory that holds the bus_plan codebase.')
    args = parser.parse_args()
    run(args.data_dir, args.output_dir, args.code_dir)

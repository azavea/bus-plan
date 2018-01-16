package com.azavea.busplan.routing

object Constants {

  // Distance threshold values in meters
  val QUARTER_MILE = 402 // 0.25 mi
  val FOUR_TENTHS_MILE = 644 // 0.40 mi
  val HALF_MILE = 804 // 0.50 mi
  val MILE = 1609 // 1.0 mi
  val ONE_AND_A_HALF_MILES = 2414 // 1.5 mi
  // Number of feet in 0.25 mi -> meters
  val EIGHTY_TWO_PERCENT_MILE = 1320 // 0.82 mi

  // A generic timestamp (midweek, school bus rush hour) 
  // to use when generating costs for cost matrices
  // or student walk distances
  // 7:30 AM, Wednesday, December 13, 2017
  val DEFAULT_COST_TIME = 1513168200
}
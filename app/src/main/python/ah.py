# coding: utf-8
from scipy.optimize import fsolve
# from pyproj.transformer import *
import exifread
import os
import numpy as np

from scipy import optimize as op



def sus_chain_line_Cross( latitude_1, longitude_1, height_1,latitude_2, longitude_2, height_2):
    coordinate_1 = Gps_to_Crs(latitude_1, longitude_1)
    coordinate_2 = Gps_to_Crs(latitude_2, longitude_2)
    old_1_x, old_1_y = coordinate_1.gps_to_crs()
    old_2_x, old_2_y = coordinate_2.gps_to_crs()
    point_base = (old_1_x[0],old_1_y[0])
    coordinate_1_x, coordinate_1_y = New_Coordinates(point_base, old_1_x, old_1_y)
    coordinate_2_x, coordinate_2_y = New_Coordinates(point_base, old_2_x, old_2_y)
    # print("dddccc",coordinate_2_x,coordinate_2_y)
    slope_1, intercept_1 = op.curve_fit(Reverse__Straight_Line, coordinate_1_x, coordinate_1_y)[0]
    slope_2, intercept_2 = op.curve_fit(Reverse__Straight_Line, coordinate_2_x, coordinate_2_y)[0]
    # # print("s1:",slope_1,"in1",intercept_1)
    # print("s2:", slope_2, "in2", intercept_2)
    cross_x=(intercept_2-intercept_1)/(slope_1-slope_2)
    # print(cross_x)
    # cross_y=slope_1*cross_x+intercept_1
    side_1_x = Side_X(coordinate_1_x, slope_1)
    side_2_x = Side_X(coordinate_2_x, slope_2)
    stall_space_1 = abs(side_1_x[0] - side_1_x[-1])
    stall_space_2 = abs(side_2_x[0] - side_2_x[-1])
    # print("side_1",side_1_x,"side_2",side_2_x)
    # side_x_mid_1 = (side_1_x[0]+side_1_x[-1])/2
    # side_x_mid_2 = (side_2_x[0]+side_2_x[-1])/2
    side_cross_x_1 = Side_X(cross_x, slope_1)
    side_cross_x_2 = Side_X(cross_x, slope_2)
    sus_chain_1 = Suspended_Chain_Line((side_1_x[1], height_1[1]), (side_1_x[2], height_1[2]),
                                       (side_1_x[3], height_1[3]))
    sus_chain_2 = Suspended_Chain_Line((side_2_x[1], height_2[1]), (side_2_x[2], height_2[2]),
                                       (side_2_x[3], height_2[3]))
    sus_chain_line_1 = fsolve(sus_chain_1.sus_chain_line, [1, -1, -1])
    sus_chain_line_2 = fsolve(sus_chain_2.sus_chain_line, [1, -1, -1])
    # print("111:",sus_chain_line_1,"",sus_chain_line_1[0]+sus_chain_line_1[2])
    # print("222:",sus_chain_line_2,"",sus_chain_line_2[0]+sus_chain_line_2[2])
    sag_h1 = get_sag_gap(side_cross_x_1, sus_chain_line_1)
    # print(sag_h1)
    sag_h2 = get_sag_gap(side_cross_x_2, sus_chain_line_2)
    # print(sag_h2)
    sag_gap = abs(sag_h1-sag_h2)
    # return latitude_1, longitude_1, height_1, latitude_2, longitude_2, height_2,stall_space_1,stall_space_2,sus_chain_line_1,sus_chain_line_2,slope_1,slope_2
    return sag_gap;

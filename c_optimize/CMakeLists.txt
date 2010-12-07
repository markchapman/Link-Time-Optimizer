cmake_minimum_required(VERSION 2.8)
PROJECT( cs706-proj )
SET(CMAKE_MODULE_PATH "${CMAKE_SOURCE_DIR}/cmake")

find_package( Boost COMPONENTS foreach)
find_package( LPSolve )

SET(CMAKE_CXX_FLAGS "-std=gnu++0x -O3 -march=native")
SET(CMAKE_CXX_FLAGS_DEBUG "-std=gnu++0x -g -O0 -ggdb3 -Wall -pedantic")

link_directories( ${Boost_LIBRARY_DIRS} )
include_directories(${LPSOLVE_INCLUDE_DIR} ${Boost_INCLUDE_DIRS})
ADD_EXECUTABLE( cvs cvs.cpp cvs.h common.cpp common.h)
ADD_EXECUTABLE( cm cm.cpp cm.h common.cpp common.h)
target_link_libraries(cvs ${LPSOLVE_LIBRARIES} ${Boost_LIBRARIES})
target_link_libraries(cm ${LPSOLVE_LIBRARIES} ${Boost_LIBRARIES})


cmake_minimum_required(VERSION 3.10)
project(linear-cas)

include(/home/peanut-brother/school/MATH270/CAS/vcpkg/scripts/buildsystems/vcpkg.cmake)

find_package(Boost REQUIRED COMPONENTS rational)
find_package(lexy CONFIG REQUIRED)

add_executable(linear-cas)
target_sources(linear-cas PRIVATE main.cpp)
target_link_libraries(linear-cas PRIVATE Boost::boost Boost::rational)
target_link_libraries(linear-cas PRIVATE foonathan::lexy foonathan::lexy::dev foonathan::lexy::ext foonathan::_lexy_base)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED True)
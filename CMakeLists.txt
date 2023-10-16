cmake_minimum_required(VERSION 3.10)
project(linear-cas)

include(FetchContent)
FetchContent_Declare(lexy URL https://lexy.foonathan.net/download/lexy-src.zip)
FetchContent_MakeAvailable(lexy)

add_executable(linear-cas)
target_sources(linear-cas PRIVATE main.cpp)
target_link_libraries(linear-cas PRIVATE foonathan::lexy)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED True)
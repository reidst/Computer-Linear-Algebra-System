cmake_minimum_required(VERSION 3.10)
project(linear-cas)

set(BOOST_INCLUDE_LIBRARIES thread rational)
set(BOOST_ENABLE_CMAKE ON)

include(FetchContent)
FetchContent_Declare(
  Boost
  GIT_REPOSITORY https://github.com/boostorg/boost.git
  GIT_SHALLOW TRUE
  GIT_TAG boost-1.74.0
)
FetchContent_MakeAvailable(Boost)

FetchContent_Declare(lexy URL https://lexy.foonathan.net/download/lexy-src.zip)
FetchContent_MakeAvailable(lexy)

add_executable(linear-cas)
target_sources(linear-cas PRIVATE main.cpp)
target_link_libraries(boost_test PRIVATE Boost::rational)
target_link_libraries(linear-cas PRIVATE foonathan::lexy)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED True)
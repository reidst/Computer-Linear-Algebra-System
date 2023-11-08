# Distributed under the OSI-approved BSD 3-Clause License.  See accompanying
# file Copyright.txt or https://cmake.org/licensing for details.

cmake_minimum_required(VERSION 3.5)

file(MAKE_DIRECTORY
  "/home/peanut-brother/school/MATH270/CAS/_deps/boost-src"
  "/home/peanut-brother/school/MATH270/CAS/_deps/boost-build"
  "/home/peanut-brother/school/MATH270/CAS/_deps/boost-subbuild/boost-populate-prefix"
  "/home/peanut-brother/school/MATH270/CAS/_deps/boost-subbuild/boost-populate-prefix/tmp"
  "/home/peanut-brother/school/MATH270/CAS/_deps/boost-subbuild/boost-populate-prefix/src/boost-populate-stamp"
  "/home/peanut-brother/school/MATH270/CAS/_deps/boost-subbuild/boost-populate-prefix/src"
  "/home/peanut-brother/school/MATH270/CAS/_deps/boost-subbuild/boost-populate-prefix/src/boost-populate-stamp"
)

set(configSubDirs )
foreach(subDir IN LISTS configSubDirs)
    file(MAKE_DIRECTORY "/home/peanut-brother/school/MATH270/CAS/_deps/boost-subbuild/boost-populate-prefix/src/boost-populate-stamp/${subDir}")
endforeach()
if(cfgdir)
  file(MAKE_DIRECTORY "/home/peanut-brother/school/MATH270/CAS/_deps/boost-subbuild/boost-populate-prefix/src/boost-populate-stamp${cfgdir}") # cfgdir has leading slash
endif()

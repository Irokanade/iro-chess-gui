cmake_minimum_required(VERSION 3.15)
project(iro_chess_movegen LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_library(iro_chess_movegen SHARED
    types.cpp
    tables.cpp
    position.cpp
)

target_include_directories(iro_chess_movegen PRIVATE ${JNI_INCLUDE_DIRS})

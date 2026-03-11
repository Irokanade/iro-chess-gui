cmake_minimum_required(VERSION 3.15)
project(iro_chess_movegen LANGUAGES CXX)

set(CMAKE_EXPORT_COMPILE_COMMANDS ON)
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(JNI REQUIRED)

add_library(iro_chess_movegen SHARED
    types.cpp
    tables.cpp
    position.cpp
    iro_chess_movegen_jni.cpp
)

target_include_directories(iro_chess_movegen PRIVATE ${JNI_INCLUDE_DIRS})

add_executable(chess_engine
    chess_engine.cpp
    types.cpp
    tables.cpp
    position.cpp
)

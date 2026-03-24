cmake_minimum_required(VERSION 3.24)
project(iro_chess_movegen LANGUAGES CXX)

find_package(JNI REQUIRED)

add_library(chess_core OBJECT
    types.cpp
    tables.cpp
    position.cpp
)
set_target_properties(chess_core PROPERTIES POSITION_INDEPENDENT_CODE ON)
target_compile_features(chess_core PUBLIC cxx_std_17)

add_library(iro_chess_movegen SHARED
    iro_chess_movegen_jni.cpp
)
target_link_libraries(iro_chess_movegen PRIVATE chess_core JNI::JNI)

add_executable(chess_engine chess_engine.cpp)
target_link_libraries(chess_engine PRIVATE chess_core)

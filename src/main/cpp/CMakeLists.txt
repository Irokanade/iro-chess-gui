cmake_minimum_required(VERSION 3.24)
project(iro_chess_movegen LANGUAGES CXX)

set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(JNI REQUIRED)

add_library(iro_chess_movegen SHARED
    types.cpp
    tables.cpp
    position.cpp
    iro_chess_movegen_jni.cpp
)

target_link_libraries(iro_chess_movegen PRIVATE JNI::JNI)

add_executable(chess_engine
    chess_engine.cpp
    types.cpp
    tables.cpp
    position.cpp
)

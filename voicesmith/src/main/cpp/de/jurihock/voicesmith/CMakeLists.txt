cmake_minimum_required(VERSION 3.1...3.19)

project(voicesmith)

file(GLOB_RECURSE HDR "${CMAKE_CURRENT_LIST_DIR}/*.h")
file(GLOB_RECURSE SRC "${CMAKE_CURRENT_LIST_DIR}/*.cpp")

add_library(${PROJECT_NAME} SHARED)
target_sources(${PROJECT_NAME} PRIVATE "${HDR}" "${SRC}")
target_include_directories(${PROJECT_NAME} PRIVATE "${CMAKE_CURRENT_LIST_DIR}/..")

target_compile_features(${PROJECT_NAME} PRIVATE cxx_std_20)
target_compile_options(${PROJECT_NAME} PRIVATE $<$<CONFIG:RELEASE>:-Ofast>)
target_compile_options(${PROJECT_NAME} PRIVATE -Wall -Werror)

include("${CMAKE_CURRENT_LIST_DIR}/libs/cpm.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/libs/easyloggingpp.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/libs/fmt.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/libs/oboe.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/libs/readerwriterqueue.cmake")

target_link_libraries(${PROJECT_NAME} PRIVATE android log)
target_link_libraries(${PROJECT_NAME} PRIVATE easyloggingpp fmt oboe readerwriterqueue)

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

include("${CMAKE_CURRENT_LIST_DIR}/cmake/cpm.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/cmake/eventpp.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/cmake/fmt.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/cmake/oboe.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/cmake/pffft.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/cmake/pocketfft.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/cmake/readerwriterqueue.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/cmake/stftpitchshift.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/cmake/vad.cmake")

target_link_libraries(${PROJECT_NAME} PRIVATE
  android
  eventpp
  fmt
  log
  oboe
  pffft
  pocketfft
  readerwriterqueue
  stftpitchshift
  vad)

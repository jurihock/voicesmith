# https://github.com/cpm-cmake/CPM.cmake

set(CPMSRC "https://github.com/cpm-cmake/CPM.cmake/releases/download/v0.39.0/CPM.cmake")
set(CPMDST "${CMAKE_BINARY_DIR}/CPM.cmake")

if(NOT EXISTS "${CPMDST}")
  file(DOWNLOAD "${CPMSRC}" "${CPMDST}")
endif()

include("${CPMDST}")

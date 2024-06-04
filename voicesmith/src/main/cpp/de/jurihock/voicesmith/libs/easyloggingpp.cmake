# https://github.com/amrayn/easyloggingpp

CPMAddPackage(
  NAME easyloggingpp
  VERSION 9.97.1
  GITHUB_REPOSITORY amrayn/easyloggingpp
  DOWNLOAD_ONLY YES)

if(easyloggingpp_ADDED)

  add_library(easyloggingpp)

  target_sources(easyloggingpp
    PUBLIC "${easyloggingpp_SOURCE_DIR}/src/easylogging++.h"
    "${easyloggingpp_SOURCE_DIR}/src/easylogging++.cc")

  target_include_directories(easyloggingpp
    PUBLIC "${easyloggingpp_SOURCE_DIR}/src")

  target_compile_definitions(easyloggingpp
    PUBLIC -DELPP_NO_DEFAULT_LOG_FILE)

  target_compile_options(easyloggingpp
    PUBLIC -Wno-range-loop-construct)

endif()

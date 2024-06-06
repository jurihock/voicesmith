# https://github.com/amrayn/easyloggingpp

CPMAddPackage(
  NAME easyloggingpp
  VERSION 9.97.1
  GITHUB_REPOSITORY amrayn/easyloggingpp
  DOWNLOAD_ONLY YES)

if(easyloggingpp_ADDED)

  set(HDR "${easyloggingpp_SOURCE_DIR}/src/easylogging++.h")
  set(SRC "${easyloggingpp_SOURCE_DIR}/src/easylogging++.cc")

  add_library(easyloggingpp)

  target_sources(easyloggingpp
    PUBLIC "${HDR}" "${SRC}")

  target_include_directories(easyloggingpp
    PUBLIC "${easyloggingpp_SOURCE_DIR}/src")

  target_compile_definitions(easyloggingpp
    PUBLIC -DELPP_NO_DEFAULT_LOG_FILE
           -DELPP_THREAD_SAFE)

  target_compile_options(easyloggingpp
    PUBLIC -Wno-range-loop-construct)

endif()

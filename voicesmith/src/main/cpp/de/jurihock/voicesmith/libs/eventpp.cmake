# https://github.com/wqking/eventpp

CPMAddPackage(
  NAME eventpp
  VERSION 2024.03.13
  GIT_TAG c472fb22e71ead0e58ff7df89e12c66b0bdfb533
  GITHUB_REPOSITORY wqking/eventpp
  DOWNLOAD_ONLY YES)

if(eventpp_ADDED)

  add_library(eventpp INTERFACE)

  target_include_directories(eventpp
    INTERFACE "${eventpp_SOURCE_DIR}/include")

endif()

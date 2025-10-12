# https://github.com/wqking/eventpp

CPMAddPackage(
  NAME eventpp
  VERSION 2024.12.08
  GIT_TAG 1224dd6c9bd4577d686ac42334fc545997f5ece1
  GITHUB_REPOSITORY wqking/eventpp
  DOWNLOAD_ONLY YES)

if(eventpp_ADDED)

  add_library(eventpp INTERFACE)

  target_include_directories(eventpp
    INTERFACE "${eventpp_SOURCE_DIR}/include")

endif()

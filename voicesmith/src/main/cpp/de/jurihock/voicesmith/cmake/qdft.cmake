# https://github.com/jurihock/qdft

CPMAddPackage(
  NAME qdft
  VERSION 2023.11.28
  GIT_TAG 682f15392d7cc227229c1af48cef01a583616a53
  GITHUB_REPOSITORY jurihock/qdft
  DOWNLOAD_ONLY YES)

if(qdft_ADDED)

  add_library(qdft INTERFACE)

  target_include_directories(qdft
    INTERFACE "${qdft_SOURCE_DIR}/cpp/src")

endif()

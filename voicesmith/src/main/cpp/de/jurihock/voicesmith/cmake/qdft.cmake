# https://github.com/jurihock/qdft

CPMAddPackage(
  NAME qdft
  VERSION 2025.10.17
  GIT_TAG 55ca40efc962b96f2e32fc375c306dc9e30dede8
  GITHUB_REPOSITORY jurihock/qdft
  DOWNLOAD_ONLY YES)

if(qdft_ADDED)

  add_library(qdft INTERFACE)

  target_include_directories(qdft
    INTERFACE "${qdft_SOURCE_DIR}/cpp/src")

endif()

# https://github.com/cameron314/readerwriterqueue

CPMAddPackage(
  NAME readerwriterqueue
  VERSION 1.0.7
  GITHUB_REPOSITORY cameron314/readerwriterqueue
  DOWNLOAD_ONLY YES)

if(readerwriterqueue_ADDED)

  add_library(readerwriterqueue INTERFACE)

  target_include_directories(readerwriterqueue
    INTERFACE "${readerwriterqueue_SOURCE_DIR}")

endif()

# https://bitbucket.org/jpommier/pffft

CPMAddPackage(
  NAME pffft
  VERSION 2025.02.24
  GIT_TAG d7a4c0206a29423478776d6b23a37bbb308f21d5
  GIT_REPOSITORY https://bitbucket.org/jpommier/pffft
  DOWNLOAD_ONLY YES)

if(pffft_ADDED)

  set(HDR "${pffft_SOURCE_DIR}/pffft.h")
  set(SRC "${pffft_SOURCE_DIR}/pffft.c")

  add_library(pffft)

  target_sources(pffft
    PUBLIC "${HDR}"
    PRIVATE "${SRC}")

  target_include_directories(pffft
    PUBLIC "${pffft_SOURCE_DIR}")

endif()

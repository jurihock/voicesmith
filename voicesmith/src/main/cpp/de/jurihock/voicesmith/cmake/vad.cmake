# https://webrtc.googlesource.com/src

CPMAddPackage(
  NAME vad
  VERSION 2025.10.12
  GIT_TAG 801244c1b1035b6af311b02a1e77ae73ccd89712
  GIT_REPOSITORY https://webrtc.googlesource.com/src
  DOWNLOAD_ONLY YES)

if(vad_ADDED)

  file(GLOB HDR "${vad_SOURCE_DIR}/common_audio/vad/*.h")
  file(GLOB SRC "${vad_SOURCE_DIR}/common_audio/vad/*.c")

  file(GLOB SRC_SIGNAL_PROCESSING
    "${vad_SOURCE_DIR}/common_audio/signal_processing/division_operations.c"
    "${vad_SOURCE_DIR}/common_audio/signal_processing/energy.c"
    "${vad_SOURCE_DIR}/common_audio/signal_processing/get_scaling_square.c"
    "${vad_SOURCE_DIR}/common_audio/signal_processing/resample*.c")

  add_library(vad)

  target_sources(vad
    PUBLIC "${HDR}"
    PRIVATE "${SRC}" "${SRC_SIGNAL_PROCESSING}")

  target_include_directories(vad
    PUBLIC "${vad_SOURCE_DIR}/common_audio/vad/include"
    PRIVATE "${vad_SOURCE_DIR}")

endif()

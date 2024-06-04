# https://gitlab.mpcdf.mpg.de/mtr/pocketfft

CPMAddPackage(
  NAME pocketfft
  VERSION 2024.05.05
  GIT_TAG b557a3519ccc1e36b74dc0901a073dd7872c0af2
  GIT_REPOSITORY https://gitlab.mpcdf.mpg.de/mtr/pocketfft
  DOWNLOAD_ONLY YES)

if(pocketfft_ADDED)

  add_library(pocketfft INTERFACE)

  target_include_directories(pocketfft
    INTERFACE "${pocketfft_SOURCE_DIR}")

  target_compile_definitions(pocketfft
    INTERFACE -DPOCKETFFT_NO_MULTITHREADING)

  target_compile_definitions(pocketfft
    INTERFACE -DPOCKETFFT_CACHE_SIZE=10)

endif()

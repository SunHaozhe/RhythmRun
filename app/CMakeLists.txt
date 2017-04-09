cmake_minimum_required(VERSION 3.4.1)
add_library( vocoder
             SHARED
             src/C/vocoder.c
             src/C/kiss_fft.c
             src/C/kiss_fftr.c
              )
include_directories(src/C/include/)

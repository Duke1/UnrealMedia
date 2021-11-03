# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)



SET(FF_LIB_PATH ${CMAKE_CURRENT_SOURCE_DIR}/../../ffmpeg_build)
SET(FF_LIB_ARMEABI_PATH ${FF_LIB_PATH}/arm64-v8a)
message(lib目录：${FF_LIB_PATH})

#set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/libs)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             NativeMedia

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             # Associated headers in the same location as their source
             # file are automatically included.
             src/main/cpp/Util.cpp
             src/main/cpp/SLAudioPlayer.cpp
             src/main/cpp/NativeAudioPlayer.cpp
             src/main/cpp/NativeMedia.cpp)

add_library(avcodec SHARED IMPORTED )
set_target_properties(avcodec  PROPERTIES IMPORTED_LOCATION ${FF_LIB_ARMEABI_PATH}/libavcodec.so)

#ffmpeg失效的库，用swresample替代
#add_library(avresample SHARED IMPORTED)
#set_target_properties(avresample  PROPERTIES IMPORTED_LOCATION ${FF_LIB_ARMEABI_PATH}/libavresample.so)

add_library(avutil SHARED IMPORTED)
set_target_properties(avutil  PROPERTIES IMPORTED_LOCATION ${FF_LIB_ARMEABI_PATH}/libavutil.so)

add_library(swresample SHARED IMPORTED)
set_target_properties(swresample  PROPERTIES IMPORTED_LOCATION ${FF_LIB_ARMEABI_PATH}/libswresample.so)

add_library(avformat SHARED IMPORTED)
set_target_properties(avformat  PROPERTIES IMPORTED_LOCATION ${FF_LIB_ARMEABI_PATH}/libavformat.so)

add_library(avfilter SHARED IMPORTED)
set_target_properties(avfilter  PROPERTIES IMPORTED_LOCATION ${FF_LIB_ARMEABI_PATH}/libavfilter.so)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because system libraries are included in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.


find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log

              OpenSLES
              )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.


target_link_libraries( # Specifies the target library.
                       NativeMedia

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib}

                        #${FF_LIB_PATH}/libavcodec.a
                        #${FF_LIB_PATH}/libavresample.a
                        #${FF_LIB_PATH}/libavutil.a
                        #${FF_LIB_PATH}/libswresample.a

                        avcodec
                        avutil
                        swresample
                        avformat
                        avfilter

                        OpenSLES
                       )

include_directories(
                   ${FF_LIB_PATH}
                   ${FF_LIB_PATH}/include
                   )

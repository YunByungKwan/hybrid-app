# 요구 CMake 최소 버전
cmake_minimum_required(VERSION 3.6.0)

# 빌드 대상 라이브러리 추가
# 빌드 최종 결과물로 생성할 라이브러리를 추가
# ADD_LIBRARY(<라이브러리 이름> [STATIC|SHARED|MODULE] <소스 파일> <소스파일>...)
add_library(native_app_glue STATIC
${ANDROID_NDK}/sources/android/native_app_glue/android_native_app_glue.c)
target_include_directories(native_app_glue PUBLIC
${ANDROID_NDK}/sources/android/native_app_glue)

find_library(log-lib
log)

#변수 정의 - SET(<변수명> <값>)
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -u ANativeActivity_onCreate")
add_library(rootingcheck SHARED
        src/main/cpp/rootingcheck.cpp)

target_link_libraries(rootingcheck
android
native_app_glue
${log-lib})
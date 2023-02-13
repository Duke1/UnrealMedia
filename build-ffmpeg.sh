#!/usr/bin/env bash
#git clone https://git.ffmpeg.org/ffmpeg.git ffmpeg && \

android_api_level=33
arch_name=aarch64

CUR_DIR=$(cd $(dirname ${BASH_SOURCE[0]}); pwd )
OUT_DIR=${CUR_DIR}/ffmpeg_build


#生成工具链。 NDK r19 已弃用 https://developer.android.google.cn/ndk/guides/other_build_systems
#if [ -d ${TOOLCHAIN_PATH} ];
#then
#    echo "Has toolchain !!"
#    #rm -r ${TOOLCHAIN_PATH}
#else
#    ${NDK_HOME}/build/tools/make_standalone_toolchain.py --arch arm64 --api 30 --install-dir ${TOOLCHAIN_PATH} --force -v
#fi
# 将独立工具链中的工具包添加到系统环境变量中
#export PATH=${TOOLCHAIN_PATH}/bin:$PATH

if [ -d ${OUT_DIR} ]; then
    rm -r ${OUT_DIR}
fi


TOOLCHAIN_PREFIX="${NDK_HOME}/toolchains/llvm/prebuilt/darwin-x86_64/bin"

export CROSS_PREFIX=${TOOLCHAIN_PREFIX}/${arch_name}-linux-android${android_api_level}
export AR=${TOOLCHAIN_PREFIX}/llvm-ar
export NM=${TOOLCHAIN_PREFIX}/llvm-nm
export STRIP=${TOOLCHAIN_PREFIX}/llvm-strip
export RANLIB=${TOOLCHAIN_PREFIX}/llvm-ranlib
export CC=${CROSS_PREFIX}-clang
export CXX=${CROSS_PREFIX}-clang++


cd ffmpeg &&\


./configure \
    --prefix=${OUT_DIR} \
    --libdir=${OUT_DIR}/arm64-v8a \
    --arch=${arch_name} \
    --cpu=armv8-a \
    --nm=${NM} \
    --ar=${AR} \
    --cc=${CC} \
    --cxx=${CXX} \
    --ranlib=${RANLIB} \
    --strip=${STRIP} \
    --cross-prefix=${CROSS_PREFIX}- \
    --target-os=android \
    --extra-ldexeflags=-pie \
    --disable-static \
    --enable-shared \
    --disable-doc \
    --disable-programs \
    --disable-avdevice \
    --disable-swscale \
    --disable-postproc \
    --disable-symver \
    --disable-encoders \
    --disable-decoders \
    --enable-decoder=flac,ape,mp3 \
    --disable-protocols\
    --enable-protocol=file \
    --disable-muxers \
    --disable-demuxers \
    --enable-demuxer=flac,ape,mp3 \
    --enable-cross-compile \
    --disable-indevs \
    --disable-outdevs \
    --enable-small \
    --enable-jni\
    --disable-vulkan \
    && \
make clean && \
make -j4 && \
make install

echo -e "\033[32m 编译完成 \033[0m" 


#./configure --list-decoders  查看支持的解码器
#./configure --list-protocols  查看支持的协议
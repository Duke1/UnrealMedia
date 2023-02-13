#include "NativeMedia.h"
#include <string>
#include "NativeAudioPlayer.h"

extern "C" {
#include "libavcodec/avcodec.h"
#include "libavutil/avutil.h"
#include "libavutil/opt.h"
#include "libswresample/swresample.h"
#include "libavformat/avformat.h"
}


const char *className = "com/qfleng/um/FFmpeg";

void readVersion(unsigned int ver, char *dstArr) {
    unsigned int ver_major, ver_minor, ver_micro;
    ver_major = (ver >> 16) & 0xff;
    ver_minor = (ver >> 8) & 0xff;
    ver_micro = (ver) & 0xff;

    sprintf(dstArr, "%d.%d.%d", ver_major, ver_minor, ver_micro);
}


jstring getFFmpegInfo(JNIEnv *env, jobject /* this */) {
    char tmp[20] = {0};

    std::string info;
    info.append("ffmpeg version：");
    info.append(av_version_info());
    info.append("\n\n");

    readVersion(avcodec_version(),tmp);
    info.append("avcodec version: ");
    info.append(tmp);
    info.append("\n");

    readVersion(avutil_version(),tmp);
    info.append("avutil version: ");
    info.append(tmp);
    info.append("\n");

    return env->NewStringUTF(info.c_str());
}

JavaVM *localJVM = NULL;
jobject localNewFFmpegObj = NULL;


static JNINativeMethod methods[] = {
        {"getFFmpegInfo", "()Ljava/lang/String;", (void *) getFFmpegInfo}
};

jint JNI_OnLoad(JavaVM *vm, void *reserved) {

    jclass clazz = NULL;
    JNIEnv *env = NULL;
    localJVM = vm;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        LOGE("JNI_OnLoad->GetEnv error!");
        return -1;
    }


    RNN(env, className, methods);
    RNN(env, Unreal::className, Unreal::methods);

    return JNI_VERSION_1_6;
}


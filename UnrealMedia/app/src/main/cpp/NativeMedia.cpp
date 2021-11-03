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

jstring getFFmpegInfo(JNIEnv *env, jobject /* this */) {
    char info[10000] = {0};
    sprintf(info,
            "ffmpeg version %s\r\n\r\navcodec:\n\t%s , version-%d \r\n avutil:\n\t%s,version-%d\r\n\r\nswresample:\n\t%s,version-%d",
            av_version_info(),
            avcodec_license(),
            avcodec_version(),
            avutil_license(),
            avutil_version(),
            swresample_license(),
            swresample_version()
    );
    return env->NewStringUTF(info);
}

JavaVM *localJVM = NULL;
jobject localNewFFmpegObj = NULL;


static JNINativeMethod methods[] = {
        {"getFFmpegInfo",                "()Ljava/lang/String;",  (void *) getFFmpegInfo}
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


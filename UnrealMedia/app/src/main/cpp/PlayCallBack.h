//
// Created by Duke
//

#ifndef UM_PLAYCALLBACK_H
#define UM_PLAYCALLBACK_H

#include <jni.h>
#include <zconf.h>
#include "Util.h"

class PlayCallBack {

public:
    JavaVM *localJVM;
    JNIEnv *env;
    int needsDetach = 0;

    jobject ffmpegObj;

    //播放进度(秒)
    int64_t pos = 0;


public:
    PlayCallBack(JavaVM *localJVM) {
        this->localJVM = localJVM;
    }

    ~PlayCallBack() {
    }


    void loadComplete(int32_t duration) {

        this->env = getJNIEnv(localJVM, &needsDetach);
        //    参数对象
        jclass clazz = env->FindClass("com/qfleng/um/FFmpeg$MediaInfo");
        jfieldID fieldDuration = env->GetFieldID(clazz, "duration", "I");
        jobject mediaLoadComplete = env->AllocObject(clazz);
        env->SetIntField(mediaLoadComplete, fieldDuration, duration);

        jclass ffmpegClass = env->GetObjectClass(ffmpegObj);

        jmethodID methodLoadSuccess =
                env->GetMethodID(ffmpegClass, "onLoadSuccess",
                                 "(Lcom/qfleng/um/FFmpeg$MediaInfo;)V");
        // 调用方法
        env->CallVoidMethod(ffmpegObj, methodLoadSuccess, mediaLoadComplete);

        if (needsDetach)
            localJVM->DetachCurrentThread();

    }

};

#endif //UM_PLAYCALLBACK_H

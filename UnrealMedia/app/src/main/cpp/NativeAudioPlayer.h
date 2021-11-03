//
// Created by Duke
//

#ifndef UM_AUDIOPLAYER_H
#define UM_AUDIOPLAYER_H

#include <jni.h>
#include "SLAudioPlayer.h"

namespace Unreal {
    class NativeAudioPlayer {
    public:
        //FFmpeg+OpenSLES的播放器实现
        SLAudioPlayer *player = NULL;

    public:

        static jlong s_create(JNIEnv *env, jobject clazz);

        static void s_play(JNIEnv *env, jobject clazz, jlong objHandle);

        static void s_stop(JNIEnv *env, jobject clazz, jlong objHandle);

        static void s_pause(JNIEnv *env, jobject clazz, jlong objHandle);

        static void s_seek(JNIEnv *env, jobject clazz, jlong objHandle, jlong pos);

        static jlong s_position(JNIEnv *env, jobject clazz, jlong objHandle);

        static jlong s_duration(JNIEnv *env, jobject clazz, jlong objHandle);

        static void s_setSource(JNIEnv *env, jobject clazz, jlong objHandle, jstring uri);

        static void s_destory(JNIEnv *env, jobject clazz, jlong objHandle);

        NativeAudioPlayer();

        ~NativeAudioPlayer();

        void setSource(char *uri);

        void play();

        void stop();

        void pause();

        void seek(int64_t pos);
    };


    //动态注册函数对应关系
    static const JNINativeMethod methods[] = {
            {"nCreate",    "()J",                    (void *) NativeAudioPlayer::s_create},
            {"nPlay",      "(J)V",                   (void *) NativeAudioPlayer::s_play},
            {"nSeek",      "(JJ)V",                  (void *) NativeAudioPlayer::s_seek},
            {"nPosition",  "(J)J",                   (void *) NativeAudioPlayer::s_position},
            {"nDuration",  "(J)J",                   (void *) NativeAudioPlayer::s_duration},
            {"nPause",     "(J)V",                   (void *) NativeAudioPlayer::s_pause},
            {"nStop",      "(J)V",                   (void *) NativeAudioPlayer::s_stop},
            {"nSetSource", "(JLjava/lang/String;)V", (void *) NativeAudioPlayer::s_setSource},
            {"nDestory",   "(J)V",                   (void *) NativeAudioPlayer::s_destory}
    };

    const static char *className = "com/qfleng/um/audio/FFAudioPlayer";
}

#endif //UM_AUDIOPLAYER_H

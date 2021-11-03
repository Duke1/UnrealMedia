//
// Created by Duke
//

#include "NativeAudioPlayer.h"

using namespace Unreal;

jlong NativeAudioPlayer::s_create(JNIEnv *env, jobject clazz) {
    NativeAudioPlayer *obj = new NativeAudioPlayer();
    return reinterpret_cast<jlong>(obj);
}

void NativeAudioPlayer::s_destory(JNIEnv *env, jobject clazz, jlong objHandle) {
    NativeAudioPlayer *obj = reinterpret_cast<NativeAudioPlayer *>(objHandle);
    if (obj)
        delete obj;
}

void NativeAudioPlayer::s_play(JNIEnv *env, jobject clazz, jlong objHandle) {
    NativeAudioPlayer *obj = reinterpret_cast<NativeAudioPlayer *>(objHandle);
    if (obj) {
        obj->play();
    }
}

void NativeAudioPlayer::s_setSource(JNIEnv *env, jobject clazz, jlong objHandle, jstring uri) {
    NativeAudioPlayer *obj = reinterpret_cast<NativeAudioPlayer *>(objHandle);
    if (obj) {
        obj->setSource(const_cast<char *>(env->GetStringUTFChars(uri, NULL)));
    }
}


void NativeAudioPlayer::s_pause(JNIEnv *env, jobject clazz, jlong objHandle) {
    NativeAudioPlayer *obj = reinterpret_cast<NativeAudioPlayer *>(objHandle);
    if (obj) {
        obj->pause();
    }
}


void NativeAudioPlayer::s_stop(JNIEnv *env, jobject clazz, jlong objHandle) {
    NativeAudioPlayer *obj = reinterpret_cast<NativeAudioPlayer *>(objHandle);
    if (obj) {
        obj->stop();
    }
}

void NativeAudioPlayer::s_seek(JNIEnv *env, jobject clazz, jlong objHandle, jlong pos) {
    NativeAudioPlayer *obj = reinterpret_cast<NativeAudioPlayer *>(objHandle);
    if (obj) {
        obj->seek(pos);
    }
}


jlong NativeAudioPlayer::s_position(JNIEnv *env, jobject clazz, jlong objHandle) {
    NativeAudioPlayer *obj = reinterpret_cast<NativeAudioPlayer *>(objHandle);
    if (obj && obj->player) {
        return obj->player->current_time;
    }
    return 0;
}

jlong NativeAudioPlayer::s_duration(JNIEnv *env, jobject clazz, jlong objHandle) {
    NativeAudioPlayer *obj = reinterpret_cast<NativeAudioPlayer *>(objHandle);
    if (obj && obj->player) {
        return obj->player->total_time;
    }
    return 0;
}


NativeAudioPlayer::NativeAudioPlayer() {

}

NativeAudioPlayer::~NativeAudioPlayer() {
    if (player)
        delete player;
}


void NativeAudioPlayer::setSource(char *uri) {
    if (player)
        delete player;

    char **pathArr = (char **) malloc(1 * sizeof(char *));
    int i = 0;
    for (i = 0; i < 1; i++) {
        pathArr[i] = uri;
    }
    player = new SLAudioPlayer(pathArr, 1);

    free(pathArr);
}

void NativeAudioPlayer::play() {
    player->play();
}

void NativeAudioPlayer::pause() {
    player->pause();
}

void NativeAudioPlayer::stop() {
    if (player)
        delete player;
}

void NativeAudioPlayer::seek(int64_t pos) {
    if(player)
        player->seek(pos);
}

#include <jni.h>
#include <string>
#include <jni.h>
#include <jni.h>


extern "C" jstring
Java_com_cloudin_monsterchicken_utils_NativeUtils_getDevUrl(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(
            "api.monsterchicken.cloudinworks.com/");
}

extern "C" jstring

Java_com_cloudin_monsterchicken_utils_NativeUtils_getProdUrl(JNIEnv *env, jobject thiz
) {
    return env->NewStringUTF(
            "api.gbazaar.in/api/v1.0/");
}

extern "C" jstring
Java_com_cloudin_monsterchicken_utils_NativeUtils_getTestUrl(JNIEnv *env, jobject thiz
) {
    return env->NewStringUTF(
            "api.test.monsterchicken.cloudinworks.com/");
}

extern "C" jstring
Java_com_cloudin_monsterchicken_utils_AES256_getPasswordKey(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(
            "s#Jv6ejUxs7MKcgyTkC3X9zZLjslGw2f");
}


extern "C" jstring
Java_com_cloudin_monsterchicken_utils_AES256_getIVKey(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(
            "K10Djpm7%9On%q7K");
}

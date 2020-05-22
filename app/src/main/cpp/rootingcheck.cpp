//
// Created by ybk on 2020-05-22.
//

#include <android_native_app_glue.h>
#include <jni.h>
#include <android/log.h>
#include <string>
#include <iostream>
#include <bitset>
#include <unistd.h>

#define LOG_TAG "C++"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, __VA_ARGS__)

extern "C" {

bool canRunSuCommand();
bool existSuspectedRootingFiles();
char* getSignature(JNIEnv*, jobject);
void startActivityAndFinish(JNIEnv*, jobject, const char*);


void android_main(struct android_app *state) {

    LOGE(LOG_TAG, "Start android_main().");

    /** Rooting check */
    if(canRunSuCommand() || existSuspectedRootingFiles()) {
        exit(0);
    }

    jobject context = state->activity->clazz;
    JNIEnv *env;
    state->activity->vm->AttachCurrentThread(&env, NULL);

    LOGE(LOG_TAG, "서명값 : %s", getSignature(env, context));

    startActivityAndFinish(env, context, "com.example.hybridapp.MainActivity");

    state->activity->vm->DetachCurrentThread();
    exit(0);
}

/** Check "su" command */
bool canRunSuCommand() {
    LOGE(LOG_TAG, " Execute function canRunSuCommand()");

    std::string command = "su";
    int result = system(command.c_str());
    LOGE(LOG_TAG, "Result: %s", std::to_string(result).c_str());

    if(result == 0) {
        LOGE(LOG_TAG, "Result 0 means rooting.");

        return true;
    }

    return false;
}

/** Check suspected rooting files */
bool existSuspectedRootingFiles() {
    const char* suspectedRootingFiles[] = {"/sbin/su",
                                           "/system/su",
                                           "/system/sbin/su",
                                           "/system/xbin/su",
                                           "/data/data/com.example.demoapp.su",
                                           "/system/app/Superuser.apk",
                                           "/system/bin/su",
                                           "/system/bin/.ext/.su",
                                           "/system/usr/we-need-root/su-backup",
                                           "/system/xbin/mu"};

    for(int index = 0; index < 10; index++) {
        if(access(suspectedRootingFiles[index], 0) == 0) {
            LOGE(LOG_TAG, "There is a suspected rooting file.");

            return true;
        }
    }

    return false;
}

/** Context 를 인자값을 받아서 Signature 의 값을 얻는다. */
char* getSignature(JNIEnv *env, jobject context) {
    jstring packageName;
    jobject packageManagerObj;
    jobject packageInfoObj;

    jclass contextClass =  env->GetObjectClass(context);

    jmethodID getPackageNameMid = env->GetMethodID(contextClass, "getPackageName", "()Ljava/lang/String;");
    jmethodID getPackageManager =  env->GetMethodID(contextClass, "getPackageManager", "()Landroid/content/pm/PackageManager;");

    jclass packageManagerClass = env->FindClass("android/content/pm/PackageManager");
    jmethodID getPackageInfo = env->GetMethodID(packageManagerClass, "getPackageInfo", "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");

    jclass packageInfoClass = env->FindClass("android/content/pm/PackageInfo");
    jfieldID signaturesFid = env->GetFieldID(packageInfoClass, "signatures", "[Landroid/content/pm/Signature;");

    jclass signatureClass = env->FindClass("android/content/pm/Signature");
    jmethodID signatureToByteArrayMid = env->GetMethodID(signatureClass, "toByteArray", "()[B");

    jclass messageDigestClass = env->FindClass("java/security/MessageDigest");
    jmethodID messageDigestUpdateMid = env->GetMethodID(messageDigestClass, "update", "([B)V");
    jmethodID getMessageDigestInstanceMid  = env->GetStaticMethodID(messageDigestClass, "getInstance", "(Ljava/lang/String;)Ljava/security/MessageDigest;");
    jmethodID digestMid = env->GetMethodID(messageDigestClass,"digest", "()[B");

    jclass base64Class = env->FindClass("android/util/Base64");
    jmethodID encodeToStringMid = env->GetStaticMethodID(base64Class,"encodeToString", "([BI)Ljava/lang/String;");

    packageName =  (jstring)env->CallObjectMethod(context, getPackageNameMid);

    packageManagerObj = env->CallObjectMethod(context, getPackageManager);
    // PackageManager.GET_SIGNATURES = 0x40
    packageInfoObj = env->CallObjectMethod(packageManagerObj,getPackageInfo, packageName, 0x40);
    jobjectArray signatures = (jobjectArray)env->GetObjectField(packageInfoObj, signaturesFid);
    //int signatureLength =  env->GetArrayLength(signatures);
    jobject signatureObj = env->GetObjectArrayElement(signatures, 0);
    jobject messageDigestObj  = env->CallStaticObjectMethod(messageDigestClass, getMessageDigestInstanceMid, env->NewStringUTF("SHA1"));
    env->CallVoidMethod(messageDigestObj, messageDigestUpdateMid, env->CallObjectMethod(signatureObj,signatureToByteArrayMid));

    // Base64.DEFAULT = 0 그렇기 때문에 맨 마지막 인자값은 0이다.
    jstring signatureHash = (jstring)env->CallStaticObjectMethod(base64Class, encodeToStringMid, env->CallObjectMethod(messageDigestObj, digestMid), 0);

    return (char*)env->GetStringUTFChars(signatureHash,0);
}

/** intent */
void startActivityAndFinish(JNIEnv *env, jobject context, const char *destination) {
    // Get instance of Intent
    jclass intentClass = env->FindClass("android/content/Intent");
    jmethodID newIntent = env->GetMethodID(intentClass, "<init>", "()V");
    jobject intentObject = env->NewObject(intentClass, newIntent);

    // Get instance of ComponentName
    jclass componentNameClass = env->FindClass("android/content/ComponentName");
    jmethodID newComponentName = env->GetMethodID(
            componentNameClass, "<init>", "(Landroid/content/Context;Ljava/lang/String;)V");
    jstring className =
            env->NewStringUTF(destination);
    jobject componentNameObject = env->NewObject(
            componentNameClass, newComponentName, context, className);

    // Set component in intent
    jmethodID setComponent = env->GetMethodID(
            intentClass, "setComponent",
            "(Landroid/content/ComponentName;)Landroid/content/Intent;");
    env->CallObjectMethod(intentObject, setComponent, componentNameObject);

    // Start activity using intent
    jclass activityClass = env->GetObjectClass(context);
    jmethodID startActivity = env->GetMethodID(activityClass, "startActivity",
                                               "(Landroid/content/Intent;)V");
    env->CallVoidMethod(context, startActivity, intentObject);

    jmethodID finish = env->GetMethodID(activityClass, "finish", "()V");
    env->CallVoidMethod(context, finish);
}
}
//
// Created by ybk on 2020-05-22.
//
#include <android_native_app_glue.h>
#include "com_example_hybridapp_MainActivity.h"
#include "com_example_hybridapp_MainActivity_FlexPopupInterface.h"
#include <jni.h>
#include <android/log.h>
#include <string>
#include <iostream>
#include <bitset>
#include <unistd.h>
#include <curl/curl.h>

#define TAG "HybridApp"
#define SU "su"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, __VA_ARGS__)

extern "C" {
    size_t responseWriter(char*, size_t, size_t, std::string*);
    bool isCorrectKeyHash(const char*);
    bool isHttpConnected(long);
    bool canRunSuCommand();
    bool existSuspectedRootingFiles();
    char* getSignature(JNIEnv*, jobject);
    void startActivityInNative(JNIEnv*, jobject, const char*);

    /** NativeActivity 진입점 */
    void android_main(struct android_app *state) {
        LOGD(TAG, "Start android_main()");

        jobject context = state->activity->clazz;
        JNIEnv *env;
        state->activity->vm->AttachCurrentThread(&env, NULL);
        jclass activityClass = env->GetObjectClass(context);

        const char* hash = getSignature(env, context);

        // F8mG1nqvFV4MmQQBuGd2v1NnKYc=
        //if(!isCorrectKeyHash("F8mG1nqvFV4MmQQBuGd2v1NnKYc=")) {
        //    LOGD(TAG, "해쉬키가 다릅니다");
        //    jclass activityClass = env->GetObjectClass(context);
        //    jmethodID finish = env->GetMethodID(activityClass, "finish", "()V");
        //    env->CallVoidMethod(context, finish);
        //}

        if(!canRunSuCommand() && !existSuspectedRootingFiles()) {
            startActivityInNative(env, context, "com.example.hybridapp.SplashActivity");
        }
        jmethodID finish = env->GetMethodID(activityClass, "finish", "()V");
        env->CallVoidMethod(context, finish);

        exit(0);

        state->activity->vm->DetachCurrentThread();
    }

    /** su 명령어가 되는지 판별 */
    bool canRunSuCommand() {
        LOGD(TAG, "Call canRunSuCommand()");

        std::string command = SU;
        int result = system(command.c_str());

        if(result == 0) {
            LOGE(TAG, "Result 0 means rooting.");
            return true;
        } else {
            LOGD(TAG, "Su command is not available.");
        }

        return false;
    }

    /** 루팅 의심 파일들을 체크 */
    bool existSuspectedRootingFiles() {
        LOGD(TAG, "Call existSuspectedRootingFiles()");

        const char* suspectedRootingFiles[] = {"/sbin/su", "/system/su", "/system/sbin/su",
                                               "/system/xbin/su",
                                               "/data/data/com.example.hybridapp.su",
                                               "/system/app/Superuser.apk",
                                               "/system/bin/su", "/system/bin/.ext/.su",
                                               "/system/usr/we-need-root/su-backup",
                                               "/system/xbin/mu"};

        for(int index = 0; index < 10; index++) {
            if(access(suspectedRootingFiles[index], 0) == 0) {
                LOGE(TAG, "There is a suspected rooting file.");

                return true;
            }
        }

        LOGD(TAG, "No suspected rooting files.");

        return false;
    }

    /** Response call back method */
    size_t responseWriter(char *data, size_t length, size_t bytes, std::string *writerData) {
    	if (writerData == NULL) {
    		return 0;
    	}

    	long size = length * bytes;
    	writerData->append(data,size);
    	return size;
    }

    bool isCorrectKeyHash(const char* hash) {
        LOGD(TAG, "Call isCorrectKeyHash()");

        CURL *curl;
        CURLcode res;
        std::string response;
        struct curl_slist *headerlist = nullptr;
        long httpCode = 0;
        headerlist = curl_slist_append(headerlist, "Content-Type: application/json");
        const char* targetUrl = "https://chathub.crabdance.com:453/android";
        std::string hashString(hash);

        // hashString = hashString.substr(0, hashString.length()); // 개행문자 제거
        // hashString = hashString.substr(0, hashString.length()-1); // 개행문자 제거

        if(hashString.at(hashString.length() - 1) == '\n') {
            hashString = hashString.substr(0, hashString.length()-1); // 개행문자 제거
        }

        std::string strResourceJSON = "{\"hash\" : \"" + hashString + "\"}";

        const char* postData = strResourceJSON.c_str();
        LOGD(TAG, "Data to send : %s", postData);

        // curl을 초기화
        curl_global_init(CURL_GLOBAL_ALL);

        // context를 생성
        curl = curl_easy_init();
        if(curl) {
            // context 설정
            curl_easy_setopt(curl, CURLOPT_URL, targetUrl);
            curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headerlist);
            curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, false);
            curl_easy_setopt(curl, CURLOPT_SSL_VERIFYHOST, false);
            curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, responseWriter);
            curl_easy_setopt(curl, CURLOPT_WRITEDATA, &response);
            curl_easy_setopt(curl, CURLOPT_POST, 1L);
            curl_easy_setopt(curl, CURLOPT_POSTFIELDS, postData);

            // 요청을 초기화하고 callback함수를 대기시킴
            res = curl_easy_perform(curl);
            curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &httpCode);

            if(res != CURLE_OK) {
                LOGE(TAG, "curl_easy_perform() failed: %s", curl_easy_strerror(res));
            }
            // context를 제거
            curl_easy_cleanup(curl);
        }
        // curl를 제거
        curl_global_cleanup();

        LOGD(TAG, "result: %s", response.c_str());
        LOGD(TAG, "Response Code: %ld", httpCode);

        return isHttpConnected(httpCode);
    }

    /** Http 통신이 되었는지 판별 */
    bool isHttpConnected(long code) {
        if(200 <= code && code < 400) {
            return true;
        }
        return false;
    }

    char* getSignature(JNIEnv *env, jobject context) {
        jstring packageName;
        jobject packageManagerObj;
        jobject packageInfoObj;

        jclass contextClass =  env->GetObjectClass(context);

        jmethodID getPackageNameMid = env->GetMethodID(contextClass, "getPackageName",
            "()Ljava/lang/String;");
        jmethodID getPackageManager =  env->GetMethodID(contextClass, "getPackageManager",
            "()Landroid/content/pm/PackageManager;");
        jclass packageManagerClass = env->FindClass("android/content/pm/PackageManager");
        jmethodID getPackageInfo = env->GetMethodID(packageManagerClass, "getPackageInfo",
            "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
        jclass packageInfoClass = env->FindClass("android/content/pm/PackageInfo");
        jfieldID signaturesFid = env->GetFieldID(packageInfoClass, "signatures",
            "[Landroid/content/pm/Signature;");
        jclass signatureClass = env->FindClass("android/content/pm/Signature");
        jmethodID signatureToByteArrayMid = env->GetMethodID(signatureClass, "toByteArray", "()[B");

        jclass messageDigestClass = env->FindClass("java/security/MessageDigest");
        jmethodID messageDigestUpdateMid = env->GetMethodID(messageDigestClass, "update", "([B)V");
        jmethodID getMessageDigestInstanceMid  = env->GetStaticMethodID(messageDigestClass,
            "getInstance", "(Ljava/lang/String;)Ljava/security/MessageDigest;");
        jmethodID digestMid = env->GetMethodID(messageDigestClass,"digest", "()[B");

        jclass base64Class = env->FindClass("android/util/Base64");
        jmethodID encodeToStringMid = env->GetStaticMethodID(base64Class,"encodeToString",
            "([BI)Ljava/lang/String;");

        packageName =  (jstring)env->CallObjectMethod(context, getPackageNameMid);

        packageManagerObj = env->CallObjectMethod(context, getPackageManager);
        // PackageManager.GET_SIGNATURES = 0x40
        packageInfoObj = env->CallObjectMethod(packageManagerObj,getPackageInfo, packageName, 0x40);
        jobjectArray signatures = (jobjectArray)env->GetObjectField(packageInfoObj, signaturesFid);
        //int signatureLength =  env->GetArrayLength(signatures);
        jobject signatureObj = env->GetObjectArrayElement(signatures, 0);
        jobject messageDigestObj  = env->CallStaticObjectMethod(messageDigestClass,
            getMessageDigestInstanceMid, env->NewStringUTF("SHA1"));
        env->CallVoidMethod(messageDigestObj, messageDigestUpdateMid,
            env->CallObjectMethod(signatureObj,signatureToByteArrayMid));

        // Base64.DEFAULT = 0 그렇기 때문에 맨 마지막 인자값은 0이다.
        jstring signatureHash = (jstring)env->CallStaticObjectMethod(base64Class,
            encodeToStringMid, env->CallObjectMethod(messageDigestObj, digestMid), 0);

        return (char*)env->GetStringUTFChars(signatureHash,0);
    }

    /** NativeActivity --> SplashActivity using intent */
    void startActivityInNative(JNIEnv *env, jobject context, const char *destination) {
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
        jmethodID startCommand = env->GetMethodID(activityClass, "startActivity", "(Landroid/content/Intent;)V");
        env->CallVoidMethod(context, startCommand, intentObject);
    }
}
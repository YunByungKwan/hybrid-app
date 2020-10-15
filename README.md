# hybrid-app

![hy](https://user-images.githubusercontent.com/51109517/95842869-096d4e00-0d82-11eb-928b-5510ddf513d4.PNG)

## Native Activity

#### Android JNI 연동 >
- CMake 사용<br>
  - https://velog.io/@ybg7955/AndroidKotlin-NativeActivity%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EB%B2%95
- ndk-build 사용<br>
  - https://re-build.tistory.com/7
  - https://dev.re.kr/67
  - https://thepassion.tistory.com/332

#### 함수 >
- size_t responseWriter(char*, size_t, size_t, std::string*)<br>
: cURL을 이용하여 http통신 후 response를 받는 callback함수 

- bool isCorrectKeyHash(const char*)<br>
: 서버에 해쉬값을 전송

- bool isHttpConnected(long)<br>
: 서버로 받은 code값으로 http통신이 잘 되었는지 판별

- bool canRunSuCommand()<br>
: Su 명령어가 가능한지 판별

- bool existSuspectedRootingFiles()<br>
: 루팅 의심 파일이 있는지 체크

- char* getSignature(JNIEnv*, jobject)<br>
: 해쉬값을 생성 후 반환

- void startActivityAndFinish(JNIEnv*, jobject, const char*)<br>
: 인텐트를 사용하여 해당 액티비티로 이동

#### 사용법 >
```c
...
if(canRunSuCommand() || existSuspectedRootingFiles()) {
    exit(0);
}
...
// Get hash
jobject context = state->activity->clazz;
JNIEnv *env;
state->activity->vm->AttachCurrentThread(&env, NULL);
const char* hash = getSignature(env, context);
...
if(!isCorrectKeyHash(hash)) {
    exit(0);
}
...
startActivityAndFinish(env, context, "com.example.hybridapp.MainActivity");
...
```

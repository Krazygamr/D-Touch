Echo Calling Make
call ndk-build G=DOOM -j3 BUILD_SERIAL=1
Echo running release tools
call ..\release_tools.exe libs\armeabi-v7a\libgzdoom.so

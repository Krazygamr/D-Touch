Echo Calling Make
call ndk-build G=JK3 -j3
Echo running CRC tools
call ..\release_tools.exe libs\armeabi\libjk3.so
call ..\release_tools.exe libs\armeabi\libjk3mp.so

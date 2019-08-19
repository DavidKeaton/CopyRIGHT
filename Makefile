PROJ_DIR	:= ${HOME}/src/school/CopyRIGHT
WINDOWS_DIR	:= ${HOME}/src/school/copyright-windows-the-segfaults
ANDROID_DIR	:= ${HOME}/src/school/copyright-android-the-segfaults
APK_DIR		:= ${PROJ_DIR}/android/app/build/outputs/apk
OUT_DIR		:= ${PROJ_DIR}/out

APK_NAME	:= CopyRIGHT.apk
APK_DEBUG	:= ${APK_DIR}/debug/app-debug.apk

LIB_DIR		:= ${PROJ_DIR}/lib

DROID_PAGE_DIR	:= ${PROJ_DIR}/android/Page
DROID_UI_DIR	:= ${PROJ_DIR}/android/GUI

PAGE_DIR	:= ${PROJ_DIR}/desktop/Page/src/Page
UI_DIR		:= ${PROJ_DIR}/desktop/UI/src/UI

all: $(Page) $(UI)

# create JAR file and place it in apropos folders
Page:
	jar -c -f $@.jar -C "${PAGE_DIR}/" .
	mv "$@.jar" "${LIB_DIR}/$@.jar"
	cp -f "${LIB_DIR}/$@.jar" "${DROID_PAGE_DIR}/$@.jar"

# create JAR file and place it in apropos folders
UI:
	jar -c -f $@.jar -C "${UI_DIR}/" .
	mv "$@.jar" "${LIB_DIR}/$@.jar"
	cp -f ${LIB_DIR}/$@.jar ${DROID_UI_DIR}/$@.jar

# port the files from CopyRIGHT -> copyright-windows-the-segfaults
port:
	# remove SEGFAULT files
	rm -rf ${ANDROID_DIR}/*
	rm -rf ${WINDOWS_DIR}/*
	# copy CopyRIGHT -> SEGFAULT
	cp -rLf desktop/* ${WINDOWS_DIR}/*
	cp -rLf lib/ ${WINDOWS_DIR}/libs/
	cp -fL .gitignore ${WINDOWS_DIR}/.gitignore
	cp -rLf android/* ${ANDROID_DIR}/*
	cp -rLf lib/ ${ANDROID_DIR}/libs/
	cp -fL .gitignore ${ANDROID_DIR}/.gitignore

# extract built APK from IntelliJ
apk: ${APK_DEBUG}
	mkdir -p ${OUT_DIR}/apk
	cp -f $^ ${OUT_DIR}/apk/${APK_NAME}

report: cloc.txt
	cloc --progress-rate=5 --out=cloc.txt .

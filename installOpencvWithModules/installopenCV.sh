#!/usr/bin/env bash
if [[ $(/usr/bin/id -u) -ne 0 ]]; then
    echo "Not run as root, exiting."
    exit
fi
mkdir installOpenCV
cd installOpenCV
apt update
apt install -y unzip git
git clone https://github.com/opencv/opencv_contrib.git
cd opencv_contrib
git checkout 3.3.1
cd ../
wget https://github.com/opencv/opencv/archive/3.3.1.zip
unzip 3.3.1.zip
cd opencv-3.3.1
apt install -y build-essential
apt install -y cmake libgtk2.0-dev pkg-config libavcodec-dev libavformat-dev libswscale-dev
apt install -y python-dev python-numpy libtbb2 libtbb-dev libjpeg-dev libpng-dev libtiff-dev libdc1394-22-dev
apt install -y libavcodec-dev libavformat-dev libswscale-dev libdc1394-22-dev
apt install -y libxine2-dev libv4l-dev
apt install -y qt5-default libgtk2.0-dev libtbb-dev
apt install -y libatlas-base-dev
apt install -y libfaac-dev libmp3lame-dev libtheora-dev
apt install -y libvorbis-dev libxvidcore-dev
apt install -y libopencore-amrnb-dev libopencore-amrwb-dev
apt install -y x264 v4l-utils
mkdir build
cd build
cmake -D CMAKE_BUILD_TYPE=RELEASE -D CMAKE_INSTALL_PREFIX=/usr/local -D INSTALL_C_EXAMPLES=ON D WITH_TBB=ON -D WITH_V4L=ON -D WITH_QT=ON -D WITH_OPENGL=ON -D OPENCV_EXTRA_MODULES_PATH=../../opencv_contrib/modules ..
make -j$(nproc)
make install
ldconfig
cd ../../..
rm -rf installOpenCV

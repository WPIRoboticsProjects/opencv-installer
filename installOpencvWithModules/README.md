# Install opencv with contrib modules for [ArUco](https://docs.opencv.org/3.4.0/db/da9/tutorial_aruco_board_detection.html) and [ChArUco](https://docs.opencv.org/3.1.0/df/d4a/tutorial_charuco_detection.html) boards
opencv_dnn module is placed in the secondary opencv_contrib repository, which isn't distributed in binary form, therefore you need to build it manually.

### For Windows and Mac users
I recommend you follow these [steps](https://docs.opencv.org/3.2.0/de/d25/tutorial_dnn_build.html). Liunx users can also follow but there is a faster way

### Linux users
if you go back to the folder, ["installOpencvWithModules"](https://github.com/N-kelkay/opencv-installer/tree/master/installOpencvWithModules), you will see a shell file called installopenCV.sh. all you need to do is 
- Download that file, by either cloning the repository and taking it out or some other method.
- Open terminal or another command-line interpreter, then get to the directory where you saved the file
- Then type: `sudo chmod -x _path_installOpenCV.sh`. for "_path_" replace it with the path file starting from your home folder Example: `sudo chmod -x /home/"compuername"/Desktop/installOpenCV.sh`
- The type `sudo sh ./installopenCV.sh`. This will actually install it. It might take while, and it might also ask for admin password so make sure to watch for that

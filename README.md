# AP.Project.WebRTC.Mobile

![Android CI](https://github.com/KushtrimPacaj/AP.Project.WebRTC.Mobile/workflows/Android%20CI/badge.svg?branch=master)

This is a project for the course "Advanced Programming" ( UBT - MS CSA ).  


The project uses WebRTC to achieve audio/video calling between two peers.  
Related project: https://github.com/KushtrimPacaj/AP.Project.WebRTC.Signaling  ( signaling implementation ).  

Demo of app:
https://www.youtube.com/playlist?list=PL8PtQZl3muhfckHkl6gNahE_cGlHW_l1O

To test this app:
1. Run signaling server in your laptop by following these steps:   
  ```bash
  git clone https://github.com/KushtrimPacaj/AP.Project.WebRTC.Signaling
  cd AP.Project.WebRTC.Signaling
  npm install
  node src/index.js 
  ```
2. Make sure phone and laptop are in same network.
3. Get IP address of laptop, and modify serverURL field in this file:   ```com/ap/project/webrtcmobile/utils/ServerInfoInteractor.kt```
3. Make sure your phone is connected and ADB enabled
4. Import this project in Android Studio and click RUN. Alternativly via CLI run:
```bash
./gradlew :app:installDebug   #macOS/Linux
./gradlew.bat  :app:installDebug     #windows
```
5. Repeat steps 3/4 for another phone.
6. Make a CALL!!!!



Students:  
* Kushtrim Pacaj
* Kujtim Hyseni
* Donika Sfishta


Copyright© 2020 Kushtrim Pacaj.  All rights reserved

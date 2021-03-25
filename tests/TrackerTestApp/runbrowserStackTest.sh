echo "M0bil35DK" | sudo -S chown -R `whoami` ~/.npm
echo "M0bil35DK" | sudo chown -R `whoami` /usr/local/lib/node_modules
export PATH=/usr/local/bin:$PATH
# Update homebrew recipes
curl -s http://api.open-notify.org/iss-now.json | jq .timestamp
brew install jq
# Get current working Directory
presentWorkingDirectory=$(pwd)
echo "presentWorkingDirectory==> $presentWorkingDirectory"
# Set Browser Stack userName & accessKey
userName="mobilesdkteam1"
accessKey="eAqGKNyysiKQmX1wDUQ4"
# Add devices list
devices="\"Google Pixel 3-9.0\""
echo " devcies==>$devices"
# Build the App apk
./gradlew clean assembleDebug
# Upload App APK for browser Stack and get appurl
appurl=$(curl -u "$userName:$accessKey" -X POST "https://api-cloud.browserstack.com/app-automate/upload" -F "file=@$presentWorkingDirectory/app/build/outputs/apk/debug/app-debug.apk" | jq .app_url)
echo "appurl==> $appurl"
# Build the Test Suite apk
./gradlew clean assembleDebugAndroidTest
# Upload Test Suite APK for browser Stack and get testurl
testurl=$(curl -u "$userName:$accessKey" -X POST "https://api-cloud.browserstack.com/app-automate/espresso/test-suite" -F "file=@$presentWorkingDirectory/app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk" | jq .test_url)
echo "testurl==> $testurl"
echo "<==Execute Espresso tests==>"
# Execute Espresso tests and get buildId
buildid=$(curl -X POST "https://api-cloud.browserstack.com/app-automate/espresso/build" -d "{\"networkLogs\" : \"true\",\"devices\": [$devices], \"app\": $appurl, \"deviceLogs\" : \"true\", \"testSuite\": $testurl}" -H "Content-Type: application/json" -u "$userName:$accessKey"| jq .build_id  | tr -d \")
echo "buildid==> $buildid"
# Wait for testcase result
testTrackerTestResult="running"
if [ $testTrackerTestResult == "running" ] ; then result=true; else result=false; fi
while $result; do sleep 1; testTrackerTestResult=$(curl -u "$userName:$accessKey" -X GET "https://api-cloud.browserstack.com/app-automate/espresso/builds/$buildid" | jq '.devices."Google Pixel 3-9.0".status' | tr -d \");
if [ $testTrackerTestResult == "running" ] ; then result=true; else result=false; fi
echo "Please wait.......\n";
sleep 60
done
echo "Test Result Impression, OMID Tracker & Click Tracker.......$testTrackerTestResult\n";

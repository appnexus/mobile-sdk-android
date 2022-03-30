EXIT_STATUS=0
DIR_PATH_SDK_REPORT="sdkTestReport"
DIR="sdk/build/reports/tests/testDebugUnitTest"
DIR_CLASSES="sdk/build/reports/tests/testDebugUnitTest/classes"
DIR_CLASSES_PACKAGES="sdk/build/reports/tests/testDebugUnitTest/packages"


checkIfDeleteDirectory() {
if [ -d "$DIR_PATH_SDK_REPORT" ]; then rm -Rf $DIR_PATH_SDK_REPORT;
else mkdir $DIR_PATH_SDK_REPORT
fi
}

copyIndexHtml(){
cp -r $DIR_CLASSES/*  sdkTestReport/classes/
cp -r $DIR_CLASSES_PACKAGES/* sdkTestReport/packages/
cat $DIR/index.html >> sdkTestReport/index.html
}

checkIfDeleteDirectory
./gradlew :instreamvideo:testDebugUnitTest  --info  || EXIT_STATUS=$?
sleep 6
wait $!
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.AdRequestTestSuite  --scan  || EXIT_STATUS=$?
cp -r  $DIR/* sdkTestReport || EXIT_STATUS=$?
sleep 6
wait $!
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.AdResponseInfoTestSuite  --scan   || EXIT_STATUS=$?
copyIndexHtml || EXIT_STATUS=$?
sleep 6
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.AdViewTestSuite  --scan  || EXIT_STATUS=$?
copyIndexHtml || EXIT_STATUS=$?
sleep 6
 ./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.DefaultSettingsTestSuite   --scan   || EXIT_STATUS=$?
copyIndexHtml || EXIT_STATUS=$?
sleep 6
 ./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.MediationTestSuite   --scan  || EXIT_STATUS=$?
copyIndexHtml || EXIT_STATUS=$?
sleep 6
 ./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.MRAIDTestSuite   --scan   || EXIT_STATUS=$?
copyIndexHtml || EXIT_STATUS=$?
sleep 6
 ./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.NativeAdSDKTestSuite   --scan  || EXIT_STATUS=$?
copyIndexHtml || EXIT_STATUS=$?
sleep 6
 ./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.OmidTestSuite   --scan  || EXIT_STATUS=$?
copyIndexHtml
sleep 6
 ./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.UtAdTestSuite --scan  || EXIT_STATUS=$?
copyIndexHtml || EXIT_STATUS=$?
sleep 6
wait $!
 ./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.NativeAdSDKFailedTest  --scan  || EXIT_STATUS=$?
copyIndexHtml || EXIT_STATUS=$?
sleep 6
wait $!
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.NativeRequestTest  --scan  || EXIT_STATUS=$?
copyIndexHtml || EXIT_STATUS=$?
sleep 6
wait $!
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.MARTestSuite  --scan  || EXIT_STATUS=$?
copyIndexHtml || EXIT_STATUS=$?
sleep 6
wait $!
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.Miscellaneous  --scan  || EXIT_STATUS=$?
copyIndexHtml || EXIT_STATUS=$?
exit $EXIT_STATUS











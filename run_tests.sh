EXIT_STATUS=0
DIR_PATH_SDK_REPORT="sdkTestReport"
DIR_PATH_SDK_REPORT_COMBINED="sdkTestReport/Combined"
DIR_PATH_SDK_REPORT_COMBINED_CLASSES="sdkTestReport/Combined/classes"
DIR_PATH_SDK_REPORT_COMBINED_PACKAGES="sdkTestReport/Combined/packages"
DIR="sdk/build/reports/tests/testDebugUnitTest"
DIR_CLASSES="sdk/build/reports/tests/testDebugUnitTest/classes"
DIR_CLASSES_PACKAGES="sdk/build/reports/tests/testDebugUnitTest/packages"


checkIfDeleteDirectory() {
if [ -d "$DIR_PATH_SDK_REPORT" ]; then rm -Rf $DIR_PATH_SDK_REPORT; fi
mkdir $DIR_PATH_SDK_REPORT
mkdir $DIR_PATH_SDK_REPORT_COMBINED
mkdir $DIR_PATH_SDK_REPORT_COMBINED_CLASSES
mkdir $DIR_PATH_SDK_REPORT_COMBINED_PACKAGES
}

copyIndexHtml(){
cp -r $DIR_CLASSES/*  sdkTestReport/Combined/classes/
cp -r $DIR_CLASSES_PACKAGES/* sdkTestReport/Combined/packages/
cat $DIR/index.html >> sdkTestReport/Combined/index.html

mkdir $DIR_PATH_SDK_REPORT/$1
cp -r $DIR/*  sdkTestReport/$1/
}

checkIfDeleteDirectory
./gradlew :instreamvideo:testDebugUnitTest  --info  || EXIT_STATUS=$?
sleep 6
wait $!
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.AdRequestTestSuite  --scan  || EXIT_STATUS=$?
copyIndexHtml "AdRequestTestSuite" || EXIT_STATUS=$?
sleep 6
wait $!
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.AdResponseInfoTestSuite  --scan   || EXIT_STATUS=$?
copyIndexHtml "AdResponseInfoTestSuite" || EXIT_STATUS=$?
sleep 6
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.AdViewTestSuite  --scan  || EXIT_STATUS=$?
copyIndexHtml "AdViewTestSuite" || EXIT_STATUS=$?
sleep 6
 ./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.DefaultSettingsTestSuite   --scan   || EXIT_STATUS=$?
copyIndexHtml "DefaultSettingsTestSuite" || EXIT_STATUS=$?
sleep 6
 ./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.MediationTestSuite   --scan  || EXIT_STATUS=$?
copyIndexHtml "MediationTestSuite" || EXIT_STATUS=$?
sleep 6
 ./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.MRAIDTestSuite   --scan   || EXIT_STATUS=$?
copyIndexHtml "MRAIDTestSuite" || EXIT_STATUS=$?
sleep 6
 ./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.NativeAdSDKTestSuite   --scan  || EXIT_STATUS=$?
copyIndexHtml "NativeAdSDKTestSuite" || EXIT_STATUS=$?
sleep 6
 ./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.OmidTestSuite   --scan  || EXIT_STATUS=$?
copyIndexHtml "OmidTestSuite" || EXIT_STATUS=$?
sleep 6
 ./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.UtAdTestSuite --scan  || EXIT_STATUS=$?
copyIndexHtml "UtAdTestSuite" || EXIT_STATUS=$?
sleep 6
wait $!
 ./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.NativeAdSDKFailedTest  --scan  || EXIT_STATUS=$?
copyIndexHtml "NativeAdSDKFailedTest" || EXIT_STATUS=$?
sleep 6
wait $!
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.NativeRequestTest  --scan  || EXIT_STATUS=$?
copyIndexHtml "NativeRequestTest" || EXIT_STATUS=$?
sleep 6
wait $!
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.MARTestSuite  --scan  || EXIT_STATUS=$?
copyIndexHtml "MARTestSuite" || EXIT_STATUS=$?
sleep 6
wait $!
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.Miscellaneous  --scan  || EXIT_STATUS=$?
copyIndexHtml "Miscellaneous" || EXIT_STATUS=$?
exit $EXIT_STATUS











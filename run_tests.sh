./gradlew :instreamvideo:testDebugUnitTest --info  &
wait
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.MARTestSuite --info   &
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.AdRequestTestSuite --info  &
 sleep 3 &
wait $!
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.AdResponseInfoTestSuite --info &
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.MediationTestSuite --info &
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.MRAIDTestSuite --info &
sleep 3 &
wait $!
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.DefaultSettingsTestSuite --info &
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.OmidTestSuite --info &
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.UtAdTestSuite --info &
sleep 3 &
wait $!
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.NativeAdSDKTestSuite --info &
wait
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.DefaultSettingsTestSuite --info &
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.suite.AdViewTestSuite --info &
wait
./gradlew :sdk:testDebugUnitTest --tests  com.appnexus.opensdk.NativeAdSDKFailedTest --info mergeAndroidReports
# Variables
OUTDIR=`pwd`/out
TMPOUTDIR=`pwd`/tmpout
AARPATH=build/outputs/aar
BASEDIR=`pwd`
MEDDIR=`pwd`/mediation/mediatedviews
MEDFORDIR=`pwd`/mediation/mediating
SDKDIR=`pwd`/sdk
COMBINE=$TMPOUTDIR/combine

# Adapters to include in AAR
# space after each item required
LIBS=""
LIBS+="mediation/mediatedviews/AdColony "
LIBS+="mediation/mediatedviews/AdMob "
LIBS+="mediation/mediatedviews/Amazon "
LIBS+="mediation/mediatedviews/Chartboost "
LIBS+="mediation/mediatedviews/Facebook "
LIBS+="mediation/mediatedviews/GooglePlay "
LIBS+="mediation/mediatedviews/InMobi "
LIBS+="mediation/mediatedviews/MillennialMedia "
LIBS+="mediation/mediatedviews/MoPub "
LIBS+="mediation/mediatedviews/InMobi "
LIBS+="mediation/mediatedviews/Vdopia "
LIBS+="mediation/mediatedviews/Vungle "
LIBS+="mediation/mediatedviews/Yahoo "

LIBS+="mediation/mediating/Google "
LIBS+="mediation/mediating/MoPub "

LIBS+="sdk "

# for moving mediation adapter aars
function moveaar {
    NAME=${1//\//_}
    echoX 'moving aar for' $NAME
    cp $BASEDIR/$1/$AARPATH/*.aar $TMPOUTDIR/$NAME.aar
    unzip $TMPOUTDIR/$NAME.aar -d $TMPOUTDIR/$NAME
    (cd $COMBINE; jar xf $TMPOUTDIR/$NAME/classes.jar)
}

function echoX {
    echo -e "BUILDSDKLOG: $@"
}

#####
# Execute
#####
echoX "Begin Building SDK AARs"

#####
# Prep
#####
rm -rf $OUTDIR
rm -rf $TMPOUTDIR

#####
# Build
#####
echoX "Building AN SDK"
# build AN SDK
gradle clean assembleRelease

echoX "Building AN Mediation Adapters"
# build Mediation adapters
cd $MEDDIR
gradle clean assembleRelease

echoX "Building Mediation Adapters for AN"
# build Mediated adapters
cd $MEDFORDIR
gradle clean assembleRelease

#####
# Copy AARs
#####
echoX "Copying AARs"
# Prep to move AARs
mkdir -p $COMBINE
mkdir -p $OUTDIR

# move aars
for i in $LIBS;
do
	moveaar $i
done

# merge classes.jar files
echoX "Combining for classes.jar"
cd $COMBINE
jar cf $TMPOUTDIR/classes.jar com*

#####
# Package ANSDK.aar
####

echoX "packaging ANSDK.aar"
SDKAAR=$OUTDIR/ANSDKAAR
STAGE=$OUTDIR/ANSDK
mkdir -p $SDKAAR
mkdir -p $STAGE

unzip $SDKDIR/$AARPATH/sdk-release.aar -d $SDKAAR
cp -f $TMPOUTDIR/classes.jar $SDKAAR/classes.jar
mkdir -p $SDKAAR/libs
cp $MEDDIR/MillennialMedia/libs/*.jar $SDKAAR/libs
cd $SDKAAR
zip -r $STAGE/ANSDK.aar *
rm -rf $SDKAAR

##### README
echoX "Create README"
cd $STAGE
touch README.txt

echo -e "
The AppNexus Mobile Advertising SDK for Android
===============================================

Include ANSDK.aar in your project in order to receive AppNexus ads. ANSDK.aar includes the AppNexus SDK as well as all of the adapters that enable the AppNexus SDK to mediate supported third-party networks. Check the Documentation for a complete list of supported networks. 

ANSDK.aar also contains adapters used by the Google or MoPub SDKs to mediate the AppNexus SDK.

Documentation is available on our wiki: https://wiki.appnexus.com/display/sdk/Mobile+SDKs.

" >> README.txt

##### zip ANSDK.aar + README.txt
echoX "zip ANSDK.zip"
zip -r $OUTDIR/ANSDK.zip .

# cleanup
rm -rf $TMPOUTDIR

echoX "End script"

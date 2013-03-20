echo ""
echo Type your first name and press ENTER:
read FIRST_NAME
if [ ${#FIRST_NAME} -eq 0 ]; then
      # length of entered text is 0
      echo "You did not enter a name."
      exit;
   else
      :
fi
echo Type your last name and press ENTER:
read LAST_NAME
if [ ${#LAST_NAME} -eq 0 ]; then
      # length of entered text is 0
      echo "You did not enter a name."
      exit;
   else
      :
fi

# check if the name entered is present in the file /names.txt
# use quiet mode, which does not print standard output
# grep --quiet "$FIRST_NAME $LAST_NAME" /names.txt;

# $? holds the exit code from grep, which is 0 if the string is found,
# or 1 (or an error code) if the string is not found.
# if [ $? -ne 0 ]; then
#      # the student name is not present in /names.txt, so exit
#       echo "Name not found"
#       exit;
#    else
#       :
# fi

echo "Welcome, $FIRST_NAME $LAST_NAME!"
FULL_NAME=$LAST_NAME-$FIRST_NAME;

# store the directory of this script in BASEDIR
BASEDIR=$(dirname $0);

# change directory to BASEDIR
cd $BASEDIR;

# must open ViMaP as a child process, or won't be able to call screencapture
# Vimap jar and associated files must be in Macintosh HD/vimap directory
function_to_fork() {
   # run vimap-j5e.jar as a jar, with the menu title as
   # "ViMaP", and in 32-bit mode
   java -jar -Xdock:name="ViMaP" -d32 /vimap/vimap-j5j.jar
}

# call the function that opens ViMaP, as a new process
function_to_fork &

DELAY_IN_SEC=60; # seconds to delay between screen captures
COUNTER=$(expr 2 \* 3600); # how many screen captures to take in 2 hours
let COUNTER/=$DELAY_IN_SEC;

while [ $COUNTER -gt 0 ]
do
   echo $COUNTER 
   MY_DATE_TIME=$(date +%d\-%m\-%Y\_%H-%M-%S); 
   MY_DATE=$(date +%d\-%m\-%Y);

   if [ -d /captures ]; then
         :
      else
         # directory "captures" does not exist, so create it
         mkdir /captures
   fi

   if [ -d /captures/$MY_DATE ]; then
         :
      else
         # directory "captures/current-date" does not exist, so create it
         mkdir /captures/$MY_DATE
   fi

   # capture whole screen as a jpg. Don't play a sound. Store in captures/currentdate, as last-first-datetime
   screencapture -t jpg -x /captures/$MY_DATE/$FULL_NAME-$MY_DATE_TIME.jpg;

   # (OMIT FOR NOW) convert the jpg to a smaller jpg, 800 pixels across and proportionally tall. overwrite the old jpg
   # sips /captures/$MY_DATE/$FULL_NAME-$MY_DATE_TIME.jpg --resampleWidth 800 --out /captures/$MY_DATE/$FULL_NAME-$MY_DATE_TIME.jpg;
   sleep $DELAY_IN_SEC;
   let COUNTER-=1;
done

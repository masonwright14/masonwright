
Usage:

on my laptop:
java -Djava.library.path=/Users/masonwright/Applications/IBM/ILOG/CPLEX_Studio125/cplex/bin/x86-64_darwin -jar coalition.jar tabuOne newfrat

on server:
java -Djava.library.path=/mod/cplex/cplex_studio1251/cplex/bin/x86-64_sles10_4.1 -jar coalition.jar tabuOne newfrat

to run Ant build:
ant -f build.xml

location of cplex dylib/so files on my laptop:
/Users/masonwright/Applications/IBM/ILOG/CPLEX_Studio125/cplex/bin/x86-64_darwin

location of cplex.jar on server:
/mod/cplex/cplex_studio1251/cplex/lib

location of cplex dylib/so files on server:
/mod/cplex/cplex_studio1251/cplex/bin/x86-64_sles10_4.1

Running on server:
java -jar coalition.jar
java -jar coalition.jar randomOpt newfrat
java -Djava.library.path=/mod/cplex/cplex_studio1251/cplex/bin/x86-64_sles10_4.1 -jar coalition.jar tabuOne newfrat


mySession1:
java -Djava.library.path=/mod/cplex/cplex_studio1251/cplex/bin/x86-64_sles10_4.1 -jar coalition.jar tabuEach newfrat
mySession2:
java -Djava.library.path=/mod/cplex/cplex_studio1251/cplex/bin/x86-64_sles10_4.1 -jar coalition.jar tabuEach random_20_agents
mySession3:
java -Djava.library.path=/mod/cplex/cplex_studio1251/cplex/bin/x86-64_sles10_4.1 -jar coalition.jar tabuEach rndUncor_20_agents

############################

mySession1:
java -Djava.library.path=/mod/cplex/cplex_studio1251/cplex/bin/x86-64_sles10_4.1 -jar coalition.jar tabuEach free
mySession2:
java -Djava.library.path=/mod/cplex/cplex_studio1251/cplex/bin/x86-64_sles10_4.1 -jar coalition.jar tabuEach vand
mySession3:
java -Djava.library.path=/mod/cplex/cplex_studio1251/cplex/bin/x86-64_sles10_4.1 -jar coalition.jar tabuEach random_30_agents
mySession4:
java -Djava.library.path=/mod/cplex/cplex_studio1251/cplex/bin/x86-64_sles10_4.1 -jar coalition.jar tabuEach rndUncor_30_agents

############################

mySession1:
java -Djava.library.path=/mod/cplex/cplex_studio1251/cplex/bin/x86-64_sles10_4.1 -jar coalition.jar tabuAllOpt newfrat
mySession2:
java -Djava.library.path=/mod/cplex/cplex_studio1251/cplex/bin/x86-64_sles10_4.1 -jar coalition.jar tabuAllOpt random_20_agents
mySession3:
java -Djava.library.path=/mod/cplex/cplex_studio1251/cplex/bin/x86-64_sles10_4.1 -jar coalition.jar tabuAllOpt rndUncor_20_agents


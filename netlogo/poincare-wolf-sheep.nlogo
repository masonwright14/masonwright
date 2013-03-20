globals [grass ;; current number of grass patches
  grass-was ;; last tick's number of grass patches
  mean-wolf-energy-was ;; last tick's mean wolf energy
  wolf-pop-was1 ;; last tick's wolf population
  wolf-pop-was2 ;; wolf population from 2 ticks ago
  last-max ;; most recent local max wolf population
  wolf-pop-list ;; list of wolf populations from every tick, normally stored in reverse for efficiency
  saved-wolf-pop-list ;; saved list of wolf populations, for rebuilding the list after plotting recurrence plot (which deletes its way through the list)
  ]
breed [sheep a-sheep]  ;; sheep is its own plural, so we use "a-sheep" as the singular.
breed [wolves wolf]
turtles-own [energy]       ;; both wolves and sheep have energy
patches-own [countdown]

to setup
  clear-all
  ask patches [ set pcolor green ]
    ask patches [
      set countdown random 30 
      set pcolor one-of [green brown]
    ]
  set-default-shape sheep "sheep"
  create-sheep initial-number-sheep  
  [
    set color white
    set size 1.5  ;; easier to see
    set label-color blue - 2
    set energy random (2 * sheep-gain-from-food)
    setxy random-xcor random-ycor
  ]
  set-default-shape wolves "wolf"
  create-wolves initial-number-wolves  
  [
    set color black
    set size 1.5  ;; easier to see
    set energy random (2 * wolf-gain-from-food)
    setxy random-xcor random-ycor
  ]
  set wolf-pop-was1 count wolves
  set wolf-pop-was2 count wolves
  set mean-wolf-energy-was mean [energy] of wolves
  set grass count patches with [pcolor = green]
  set last-max 0
  set wolf-pop-list (list count wolves)
  update-plot
end

to go
  if not any? turtles [ stop ]
  ask sheep [
    move
    set energy energy - 1
    eat-grass
    death
    reproduce-sheep
  ]
  ask wolves [
    move
    set energy energy - 1
    catch-sheep
    death
    reproduce-wolves
  ]
   if empty? wolf-pop-list
        [set wolf-pop-list reverse saved-wolf-pop-list] ;; if plotting recurrence has emptied the list, restore it with the saved copy
   set wolf-pop-list fput (count wolves) wolf-pop-list ;; add the current wolf population to the list (first, for efficiency)
   ask patches [ grow-grass ] 
  tick
  update-plot
  if plot-recurrence?
       [if ticks mod 50 = 0 and any? wolves
              [plot-recurrence]
       ]
end

to move  ;; turtle procedure
  rt random 50
  lt random 50
  fd 1
end

to eat-grass  ;; sheep procedure
  ;; sheep eat grass, turn the patch brown
  if pcolor = green [
    set pcolor brown
    set energy energy + sheep-gain-from-food  
  ]
end

to reproduce-sheep  
  if random-float 100 < sheep-reproduce [  
    set energy (energy / 2)                
    hatch 1 [ rt random-float 360 fd 1 ]   
  ]
end

to reproduce-wolves  
  if random-float 100 < wolf-reproduce [  
    set energy (energy / 2)               
    hatch 1 [ rt random-float 360 fd 1 ] 
  ]
end

to catch-sheep 
  let prey one-of sheep-here                    
  if prey != nobody                             
    [ ask prey [ die ]                          
      set energy energy + wolf-gain-from-food ] 
end

to death  ;; turtle procedure
  ;; when energy dips below zero, die
  if energy < 0 [ die ]
end

to grow-grass
  ;; countdown on brown patches: if reach 0, grow some grass
  if pcolor = brown [
    ifelse countdown <= 0
      [ set pcolor green
        set countdown 30 ]
      [ set countdown countdown - 1 ]
  ]
end

to update-plot
  set grass-was grass
  set grass count patches with [pcolor = green]
  set-current-plot "populations"
  set-current-plot-pen "sheep"
  plot count sheep
  set-current-plot-pen "wolves"
  plot count wolves
  set-current-plot-pen "grass/4"
  plot grass / 4
 
  set-current-plot "energy"
  if any? wolves
        [set-current-plot-pen "mean-wolf-e"
         plot mean [energy] of wolves]
  if any? sheep
        [set-current-plot-pen "mean-sheep-e"
         plot mean [energy] of sheep]
 
  if (grass-was <= 1200) and grass > 1200
       [set-current-plot "poincare-grass-increasing-1200"
        set-current-plot-pen "poincare"
        set-plot-pen-mode 2 ;; sets plot pen to point mode
        plotxy (count sheep) (count wolves)
       ]
  
  if any? wolves and (mean-wolf-energy-was <= 12) and mean [energy] of wolves > 12
      [set-current-plot "poincare-wolf-energy-increasing-12"
       set-current-plot-pen "default"
       set-plot-pen-mode 2
       plotxy (count sheep) (count wolves)
      ]
  if any? wolves
      [set mean-wolf-energy-was mean [energy] of wolves] ;; update the record of last tick's mean wolf energy
  
  if wolf-pop-was1 > (count wolves) and wolf-pop-was1 > wolf-pop-was2 ;; if the last tick was a local max for wolf population
      [if last-max != 0 ;; if this is not the first local max for wolf population
            [set-current-plot "first-recurrence-wolf-pop-max"
             set-current-plot-pen "default"
             set-plot-pen-mode 2
             plotxy last-max wolf-pop-was1 ;; plot the previous tick's (local max's) wolf population vs. the previous local max 
            ]
       set last-max wolf-pop-was1 ;; update the record of the most recent local max
      ]
  
  set wolf-pop-was2 wolf-pop-was1
  set wolf-pop-was1 count wolves ;; update the record of the previous 2 wolf populations
end

to plot-recurrence
  set wolf-pop-list reverse wolf-pop-list ;; reverse the list of wolf populations, to put them in order
  if empty? wolf-pop-list ;; if recurrence has just been plotted, emptying the list
       [set wolf-pop-list saved-wolf-pop-list] ;; restore it with the saved copy
  set saved-wolf-pop-list wolf-pop-list ;; save a copy of the list
  set-current-plot "recurrence-wolf-pop"
  clear-plot
  set-plot-pen-mode 2
  let index 0 ;; this is the index (with respect to the original, uncropped list of populations) of the current population item being handled
  let initial-length length wolf-pop-list
  let reduced-wpl wolf-pop-list ;; reduced-wpl will be used as a cut-down version of the list, in order to find multiple "hits" via the command
                                ;; "position," which returns the index only of the first hit found. To get around this shortcoming, the program
                                ;; will crop off the first items of reduced-wpl until the first "hit" is removed, then search via "position"
                                ;; again, and repeat 4 times. a "while" loop would find every recurrence, but have little effect on the look
                                ;; of the graph, and it seems to stall NetLogo every time; this limited repeat loop is a compromise for the sake
                                ;; of saving time and not overloading NetLogo.
  while [index <= (initial-length - 1)] ;; iterate for each item in the original list
       [let a first wolf-pop-list ;; a is the value of item #index from the original list
        let match position a reduced-wpl ;; match is the index (in the cropped list of populations) of the first match for "a"
        if match != false ;; match = false if no matches are found
             [plotxy (match + index) index ;; plot the point. index must be added to match so that match will correspond to the index of the
                                           ;; match from the original list, not the cropped list.
              plotxy index (match + index) ;; plot the reflection of the point too.
              let cropped 0 ;; cropped will equal the number of additional items removed from reduced-wpl as more hits are found.
              let q 1
              repeat 4
                [if match != false
                 [repeat (match + 1) ;; this loop removes all the first items from reduced-wpl, up to and including the most recent match.
                                     ;; this prevents the same match from being plotted multiple times (and incorrectly, after the first time).
                     [ifelse not empty? reduced-wpl
                          [set reduced-wpl but-first reduced-wpl
                           set cropped cropped + 1 ;; cropped counts the number of additional items removed from reduced-wpl
                          ]
                          [set q 0]
                     ]
                if q = 1 ;; if reduced-wpl has not run out of items to check
                     [set match position a reduced-wpl
                      if match != false
                          [plotxy (match + index + cropped) index ;; match + index + cropped = the index of an item from the original list.
                           plotxy index (match + index + cropped)
                          ]
                     ]
                 ]
                ]
              ]
        set wolf-pop-list but-first wolf-pop-list ;; once 5 iterations have been done, remove the first item from the list
        set reduced-wpl wolf-pop-list ;; re-align reduced-wpl with wolf-pop-list
        set index (index + 1) ;; increment the index counter
       ]
end


; Copyright 1997 Uri Wilensky. All rights reserved.
; The full copyright notice is in the Information tab.
@#$#@#$#@
GRAPHICS-WINDOW
350
10
819
500
25
25
9.0
1
14
1
1
1
0
1
1
1
-25
25
-25
25
1
1
1
ticks

SLIDER
5
51
179
84
initial-number-sheep
initial-number-sheep
0
250
100
1
1
NIL
HORIZONTAL

SLIDER
5
88
179
121
sheep-gain-from-food
sheep-gain-from-food
0.0
50.0
4
1.0
1
NIL
HORIZONTAL

SLIDER
5
123
179
156
sheep-reproduce
sheep-reproduce
1.0
20.0
6
1.0
1
%
HORIZONTAL

SLIDER
183
51
348
84
initial-number-wolves
initial-number-wolves
0
250
60
1
1
NIL
HORIZONTAL

SLIDER
183
87
348
120
wolf-gain-from-food
wolf-gain-from-food
0.0
100.0
25
1.0
1
NIL
HORIZONTAL

SLIDER
183
123
348
156
wolf-reproduce
wolf-reproduce
0.0
20.0
10
1.0
1
%
HORIZONTAL

BUTTON
5
12
74
45
setup
setup
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL

BUTTON
87
12
154
45
go
go
T
1
T
OBSERVER
NIL
NIL
NIL
NIL

PLOT
9
216
307
384
populations
time
pop.
0.0
100.0
0.0
100.0
true
true
PENS
"sheep" 1.0 0 -13345367 true
"wolves" 1.0 0 -2674135 true
"grass/4" 1.0 0 -10899396 true

MONITOR
14
165
85
210
sheep
count sheep
3
1
11

MONITOR
89
165
171
210
wolves
count wolves
3
1
11

PLOT
832
10
1094
160
poincare-grass-increasing-1200
sheep-pop
wolf-pop
0.0
10.0
0.0
10.0
true
false
PENS
"default" 1.0 0 -16777216 true
"poincare" 1.0 0 -16777216 true

MONITOR
185
167
242
212
grass
grass
17
1
11

PLOT
9
391
344
555
energy
time
energy
0.0
10.0
0.0
10.0
true
true
PENS
"mean-sheep-e" 1.0 0 -13345367 true
"mean-wolf-e" 1.0 0 -2674135 true

PLOT
833
163
1094
313
poincare-wolf-energy-increasing-12
sheep-pop
wolf-pop
0.0
10.0
0.0
10.0
true
false
PENS
"default" 1.0 0 -16777216 true

PLOT
834
324
1093
474
first-recurrence-wolf-pop-max
pop-k
pop-k+1
0.0
10.0
0.0
10.0
true
false
PENS
"default" 1.0 0 -16777216 true

BUTTON
693
526
824
559
NIL
plot-recurrence
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL

PLOT
837
480
1093
709
recurrence-wolf-pop
time
time
0.0
10.0
0.0
10.0
true
false
PENS
"default" 1.0 0 -16777216 true

SWITCH
185
12
347
45
plot-recurrence?
plot-recurrence?
0
1
-1000

@#$#@#$#@
WHAT IS IT?
-----------
This model explores the stability of predator-prey ecosystems. Such a system is called unstable if it tends to result in extinction for one or more species involved.  In contrast, a system is stable if it tends to maintain itself over time, despite fluctuations in population sizes.


HOW IT WORKS
------------
There are two main variations to this model.

In the first variation, wolves and sheep wander randomly around the landscape, while the wolves look for sheep to prey on. Each step costs the wolves energy, and they must eat sheep in order to replenish their energy - when they run out of energy they die. To allow the population to continue, each wolf or sheep has a fixed probability of reproducing at each time step. This variation produces interesting population dynamics, but is ultimately unstable.

The second variation includes grass (green) in addition to wolves and sheep. The behavior of the wolves is identical to the first variation, however this time the sheep must eat grass in order to maintain their energy - when they run out of energy they die. Once grass is eaten it will only regrow after a fixed amount of time. This variation is more complex than the first, but it is generally stable.

The construction of this model is described in two papers by Wilensky & Reisman referenced below.


HOW TO USE IT
-------------
1. Set the GRASS? switch to TRUE to include grass in the model, or to FALSE to only include wolves (red) and sheep (white).
2. Adjust the slider parameters (see below), or use the default settings.
3. Press the SETUP button.
4. Press the GO button to begin the simulation.
5. Look at the monitors to see the current population sizes
6. Look at the POPULATIONS plot to watch the populations fluctuate over time

Parameters:
INITIAL-NUMBER-SHEEP: The initial size of sheep population
INITIAL-NUMBER-WOLVES: The initial size of wolf population
SHEEP-GAIN-FROM-FOOD: The amount of energy sheep get for every grass patch eaten
WOLF-GAIN-FROM-FOOD: The amount of energy wolves get for every sheep eaten
SHEEP-REPRODUCE: The probability of a sheep reproducing at each time step
WOLF-REPRODUCE: The probability of a wolf reproducing at each time step
GRASS?: Whether or not to include grass in the model
GRASS-REGROWTH-TIME: How long it takes for grass to regrow once it is eaten
SHOW-ENERGY?: Whether or not to show the energy of each animal as a number

Notes:
- one unit of energy is deducted for every step a wolf takes
- when grass is included, one unit of energy is deducted for every step a sheep takes


THINGS TO NOTICE
----------------
When grass is not included, watch as the sheep and wolf populations fluctuate. Notice that increases and decreases in the sizes of each population are related. In what way are they related? What eventually happens?

Once grass is added, notice the green line added to the population plot representing fluctuations in the amount of grass. How do the sizes of the three populations appear to relate now? What is the explanation for this?

Why do you suppose that some variations of the model might be stable while others are not?


THINGS TO TRY
-------------
Try adjusting the parameters under various settings. How sensitive is the stability of the model to the particular parameters?

Can you find any parameters that generate a stable ecosystem that includes only wolves and sheep?

Try setting GRASS? to TRUE, but setting INITIAL-NUMBER-WOLVES to 0. This gives a stable ecosystem with only sheep and grass. Why might this be stable while the variation with only sheep and wolves is not?

Notice that under stable settings, the populations tend to fluctuate at a predictable pace. Can you find any parameters that will speed this up or slow it down?

Try changing the reproduction rules -- for example, what would happen if reproduction depended on energy rather than being determined by a fixed probability?


EXTENDING THE MODEL
-------------------
There are a number ways to alter the model so that it will be stable with only wolves and sheep (no grass). Some will require new elements to be coded in or existing behaviors to be changed. Can you develop such a version?


NETLOGO FEATURES
----------------
Note the use of breeds to model two different kinds of "turtles": wolves and sheep. Note the use of patches to model grass.

Note use of the ONE-OF agentset reporter to select a random sheep to be eaten by a wolf.


RELATED MODELS
---------------
Look at Rabbits Grass Weeds for another model of interacting populations with different rules.


CREDITS AND REFERENCES
----------------------
Wilensky, U. & Reisman, K. (1999). Connected Science: Learning Biology through Constructing and Testing Computational Theories -- an Embodied Modeling Approach. International Journal of Complex Systems, M. 234, pp. 1 - 12. (This model is a slightly extended version of the model described in the paper.)

Wilensky, U. & Reisman, K. (2006). Thinking like a Wolf, a Sheep or a Firefly: Learning Biology through Constructing and Testing Computational Theories -- an Embodied Modeling Approach. Cognition & Instruction, 24(2), pp. 171-209.
http://ccl.northwestern.edu/papers/wolfsheep.pdf


HOW TO CITE
-----------
If you mention this model in an academic publication, we ask that you include these citations for the model itself and for the NetLogo software:
- Wilensky, U. (1997).  NetLogo Wolf Sheep Predation model.  http://ccl.northwestern.edu/netlogo/models/WolfSheepPredation.  Center for Connected Learning and Computer-Based Modeling, Northwestern University, Evanston, IL.
- Wilensky, U. (1999). NetLogo. http://ccl.northwestern.edu/netlogo/. Center for Connected Learning and Computer-Based Modeling, Northwestern University, Evanston, IL.

In other publications, please use:
- Copyright 1997 Uri Wilensky. All rights reserved. See http://ccl.northwestern.edu/netlogo/models/WolfSheepPredation for terms of use.


COPYRIGHT NOTICE
----------------
Copyright 1997 Uri Wilensky. All rights reserved.

Permission to use, modify or redistribute this model is hereby granted, provided that both of the following requirements are followed:
a) this copyright notice is included.
b) this model will not be redistributed for profit without permission from Uri Wilensky. Contact Uri Wilensky for appropriate licenses for redistribution for profit.

This model was created as part of the project: CONNECTED MATHEMATICS: MAKING SENSE OF COMPLEX PHENOMENA THROUGH BUILDING OBJECT-BASED PARALLEL MODELS (OBPML).  The project gratefully acknowledges the support of the National Science Foundation (Applications of Advanced Technologies Program) -- grant numbers RED #9552950 and REC #9632612.

This model was converted to NetLogo as part of the projects: PARTICIPATORY SIMULATIONS: NETWORK-BASED DESIGN FOR SYSTEMS LEARNING IN CLASSROOMS and/or INTEGRATED SIMULATION AND MODELING ENVIRONMENT. The project gratefully acknowledges the support of the National Science Foundation (REPP & ROLE programs) -- grant numbers REC #9814682 and REC-0126227. Converted from StarLogoT to NetLogo, 2000.

@#$#@#$#@
default
true
0
Polygon -7500403 true true 150 5 40 250 150 205 260 250

airplane
true
0
Polygon -7500403 true true 150 0 135 15 120 60 120 105 15 165 15 195 120 180 135 240 105 270 120 285 150 270 180 285 210 270 165 240 180 180 285 195 285 165 180 105 180 60 165 15

arrow
true
0
Polygon -7500403 true true 150 0 0 150 105 150 105 293 195 293 195 150 300 150

box
false
0
Polygon -7500403 true true 150 285 285 225 285 75 150 135
Polygon -7500403 true true 150 135 15 75 150 15 285 75
Polygon -7500403 true true 15 75 15 225 150 285 150 135
Line -16777216 false 150 285 150 135
Line -16777216 false 150 135 15 75
Line -16777216 false 150 135 285 75

bug
true
0
Circle -7500403 true true 96 182 108
Circle -7500403 true true 110 127 80
Circle -7500403 true true 110 75 80
Line -7500403 true 150 100 80 30
Line -7500403 true 150 100 220 30

butterfly
true
0
Polygon -7500403 true true 150 165 209 199 225 225 225 255 195 270 165 255 150 240
Polygon -7500403 true true 150 165 89 198 75 225 75 255 105 270 135 255 150 240
Polygon -7500403 true true 139 148 100 105 55 90 25 90 10 105 10 135 25 180 40 195 85 194 139 163
Polygon -7500403 true true 162 150 200 105 245 90 275 90 290 105 290 135 275 180 260 195 215 195 162 165
Polygon -16777216 true false 150 255 135 225 120 150 135 120 150 105 165 120 180 150 165 225
Circle -16777216 true false 135 90 30
Line -16777216 false 150 105 195 60
Line -16777216 false 150 105 105 60

car
false
0
Polygon -7500403 true true 300 180 279 164 261 144 240 135 226 132 213 106 203 84 185 63 159 50 135 50 75 60 0 150 0 165 0 225 300 225 300 180
Circle -16777216 true false 180 180 90
Circle -16777216 true false 30 180 90
Polygon -16777216 true false 162 80 132 78 134 135 209 135 194 105 189 96 180 89
Circle -7500403 true true 47 195 58
Circle -7500403 true true 195 195 58

circle
false
0
Circle -7500403 true true 0 0 300

circle 2
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240

cow
false
0
Polygon -7500403 true true 200 193 197 249 179 249 177 196 166 187 140 189 93 191 78 179 72 211 49 209 48 181 37 149 25 120 25 89 45 72 103 84 179 75 198 76 252 64 272 81 293 103 285 121 255 121 242 118 224 167
Polygon -7500403 true true 73 210 86 251 62 249 48 208
Polygon -7500403 true true 25 114 16 195 9 204 23 213 25 200 39 123

cylinder
false
0
Circle -7500403 true true 0 0 300

dot
false
0
Circle -7500403 true true 90 90 120

face happy
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 255 90 239 62 213 47 191 67 179 90 203 109 218 150 225 192 218 210 203 227 181 251 194 236 217 212 240

face neutral
false
0
Circle -7500403 true true 8 7 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Rectangle -16777216 true false 60 195 240 225

face sad
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 168 90 184 62 210 47 232 67 244 90 220 109 205 150 198 192 205 210 220 227 242 251 229 236 206 212 183

fish
false
0
Polygon -1 true false 44 131 21 87 15 86 0 120 15 150 0 180 13 214 20 212 45 166
Polygon -1 true false 135 195 119 235 95 218 76 210 46 204 60 165
Polygon -1 true false 75 45 83 77 71 103 86 114 166 78 135 60
Polygon -7500403 true true 30 136 151 77 226 81 280 119 292 146 292 160 287 170 270 195 195 210 151 212 30 166
Circle -16777216 true false 215 106 30

flag
false
0
Rectangle -7500403 true true 60 15 75 300
Polygon -7500403 true true 90 150 270 90 90 30
Line -7500403 true 75 135 90 135
Line -7500403 true 75 45 90 45

flower
false
0
Polygon -10899396 true false 135 120 165 165 180 210 180 240 150 300 165 300 195 240 195 195 165 135
Circle -7500403 true true 85 132 38
Circle -7500403 true true 130 147 38
Circle -7500403 true true 192 85 38
Circle -7500403 true true 85 40 38
Circle -7500403 true true 177 40 38
Circle -7500403 true true 177 132 38
Circle -7500403 true true 70 85 38
Circle -7500403 true true 130 25 38
Circle -7500403 true true 96 51 108
Circle -16777216 true false 113 68 74
Polygon -10899396 true false 189 233 219 188 249 173 279 188 234 218
Polygon -10899396 true false 180 255 150 210 105 210 75 240 135 240

house
false
0
Rectangle -7500403 true true 45 120 255 285
Rectangle -16777216 true false 120 210 180 285
Polygon -7500403 true true 15 120 150 15 285 120
Line -16777216 false 30 120 270 120

leaf
false
0
Polygon -7500403 true true 150 210 135 195 120 210 60 210 30 195 60 180 60 165 15 135 30 120 15 105 40 104 45 90 60 90 90 105 105 120 120 120 105 60 120 60 135 30 150 15 165 30 180 60 195 60 180 120 195 120 210 105 240 90 255 90 263 104 285 105 270 120 285 135 240 165 240 180 270 195 240 210 180 210 165 195
Polygon -7500403 true true 135 195 135 240 120 255 105 255 105 285 135 285 165 240 165 195

line
true
0
Line -7500403 true 150 0 150 300

line half
true
0
Line -7500403 true 150 0 150 150

pentagon
false
0
Polygon -7500403 true true 150 15 15 120 60 285 240 285 285 120

person
false
0
Circle -7500403 true true 110 5 80
Polygon -7500403 true true 105 90 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285 180 195 195 90
Rectangle -7500403 true true 127 79 172 94
Polygon -7500403 true true 195 90 240 150 225 180 165 105
Polygon -7500403 true true 105 90 60 150 75 180 135 105

plant
false
0
Rectangle -7500403 true true 135 90 165 300
Polygon -7500403 true true 135 255 90 210 45 195 75 255 135 285
Polygon -7500403 true true 165 255 210 210 255 195 225 255 165 285
Polygon -7500403 true true 135 180 90 135 45 120 75 180 135 210
Polygon -7500403 true true 165 180 165 210 225 180 255 120 210 135
Polygon -7500403 true true 135 105 90 60 45 45 75 105 135 135
Polygon -7500403 true true 165 105 165 135 225 105 255 45 210 60
Polygon -7500403 true true 135 90 120 45 150 15 180 45 165 90

sheep
false
15
Rectangle -1 true true 166 225 195 285
Rectangle -1 true true 62 225 90 285
Rectangle -1 true true 30 75 210 225
Circle -1 true true 135 75 150
Circle -7500403 true false 180 76 116

square
false
0
Rectangle -7500403 true true 30 30 270 270

square 2
false
0
Rectangle -7500403 true true 30 30 270 270
Rectangle -16777216 true false 60 60 240 240

star
false
0
Polygon -7500403 true true 151 1 185 108 298 108 207 175 242 282 151 216 59 282 94 175 3 108 116 108

target
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240
Circle -7500403 true true 60 60 180
Circle -16777216 true false 90 90 120
Circle -7500403 true true 120 120 60

tree
false
0
Circle -7500403 true true 118 3 94
Rectangle -6459832 true false 120 195 180 300
Circle -7500403 true true 65 21 108
Circle -7500403 true true 116 41 127
Circle -7500403 true true 45 90 120
Circle -7500403 true true 104 74 152

triangle
false
0
Polygon -7500403 true true 150 30 15 255 285 255

triangle 2
false
0
Polygon -7500403 true true 150 30 15 255 285 255
Polygon -16777216 true false 151 99 225 223 75 224

truck
false
0
Rectangle -7500403 true true 4 45 195 187
Polygon -7500403 true true 296 193 296 150 259 134 244 104 208 104 207 194
Rectangle -1 true false 195 60 195 105
Polygon -16777216 true false 238 112 252 141 219 141 218 112
Circle -16777216 true false 234 174 42
Rectangle -7500403 true true 181 185 214 194
Circle -16777216 true false 144 174 42
Circle -16777216 true false 24 174 42
Circle -7500403 false true 24 174 42
Circle -7500403 false true 144 174 42
Circle -7500403 false true 234 174 42

turtle
true
0
Polygon -10899396 true false 215 204 240 233 246 254 228 266 215 252 193 210
Polygon -10899396 true false 195 90 225 75 245 75 260 89 269 108 261 124 240 105 225 105 210 105
Polygon -10899396 true false 105 90 75 75 55 75 40 89 31 108 39 124 60 105 75 105 90 105
Polygon -10899396 true false 132 85 134 64 107 51 108 17 150 2 192 18 192 52 169 65 172 87
Polygon -10899396 true false 85 204 60 233 54 254 72 266 85 252 107 210
Polygon -7500403 true true 119 75 179 75 209 101 224 135 220 225 175 261 128 261 81 224 74 135 88 99

wheel
false
0
Circle -7500403 true true 3 3 294
Circle -16777216 true false 30 30 240
Line -7500403 true 150 285 150 15
Line -7500403 true 15 150 285 150
Circle -7500403 true true 120 120 60
Line -7500403 true 216 40 79 269
Line -7500403 true 40 84 269 221
Line -7500403 true 40 216 269 79
Line -7500403 true 84 40 221 269

wolf
false
0
Rectangle -7500403 true true 195 106 285 150
Rectangle -7500403 true true 195 90 255 105
Polygon -7500403 true true 240 90 217 44 196 90
Polygon -16777216 true false 234 89 218 59 203 89
Rectangle -1 true false 240 93 252 105
Rectangle -16777216 true false 242 96 249 104
Rectangle -16777216 true false 241 125 285 139
Polygon -1 true false 285 125 277 138 269 125
Polygon -1 true false 269 140 262 125 256 140
Rectangle -7500403 true true 45 120 195 195
Rectangle -7500403 true true 45 114 185 120
Rectangle -7500403 true true 165 195 180 270
Rectangle -7500403 true true 60 195 75 270
Polygon -7500403 true true 45 105 15 30 15 75 45 150 60 120

x
false
0
Polygon -7500403 true true 270 75 225 30 30 225 75 270
Polygon -7500403 true true 30 75 75 30 270 225 225 270

@#$#@#$#@
NetLogo 4.1
@#$#@#$#@
setup
set grass? true
repeat 75 [ go ]
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
default
0.0
-0.2 0 1.0 0.0
0.0 1 1.0 0.0
0.2 0 1.0 0.0
link direction
true
0
Line -7500403 true 150 150 90 180
Line -7500403 true 150 150 210 180

@#$#@#$#@
0
@#$#@#$#@

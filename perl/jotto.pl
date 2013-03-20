#!/usr/bin/perl -w

# jotto.pl

# A 3- or 4-letter jotto game. Player guesses words with that number of letters
# and is told how many letters match the secret word, until secret word is guessed.


############ Program outline ##############

# sub decision_screen: choose 3- or 4-letter game
# secret word is randomly chosen by computer from list
# sub welcome_screen
# sub new_guess: takes guess input, checks if player's guess is valid
# sub checkmate: checks if guess equals secret word. If so, game over.
# sub count: counts how many letters of guess are found in secret word,
#            reports number to user
# sub prompt: asks for next guess.


# side menus:
# sub history: prints array of past guesses and how many letters matched the secret word.
# sub help:    help screen.


###########################################


use strict;

my $guess;       #stores input: player's most recent guess
my $score;       #how many letters from guess appear in secret word
my $count = 0;   #how many guesses the player has taken
my $location;    #which screen to return to from help screen
my @history;     #array of player's moves in the format:
                 #(count guess score [this is the first element], . . .)
my $version;     #recods three- or four-letter game
my $secret;      #the secret word

# list of words allowed as guesses; includes words with repeated letters.
my $guesslist_three = ('ace act add ade ado aft age ago aid ail aim air ale all alp amp and ant any ape apt arc are arf ark arm art ash ask asp ass ate auk awe awl axe aye baa bad bag bah bam ban bar bas bat bay bed bee beg bet bib bid big bin bio bit boa bob bog boo bop bot bow box boy bra bud bug bum bun bur bus but buy bue cab cad cam can cap car cat caw cay cel cob cod cog col con coo cop cot cow coy cry cub cud cue cup cur cut dab dad dam day den dew dib did die dig dim din dip doc doe dog don dot dow dry dub dud due dug dun duo dye ear eat ebb eel egg ego eke elf elk ell elm emu end eon era ere erg err ess eve ewe eye fad fag fan far fat fax fay fed fee fen few fey fez fib fie fig fin fir fit fix flu fly fob foe fog fop for fox fry fun fur gab gad gag gal gam gap gar gas gay gee gel gem get gig gin gnu gob god goo got gum gun gut gym gyp had hag ham has hat hay hem hen her hew hex hey hid hie him hip his hit hob hoe hog hop hot how hub hue hug hum hut ice icy ilk ill imp ink inn ion ire irk ism its ivy jab jag jam jar jaw jay jet jew jib jig job jog jot joy jug jut keg ken key kid kin kit koi lab lad lag lam lap law lax lay lea led lee leg lei let lid lie lip lit lob log loo lop lot low lug lye mac mad man map mar mat maw may men met mew mix mob mod mom moo mop mow mud mug mum nab nag nap nay nee net new nib nip nit nob nod nog nor not now nub nun nut oaf oak oar oat obi odd ode off oft ohm oil old one opt orb ore our out ova owe owl own pad pal pan pap par pat paw pay pea pee pen pep per pet pew pie pig pin pip pit ply pod poo pop pot pox pro pry pub pug pun pup pus put qua rag raj ram ran rap rat raw ray red ref reg ret rho rib rid rig rim rin rip rob roc rod roe rot row rub rue rug rum run rut rye sad sag sap sat saw sax say sea see set sew sex she shy sic sin sip sir sis sit six ska ski sky sly sob sod son sop sot sow soy spa spy sty sub sue sum sun sup tab tad tag tan tap tar tau tax tea tee ten the thy tic tie til tin tip tis tit toe ton too top tot tow toy try tub tug tun tut two urn use van vat vee vet vex vie vim vow vox wad wag wan war was wax way web wed wee wet who why wig win wit woe wok won woo wow wry yak yam yaw yay yea yen yes yet yew yon you zag zap zig zip zoo');

my $guesslist_four = ('abet able ably abut aced aces ache achy acid acme acne acre acts adds afar aged ages agog ahoy aide aids ails aims airs airy ajar akin alas alee ales ally alms aloe alps also alto alum amen amid amok amps anal anew anon ants anus aped apes apex arch arcs area aria arid arks arms army arts arty ashy asks asps atom atop aunt aura auto avid avow away awed awls axed axes axis axle ayes babe baby back bags bail bait bake bald bale balk ball balm band bane bang bank bans barb bard bare bark barn bars base bash bask bass bath bats bawl bays bead beak beam bean bear beat beau beck beds beef been beep beer bees beet begs bell belt bend bent best bets bevy bias bibs bide bids bier bike bile bilk bill bind bins bird bite bits bled blew blip blob bloc blot blow blue blur boar boas boat bobs bock bode body bogs boil bold bolo bolt bomb bond bone bong bonk bony boob book boom boon boor boos boot bops bore born boss both bout bowl bows boys brad brag bran bras brat bray bred brew brie brim brio brow buck buds buff bugs bulb bulk bull bump bums bunk buns bunt buoy burn burp burr bury bush bust busy butt buys buzz byes byte cabs cads cafe cage cagy cake calf calk call calm came camp cams cane cans cant cape capo caps card care carp cars cart case cash cask cast cats cave caws cede cell cent chap char chat chef chew chic chin chip chop chow chug chum ciao cite city clad clam clan clap claw clay clef clip clod clog clop clot cloy club clue coal coat coax cobs cock coda code cogs coif coil coin coke cola cold colt coma comb come cone conk cons cook cool coop coos coot cope cops copy cord core cork corn cost cots coup cove cows cozy crab crag cram crap craw crew crib crop crow crud crux cube cubs cued cues cuff cull cult cunt cups curb curd cure curl curs curt cusp cuss cute cuts cyan cyst czar dabs dada dads daft dais dale dame damn damp dams dank dare dark darn dart dash data date daub dawn days daze dead deaf deal dean dear debt deck deed deem deep deer deft defy deli dell demo dent deny desk dewy dhow dial dibs dice dick died dies diet digs dike dill dime dims dine ding dins dint dips dire dirt disc dish disk ditz diva dive dock dodo doer does doff dogs dojo dole dolt dome done dons doom door dope dork dorm dose dote dots dour dove down doze drab drag drat draw dreg drew drip drop drub drug drum dual dubs duck duct dude duds duel dues duet duke dull duly dumb dump dune dung dunk duos dupe dusk dust duty dyed dyes dyke each earl earn ears ease east easy eats ebbs echo ecru eddy edge edgy edit eels eggs egos eked ekes elan elms else emir emit emus ends envy eons epee epic eras errs espy etch euro even ever eves evil ewes exam exit eyed eyes face fact fade fads fags fail fair fake fall fame fang fans fare farm fart fast fate faun faux fawn faze fear feat feed feel fees feet fell felt fend fern feta feud fiat fibs fife figs file fill film find fine fink fins fire firm firs fish fist fits five fizz flab flag flak flan flap flat flaw flax flay flea fled flee flew flex flip flit floe flog flop flow flub flue flux foal foam foci foes fogs foil fold folk fond font food fool foot fops ford fore fork form fort foul four fowl foxy fray free fret frog from fuck fuel full fume fund funk furs fury fuse fuss fuzz gabs gads gaga gags gain gait gala gale gall gals game gamy gang gape gaps garb gash gasp gate gave gawk gays gaze gear geek geld gems gene gent germ gets gibe gift gild gill gilt gimp gins gird girl gist give glad glee glen glib glob glom glop glow glue glum glut gnat gnaw gnus goad goal goat gobs gods goes gold golf gone gong good goof goon goop gore gout gown grab gram gray grew grey grid grim grin grip grit grow grub gulf gull gulp gums gunk guns guru gush gust guts guys gyms hack hags hail hair hale half hall halo halt hams hand hang hard hare harm harp hart hash hate hats haul have hawk haze hazy head heal heap hear heat heed heel heir held hell helm help hemp hems hens herb herd here hero hers hewn hews hick hide high hike hill hilt hind hint hips hire hiss hits hive hoax hobo hobs hock hoed hoes hogs hold hole holy home hone honk hood hoof hook hoop hoot hope hops horn hose host hour howl hubs hues huff huge hugs hula hulk hull hump hums hung hunk hunt hurl hurt hush husk huts hymn hype ibex ibis iced ices icon idea ides idle idly idol ills imam imps inch inks inky inns into ions iota iris irks iron isle isms itch item jabs jack jade jail jamb jams jars jaws jays jazz jeer jerk jest jets jibe jibs jilt jinx jive jobs jock jogs join joke jolt jots jowl joys judo jugs jump junk jury just jute juts keel keen kegs kelp kept keys kick kids kill kiln kilt kind king kink kiss kite kits kiwi knee knew knit knob knot know kook labs lace lack lacy lads lady lags laid lain lair lake lamb lame lamp land lane laps lard lark lash lass last late laud lava lawn laws lays lazy lead leaf leak lean leap leek leer left legs leis lend lens less lest lets levy lewd liar lice lick lids lien lies lieu life lift like lilt lily lima limb lime limp line link lint lion lips lisp list live load loaf loan lobe lobs lock lode loft loge logo logs loin loll lone long look loom loon loop loot lope lops lord lore lose loss lost lots loud love lows luau lube luck luge lugs lull lump lung lure lurk lush lust lute lynx lyre mace made maid mail maim main make male mall malt mama mane many maps mare mark mars mash mask mass mast mate math mats maul maze mead meal mean meat meek meet meld melt memo mend menu mere mesa mesh mess mice mild mile milk mill mime mind mine mink mint minx mire miss mist mite mitt moan moat mobs mock mode mold mole molt moms monk mood moon moor moos moot mope mops more morn moss most mote moth move mown mows much muck muff mugs mule mull muse mush musk muss must mute mutt myna myth nabs nags nail name nape naps nary nave navy nays nazi near neat neck need neon nerd nest nets news newt next nice nick nigh nine nips node nods none nook noon nope norm nose nosy note noun nubs nude nuke null numb nuns nuts oafs oaks oars oath oats obey oboe odds odes odor ogle ogre oils oily oink okay okra omen omit once ones only onto onus onyx ooze opal open opts opus oral orbs orca ores orgy ours oust outs ouzo oval oven over owed owes owls owns oxen pace pack pact pads page paid pail pain pair pale pall palm pals pane pang pans pant papa pare park part pass past path pats pave pawn paws pays peak peal pear peas peat peck peed peek peel peep peer pees pegs pelt pens pent peon perk perm pert pest pets pews pick pied pies pigs pike pile pill pimp pine pink pins pint pipe pita pith pits pity pixy plan play plea pled plod plop plot plow ploy plug plum plus pods poem poet poke pole poll polo pomp pond pony pool poor pope pops pore pork port pose posh post posy pots pour pout pray prep prey prig prim prod prom prop pros prow pubs puck puff pugs pull pulp puma pump punk puns punt puny pups pure purr push puts putt pyre quay quip quit quiz race rack racy raft rage rags raid rail rain rake ramp rams rang rank rant rape raps rapt rare rash rate rave rays raze read real ream reap rear reds reed reef reek reel rein rely rend rent rest ribs rice ride rids rife riff rift rigs rile rims rind ring rink riot ripe rips rise risk rite road roam roar robe robs rock rode rods roes role roll romp roof rook room root rope rose rosy rote rots rout rove rows rube rubs ruby rude rued rues ruff rugs ruin rule rump rums rung runs runt ruse rush rust ruts ryes sack sacs safe saga sage sags said sail sake sale salt same sand sane sang sank saps sash sass sate save saws says scab scam scan scar scat scow scum seal seam sear seas seat sect seed seek seem seen seep seer sees self sell send serf sets sewn sews sexy shag shah sham shed shin ship shit shiv shod shoe shoo shop shot show shun shut sick side sift sigh sign silk sill silo silt sing sink sins sips sire sirs site sits size skew skid skim skin skip skis skit slab slam slap slat slaw slay sled slew slid slim slip slit slob slog slop slot slow slug slum slur slut smog smug smut snag snap snip snob snot snow snub snug soak soap soar sobs sock soda sofa soft soil sold sole solo some song soon soot sops sore sort sots soul soup sour sown sows span spar spas spat spay sped spew spin spit spot spry spun spur stab stag star stay stem step stew stir stop stow stub stud stun subs such suck suds sued sues suet suit sulk sumo sump sums sung sunk suns sure surf swab swag swam swan swap swat sway swig swim swum tabs tack taco tact tags tail take tale talk tall tame tamp tang tank tans tape taps tare tarp tars tart task taut taxi teak teal team tear teas teat teed teem teen tees tell tend tens tent term tern test text than that thaw them then they thin this thud thug tick tics tide tidy tied tier ties tiff tiki tile till tilt time tine tins tint tiny tips tire tits toad toed toes tofu toga togs toil told toll tomb tome tone tons tony took tool toot tops tore torn tort toss tote tots tour tout town tows toys tram trap tray tree trek trim trio trip trot true tsar tuba tube tubs tuck tuft tugs tuna tune turf turn tusk tutu twee twig twin twit tyke type typo tzar ugly undo unit unto upon urge urns used user uses vain vane vans vary vase vast vats veal veer veil vein vent verb very vest veto vets vial vibe vice vied vies view vile vine visa vise void volt vote vows wade wads waft wage wags waif wail wait wake walk wall wand wane want ward ware warm warn warp wars wart wary wash wasp watt wave wavy waxy ways weak wean wear webs weds weed week weep weld well welt wend went wept were west wets what when whet whey whim whip whit whom wick wide wife wigs wild will wilt wily wimp wind wine wing wink wino wins wipe wire wiry wise wish wisp with wits woes woke woks wolf womb wont wood woof wool woos word wore work worm worn wrap wren writ yaks yams yank yard yarn yawn year yeas yell yelp yeti yoga yogi yoke yolk yore your yule yurt zany zaps zeal zero zest zinc zips ziti zoos');


#list of words allowed as the secret word. no words with repeated letters.
my @secretlist_three = ('ace', 'act', 'ade', 'ado', 'aft', 'age', 'ago', 'aid', 'ail', 'aim', 'air', 'ale', 'alp', 'amp', 'and', 'ant', 'any', 'ape', 'apt', 'arc', 'are', 'arf', 'ark', 'arm', 'art', 'ash', 'ask', 'asp', 'ate', 'auk', 'awe', 'awl', 'axe', 'aye', 'bad', 'bag', 'bah', 'bam', 'ban', 'bar', 'bas', 'bat', 'bay', 'bed', 'beg', 'bet', 'bid', 'big', 'bin', 'bio', 'bit', 'boa', 'bog', 'bop', 'bot', 'bow', 'box', 'boy', 'bra', 'bud', 'bug', 'bum', 'bun', 'bur', 'bus', 'but', 'buy', 'bue', 'cab', 'cad', 'cam', 'can', 'cap', 'car', 'cat', 'caw', 'cay', 'cel', 'cob', 'cod', 'cog', 'col', 'con', 'cop', 'cot', 'cow', 'coy', 'cry', 'cub', 'cud', 'cue', 'cup', 'cur', 'cut', 'dab', 'dam', 'day', 'den', 'dew', 'dib', 'die', 'dig', 'dim', 'din', 'dip', 'doc', 'doe', 'dog', 'don', 'dot', 'dow', 'dry', 'dub', 'due', 'dug', 'dun', 'duo', 'dye', 'ear', 'eat', 'ego', 'elf', 'elk', 'elm', 'emu', 'end', 'eon', 'era', 'erg', 'fad', 'fag', 'fan', 'far', 'fat', 'fax', 'fay', 'fed', 'fen', 'few', 'fey', 'fez', 'fib', 'fie', 'fig', 'fin', 'fir', 'fit', 'fix', 'flu', 'fly', 'fob', 'foe', 'fog', 'fop', 'for', 'fox', 'fry', 'fun', 'fur', 'gab', 'gad', 'gal', 'gam', 'gap', 'gar', 'gas', 'gay', 'gel', 'gem', 'get', 'gin', 'gnu', 'gob', 'god', 'got', 'gum', 'gun', 'gut', 'gym', 'gyp', 'had', 'hag', 'ham', 'has', 'hat', 'hay', 'hem', 'hen', 'her', 'hew', 'hex', 'hey', 'hid', 'hie', 'him', 'hip', 'his', 'hit', 'hob', 'hoe', 'hog', 'hop', 'hot', 'how', 'hub', 'hue', 'hug', 'hum', 'hut', 'ice', 'icy', 'ilk', 'imp', 'ink', 'ion', 'ire', 'irk', 'ism', 'its', 'ivy', 'jab', 'jag', 'jam', 'jar', 'jaw', 'jay', 'jet', 'jew', 'jib', 'jig', 'job', 'jog', 'jot', 'joy', 'jug', 'jut', 'keg', 'ken', 'key', 'kid', 'kin', 'kit', 'koi', 'lab', 'lad', 'lag', 'lam', 'lap', 'law', 'lax', 'lay', 'lea', 'led', 'leg', 'lei', 'let', 'lid', 'lie', 'lip', 'lit', 'lob', 'log', 'lop', 'lot', 'low', 'lug', 'lye', 'mac', 'mad', 'man', 'map', 'mar', 'mat', 'maw', 'may', 'men', 'met', 'mew', 'mix', 'mob', 'mod', 'mop', 'mow', 'mud', 'mug', 'nab', 'nag', 'nap', 'nay', 'net', 'new', 'nib', 'nip', 'nit', 'nob', 'nod', 'nog', 'nor', 'not', 'now', 'nub', 'nut', 'oaf', 'oak', 'oar', 'oat', 'obi', 'ode', 'oft', 'ohm', 'oil', 'old', 'one', 'opt', 'orb', 'ore', 'our', 'out', 'ova', 'owe', 'owl', 'own', 'pad', 'pal', 'pan', 'par', 'pat', 'paw', 'pay', 'pea', 'pen', 'per', 'pet', 'pew', 'pie', 'pig', 'pin', 'pit', 'ply', 'pod', 'pot', 'pox', 'pro', 'pry', 'pub', 'pug', 'pun', 'pus', 'put', 'qua', 'rag', 'raj', 'ram', 'ran', 'rap', 'rat', 'raw', 'ray', 'red', 'ref', 'reg', 'ret', 'rho', 'rib', 'rid', 'rig', 'rim', 'rin', 'rip', 'rob', 'roc', 'rod', 'roe', 'rot', 'row', 'rub', 'rue', 'rug', 'rum', 'run', 'rut', 'rye', 'sad', 'sag', 'sap', 'sat', 'saw', 'sax', 'say', 'sea', 'set', 'sew', 'sex', 'she', 'shy', 'sic', 'sin', 'sip', 'sir', 'sit', 'six', 'ska', 'ski', 'sky', 'sly', 'sob', 'sod', 'son', 'sop', 'sot', 'sow', 'soy', 'spa', 'spy', 'sty', 'sub', 'sue', 'sum', 'sun', 'sup', 'tab', 'tad', 'tag', 'tan', 'tap', 'tar', 'tau', 'tax', 'tea', 'ten', 'the', 'thy', 'tic', 'tie', 'til', 'tin', 'tip', 'tis', 'toe', 'ton', 'top', 'tow', 'toy', 'try', 'tub', 'tug', 'tun', 'two', 'urn', 'use', 'van', 'vat', 'vet', 'vex', 'vie', 'vim', 'vow', 'vox', 'wad', 'wag', 'wan', 'war', 'was', 'wax', 'way', 'web', 'wed', 'wet', 'who', 'why', 'wig', 'win', 'wit', 'woe', 'wok', 'won', 'wry', 'yak', 'yam', 'yaw', 'yea', 'yen', 'yes', 'yet', 'yew', 'yon', 'you', 'zag', 'zap', 'zig', 'zip');

my @secretlist_four = ('abet', 'able', 'ably', 'abut', 'aced', 'aces', 'ache', 'achy', 'acid', 'acme', 'acne', 'acre', 'acts', 'aged', 'ages', 'ahoy', 'aide', 'aids', 'ails', 'aims', 'airs', 'airy', 'akin', 'ales', 'alms', 'aloe', 'alps', 'also', 'alto', 'alum', 'amen', 'amid', 'amok', 'amps', 'anew', 'ants', 'anus', 'aped', 'apes', 'apex', 'arch', 'arcs', 'arid', 'arks', 'arms', 'army', 'arts', 'arty', 'ashy', 'atom', 'atop', 'aunt', 'auto', 'avid', 'avow', 'awed', 'awls', 'axed', 'axes', 'axis', 'axle', 'ayes', 'back', 'bags', 'bail', 'bait', 'bake', 'bald', 'bale', 'balk', 'balm', 'band', 'bane', 'bang', 'bank', 'bans', 'bard', 'bare', 'bark', 'barn', 'bars', 'base', 'bash', 'bask', 'bath', 'bats', 'bawl', 'bays', 'bead', 'beak', 'beam', 'bean', 'bear', 'beat', 'beau', 'beck', 'beds', 'begs', 'belt', 'bend', 'bent', 'best', 'bets', 'bevy', 'bias', 'bide', 'bids', 'bier', 'bike', 'bile', 'bilk', 'bind', 'bins', 'bird', 'bite', 'bits', 'bled', 'blew', 'blip', 'bloc', 'blot', 'blow', 'blue', 'blur', 'boar', 'boas', 'boat', 'bock', 'bode', 'body', 'bogs', 'boil', 'bold', 'bolt', 'bond', 'bone', 'bong', 'bonk', 'bony', 'bops', 'bore', 'born', 'both', 'bout', 'bowl', 'bows', 'boys', 'brad', 'brag', 'bran', 'bras', 'brat', 'bray', 'bred', 'brew', 'brie', 'brim', 'brio', 'brow', 'buck', 'buds', 'bugs', 'bulk', 'bump', 'bums', 'bunk', 'buns', 'bunt', 'buoy', 'burn', 'burp', 'bury', 'bush', 'bust', 'busy', 'buys', 'byes', 'byte', 'cabs', 'cads', 'cafe', 'cage', 'cagy', 'cake', 'calf', 'calk', 'calm', 'came', 'camp', 'cams', 'cane', 'cans', 'cant', 'cape', 'capo', 'caps', 'card', 'care', 'carp', 'cars', 'cart', 'case', 'cash', 'cask', 'cast', 'cats', 'cave', 'caws', 'cent', 'chap', 'char', 'chat', 'chef', 'chew', 'chin', 'chip', 'chop', 'chow', 'chug', 'chum', 'ciao', 'cite', 'city', 'clad', 'clam', 'clan', 'clap', 'claw', 'clay', 'clef', 'clip', 'clod', 'clog', 'clop', 'clot', 'cloy', 'club', 'clue', 'coal', 'coat', 'coax', 'cobs', 'coda', 'code', 'cogs', 'coif', 'coil', 'coin', 'coke', 'cola', 'cold', 'colt', 'coma', 'comb', 'come', 'cone', 'conk', 'cons', 'cope', 'cops', 'copy', 'cord', 'core', 'cork', 'corn', 'cost', 'cots', 'coup', 'cove', 'cows', 'cozy', 'crab', 'crag', 'cram', 'crap', 'craw', 'crew', 'crib', 'crop', 'crow', 'crud', 'crux', 'cube', 'cubs', 'cued', 'cues', 'cult', 'cunt', 'cups', 'curb', 'curd', 'cure', 'curl', 'curs', 'curt', 'cusp', 'cute', 'cuts', 'cyan', 'cyst', 'czar', 'dabs', 'daft', 'dais', 'dale', 'dame', 'damn', 'damp', 'dams', 'dank', 'dare', 'dark', 'darn', 'dart', 'dash', 'date', 'daub', 'dawn', 'days', 'daze', 'deaf', 'deal', 'dean', 'dear', 'debt', 'deck', 'deft', 'defy', 'deli', 'demo', 'dent', 'deny', 'desk', 'dewy', 'dhow', 'dial', 'dibs', 'dice', 'dick', 'dies', 'diet', 'digs', 'dike', 'dime', 'dims', 'dine', 'ding', 'dins', 'dint', 'dips', 'dire', 'dirt', 'disc', 'dish', 'disk', 'ditz', 'diva', 'dive', 'dock', 'doer', 'does', 'dogs', 'dole', 'dolt', 'dome', 'done', 'dons', 'dope', 'dork', 'dorm', 'dose', 'dote', 'dots', 'dour', 'dove', 'down', 'doze', 'drab', 'drag', 'drat', 'draw', 'dreg', 'drew', 'drip', 'drop', 'drub', 'drug', 'drum', 'dual', 'dubs', 'duck', 'duct', 'duel', 'dues', 'duet', 'duke', 'duly', 'dumb', 'dump', 'dune', 'dung', 'dunk', 'duos', 'dupe', 'dusk', 'dust', 'duty', 'dyes', 'dyke', 'each', 'earl', 'earn', 'ears', 'east', 'easy', 'eats', 'echo', 'ecru', 'edgy', 'edit', 'egos', 'elan', 'elms', 'emir', 'emit', 'emus', 'ends', 'envy', 'eons', 'epic', 'eras', 'espy', 'etch', 'euro', 'evil', 'exam', 'exit', 'face', 'fact', 'fade', 'fads', 'fags', 'fail', 'fair', 'fake', 'fame', 'fang', 'fans', 'fare', 'farm', 'fart', 'fast', 'fate', 'faun', 'faux', 'fawn', 'faze', 'fear', 'feat', 'felt', 'fend', 'fern', 'feta', 'feud', 'fiat', 'fibs', 'figs', 'file', 'film', 'find', 'fine', 'fink', 'fins', 'fire', 'firm', 'firs', 'fish', 'fist', 'fits', 'five', 'flab', 'flag', 'flak', 'flan', 'flap', 'flat', 'flaw', 'flax', 'flay', 'flea', 'fled', 'flew', 'flex', 'flip', 'flit', 'floe', 'flog', 'flop', 'flow', 'flub', 'flue', 'flux', 'foal', 'foam', 'foci', 'foes', 'fogs', 'foil', 'fold', 'folk', 'fond', 'font', 'fops', 'ford', 'fore', 'fork', 'form', 'fort', 'foul', 'four', 'fowl', 'foxy', 'fray', 'fret', 'frog', 'from', 'fuck', 'fuel', 'fume', 'fund', 'funk', 'furs', 'fury', 'fuse', 'gabs', 'gads', 'gain', 'gait', 'gale', 'gals', 'game', 'gamy', 'gape', 'gaps', 'garb', 'gash', 'gasp', 'gate', 'gave', 'gawk', 'gays', 'gaze', 'gear', 'geld', 'gems', 'gent', 'germ', 'gets', 'gibe', 'gift', 'gild', 'gilt', 'gimp', 'gins', 'gird', 'girl', 'gist', 'give', 'glad', 'glen', 'glib', 'glob', 'glom', 'glop', 'glow', 'glue', 'glum', 'glut', 'gnat', 'gnaw', 'gnus', 'goad', 'goal', 'goat', 'gobs', 'gods', 'goes', 'gold', 'golf', 'gone', 'gore', 'gout', 'gown', 'grab', 'gram', 'gray', 'grew', 'grey', 'grid', 'grim', 'grin', 'grip', 'grit', 'grow', 'grub', 'gulf', 'gulp', 'gums', 'gunk', 'guns', 'gush', 'gust', 'guts', 'guys', 'gyms', 'hack', 'hags', 'hail', 'hair', 'hale', 'half', 'halo', 'halt', 'hams', 'hand', 'hang', 'hard', 'hare', 'harm', 'harp', 'hart', 'hate', 'hats', 'haul', 'have', 'hawk', 'haze', 'hazy', 'head', 'heal', 'heap', 'hear', 'heat', 'heir', 'held', 'helm', 'help', 'hemp', 'hems', 'hens', 'herb', 'herd', 'hero', 'hers', 'hewn', 'hews', 'hick', 'hide', 'hike', 'hilt', 'hind', 'hint', 'hips', 'hire', 'hits', 'hive', 'hoax', 'hobs', 'hock', 'hoed', 'hoes', 'hogs', 'hold', 'hole', 'holy', 'home', 'hone', 'honk', 'hope', 'hops', 'horn', 'hose', 'host', 'hour', 'howl', 'hubs', 'hues', 'huge', 'hugs', 'hula', 'hulk', 'hump', 'hums', 'hung', 'hunk', 'hunt', 'hurl', 'hurt', 'husk', 'huts', 'hymn', 'hype', 'ibex', 'iced', 'ices', 'icon', 'idea', 'ides', 'idle', 'idly', 'idol', 'imps', 'inch', 'inks', 'inky', 'into', 'ions', 'iota', 'irks', 'iron', 'isle', 'itch', 'item', 'jabs', 'jack', 'jade', 'jail', 'jamb', 'jams', 'jars', 'jaws', 'jays', 'jerk', 'jest', 'jets', 'jibe', 'jibs', 'jilt', 'jinx', 'jive', 'jobs', 'jock', 'jogs', 'join', 'joke', 'jolt', 'jots', 'jowl', 'joys', 'judo', 'jugs', 'jump', 'junk', 'jury', 'just', 'jute', 'juts', 'kegs', 'kelp', 'kept', 'keys', 'kids', 'kiln', 'kilt', 'kind', 'king', 'kite', 'kits', 'knew', 'knit', 'knob', 'knot', 'know', 'labs', 'lace', 'lack', 'lacy', 'lads', 'lady', 'lags', 'laid', 'lain', 'lair', 'lake', 'lamb', 'lame', 'lamp', 'land', 'lane', 'laps', 'lard', 'lark', 'lash', 'last', 'late', 'laud', 'lawn', 'laws', 'lays', 'lazy', 'lead', 'leaf', 'leak', 'lean', 'leap', 'left', 'legs', 'leis', 'lend', 'lens', 'lest', 'lets', 'levy', 'lewd', 'liar', 'lice', 'lick', 'lids', 'lien', 'lies', 'lieu', 'life', 'lift', 'like', 'lima', 'limb', 'lime', 'limp', 'line', 'link', 'lint', 'lion', 'lips', 'lisp', 'list', 'live', 'load', 'loaf', 'loan', 'lobe', 'lobs', 'lock', 'lode', 'loft', 'loge', 'logs', 'loin', 'lone', 'long', 'lope', 'lops', 'lord', 'lore', 'lose', 'lost', 'lots', 'loud', 'love', 'lows', 'lube', 'luck', 'luge', 'lugs', 'lump', 'lung', 'lure', 'lurk', 'lush', 'lust', 'lute', 'lynx', 'lyre', 'mace', 'made', 'maid', 'mail', 'main', 'make', 'male', 'malt', 'mane', 'many', 'maps', 'mare', 'mark', 'mars', 'mash', 'mask', 'mast', 'mate', 'math', 'mats', 'maul', 'maze', 'mead', 'meal', 'mean', 'meat', 'meld', 'melt', 'mend', 'menu', 'mesa', 'mesh', 'mice', 'mild', 'mile', 'milk', 'mind', 'mine', 'mink', 'mint', 'minx', 'mire', 'mist', 'mite', 'moan', 'moat', 'mobs', 'mock', 'mode', 'mold', 'mole', 'molt', 'monk', 'mope', 'mops', 'more', 'morn', 'most', 'mote', 'moth', 'move', 'mown', 'mows', 'much', 'muck', 'mugs', 'mule', 'muse', 'mush', 'musk', 'must', 'mute', 'myna', 'myth', 'nabs', 'nags', 'nail', 'name', 'nape', 'naps', 'nary', 'nave', 'navy', 'nays', 'nazi', 'near', 'neat', 'neck', 'nerd', 'nest', 'nets', 'news', 'newt', 'next', 'nice', 'nick', 'nigh', 'nips', 'node', 'nods', 'nope', 'norm', 'nose', 'nosy', 'note', 'nubs', 'nude', 'nuke', 'numb', 'nuts', 'oafs', 'oaks', 'oars', 'oath', 'oats', 'obey', 'odes', 'ogle', 'ogre', 'oils', 'oily', 'oink', 'okay', 'okra', 'omen', 'omit', 'once', 'ones', 'only', 'onus', 'onyx', 'opal', 'open', 'opts', 'opus', 'oral', 'orbs', 'orca', 'ores', 'orgy', 'ours', 'oust', 'outs', 'oval', 'oven', 'over', 'owed', 'owes', 'owls', 'owns', 'oxen', 'pace', 'pack', 'pact', 'pads', 'page', 'paid', 'pail', 'pain', 'pair', 'pale', 'palm', 'pals', 'pane', 'pang', 'pans', 'pant', 'pare', 'park', 'part', 'past', 'path', 'pats', 'pave', 'pawn', 'paws', 'pays', 'peak', 'peal', 'pear', 'peas', 'peat', 'peck', 'pegs', 'pelt', 'pens', 'pent', 'peon', 'perk', 'perm', 'pert', 'pest', 'pets', 'pews', 'pick', 'pied', 'pies', 'pigs', 'pike', 'pile', 'pine', 'pink', 'pins', 'pint', 'pita', 'pith', 'pits', 'pity', 'pixy', 'plan', 'play', 'plea', 'pled', 'plod', 'plot', 'plow', 'ploy', 'plug', 'plum', 'plus', 'pods', 'poem', 'poet', 'poke', 'pole', 'pond', 'pony', 'pore', 'pork', 'port', 'pose', 'posh', 'post', 'posy', 'pots', 'pour', 'pout', 'pray', 'prey', 'prig', 'prim', 'prod', 'prom', 'pros', 'prow', 'pubs', 'puck', 'pugs', 'puma', 'punk', 'puns', 'punt', 'puny', 'pure', 'push', 'puts', 'pyre', 'quay', 'quip', 'quit', 'quiz', 'race', 'rack', 'racy', 'raft', 'rage', 'rags', 'raid', 'rail', 'rain', 'rake', 'ramp', 'rams', 'rang', 'rank', 'rant', 'rape', 'raps', 'rapt', 'rash', 'rate', 'rave', 'rays', 'raze', 'read', 'real', 'ream', 'reap', 'reds', 'rein', 'rely', 'rend', 'rent', 'rest', 'ribs', 'rice', 'ride', 'rids', 'rife', 'rift', 'rigs', 'rile', 'rims', 'rind', 'ring', 'rink', 'riot', 'ripe', 'rips', 'rise', 'risk', 'rite', 'road', 'roam', 'robe', 'robs', 'rock', 'rode', 'rods', 'roes', 'role', 'romp', 'rope', 'rose', 'rosy', 'rote', 'rots', 'rout', 'rove', 'rows', 'rube', 'rubs', 'ruby', 'rude', 'rued', 'rues', 'rugs', 'ruin', 'rule', 'rump', 'rums', 'rung', 'runs', 'runt', 'ruse', 'rush', 'rust', 'ruts', 'ryes', 'sack', 'safe', 'sage', 'said', 'sail', 'sake', 'sale', 'salt', 'same', 'sand', 'sane', 'sang', 'sank', 'sate', 'save', 'scab', 'scam', 'scan', 'scar', 'scat', 'scow', 'scum', 'seal', 'seam', 'sear', 'seat', 'sect', 'self', 'send', 'serf', 'sewn', 'sexy', 'shag', 'sham', 'shed', 'shin', 'ship', 'shit', 'shiv', 'shod', 'shoe', 'shop', 'shot', 'show', 'shun', 'shut', 'sick', 'side', 'sift', 'sigh', 'sign', 'silk', 'silo', 'silt', 'sing', 'sink', 'sire', 'site', 'size', 'skew', 'skid', 'skim', 'skin', 'skip', 'skit', 'slab', 'slam', 'slap', 'slat', 'slaw', 'slay', 'sled', 'slew', 'slid', 'slim', 'slip', 'slit', 'slob', 'slog', 'slop', 'slot', 'slow', 'slug', 'slum', 'slur', 'slut', 'smog', 'smug', 'smut', 'snag', 'snap', 'snip', 'snob', 'snot', 'snow', 'snub', 'snug', 'soak', 'soap', 'soar', 'sock', 'soda', 'sofa', 'soft', 'soil', 'sold', 'sole', 'some', 'song', 'sore', 'sort', 'soul', 'soup', 'sour', 'sown', 'span', 'spar', 'spat', 'spay', 'sped', 'spew', 'spin', 'spit', 'spot', 'spry', 'spun', 'spur', 'stab', 'stag', 'star', 'stay', 'stem', 'step', 'stew', 'stir', 'stop', 'stow', 'stub', 'stud', 'stun', 'such', 'suck', 'sued', 'suet', 'suit', 'sulk', 'sumo', 'sump', 'sung', 'sunk', 'sure', 'surf', 'swab', 'swag', 'swam', 'swan', 'swap', 'swat', 'sway', 'swig', 'swim', 'swum', 'tabs', 'tack', 'taco', 'tags', 'tail', 'take', 'tale', 'talk', 'tame', 'tamp', 'tang', 'tank', 'tans', 'tape', 'taps', 'tare', 'tarp', 'tars', 'task', 'taxi', 'teak', 'teal', 'team', 'tear', 'teas', 'tend', 'tens', 'term', 'tern', 'than', 'thaw', 'them', 'then', 'they', 'thin', 'this', 'thud', 'thug', 'tick', 'tics', 'tide', 'tidy', 'tied', 'tier', 'ties', 'tile', 'time', 'tine', 'tins', 'tiny', 'tips', 'tire', 'toad', 'toed', 'toes', 'tofu', 'toga', 'togs', 'toil', 'told', 'tomb', 'tome', 'tone', 'tons', 'tony', 'tops', 'tore', 'torn', 'tour', 'town', 'tows', 'toys', 'tram', 'trap', 'tray', 'trek', 'trim', 'trio', 'trip', 'true', 'tsar', 'tuba', 'tube', 'tubs', 'tuck', 'tugs', 'tuna', 'tune', 'turf', 'turn', 'tusk', 'twig', 'twin', 'tyke', 'type', 'typo', 'tzar', 'ugly', 'undo', 'unit', 'unto', 'upon', 'urge', 'urns', 'used', 'user', 'vain', 'vane', 'vans', 'vary', 'vase', 'vast', 'vats', 'veal', 'veil', 'vein', 'vent', 'verb', 'very', 'vest', 'veto', 'vets', 'vial', 'vibe', 'vice', 'vied', 'vies', 'view', 'vile', 'vine', 'visa', 'vise', 'void', 'volt', 'vote', 'vows', 'wade', 'wads', 'waft', 'wage', 'wags', 'waif', 'wail', 'wait', 'wake', 'walk', 'wand', 'wane', 'want', 'ward', 'ware', 'warm', 'warn', 'warp', 'wars', 'wart', 'wary', 'wash', 'wasp', 'wave', 'wavy', 'waxy', 'ways', 'weak', 'wean', 'wear', 'webs', 'weds', 'weld', 'welt', 'wend', 'went', 'wept', 'west', 'wets', 'what', 'when', 'whet', 'whey', 'whim', 'whip', 'whit', 'whom', 'wick', 'wide', 'wife', 'wigs', 'wild', 'wilt', 'wily', 'wimp', 'wind', 'wine', 'wing', 'wink', 'wino', 'wins', 'wipe', 'wire', 'wiry', 'wise', 'wish', 'wisp', 'with', 'wits', 'woes', 'woke', 'woks', 'wolf', 'womb', 'wont', 'word', 'wore', 'work', 'worm', 'worn', 'wrap', 'wren', 'writ', 'yaks', 'yams', 'yank', 'yard', 'yarn', 'yawn', 'year', 'yeas', 'yelp', 'yeti', 'yoga', 'yogi', 'yoke', 'yolk', 'yore', 'your', 'yule', 'yurt', 'zany', 'zaps', 'zeal', 'zero', 'zest', 'zinc', 'zips');


decision_screen ();

sub decision_screen
      {print "\n\n++++++++++++++++++++++++++++++++++++++++++++\n\n
Three- or four-letter game?
Three-letter is faster. Four-letter is more challenging.
Enter 3 or 4.";
       chomp (my $action = <STDIN>);
       if ($action == 3)
	        {$version = "three";
	         }
       elsif ($action == 4)
	        {$version = "four";
	         }
       else {decision_screen ();}
      }




# Computer chooses secret word from list
if ($version eq "three")
	{$secret = $secretlist_three[int(rand(489))];
         }


elsif ($version eq "four")
	{$secret = $secretlist_four[int(rand(1626))];
         }




welcome_screen ();

sub welcome_screen
	{print "\n\n\n++++++++++++++++++++++++++++++++++++++++++++\n\n++++++++++++++++++++++++++++++++++++++++++++\n\n\n\nJotto\n\n
Hit enter to play.\n
(Anytime, type \"helpme\" for rules or \"quitgame\" to exit.)\n";
        my $action = <STDIN>;
# action: player's input for help, quit, enter, or back.
        if ($action eq "\n")
                {print "\n++++++++++++++++++++++++++++++++++++++++++++\n\n\n\n\nGuess a word: ";
                new_guess ();
                 }
        elsif (lc($action) eq "helpme\n")
                 {$location = "welcome_screen";
                  help ();
                 }
        elsif (lc ($action) eq "quitgame\n")
                 {quit ();
                  }
        else {welcome_screen ();}
         }
#welcome screen may lead to help, quit, new game, or welcome screen in event of error.





sub new_guess
       {$guess = <STDIN>;
        if ($guess eq "\n")
                {error ();
	         }
	chomp ($guess);
	$guess = lc($guess);

	$location = "new_guess";


	if ($version eq "three")
	         {if ($guesslist_three =~ /\b$guess\b/i) #checks if guess is on guesslist.
		           {$count ++; #increases total of valid guesses made by one.
			    checkmate ();
			    }

		  elsif ($guess eq "helpme")
		           {help ();}

		  elsif ($guess eq "quitgame")
		           {quit ();}

		  elsif ($guess eq "history")
		           {history ();}
		  else {error ();}
		  }

	elsif ($version eq "four")
	         {if ($guesslist_four =~ /\b$guess\b/i) #checks if guess is on guesslist.
		           {$count ++; #increases total of valid guesses made by one.
			    checkmate ();
			    }

		  elsif ($guess eq "helpme")
		           {help ();}

		  elsif ($guess eq "quitgame")
		           {quit ();}

		  elsif ($guess eq "history")
		           {history ();}
		  else {error ();}
		}
      }


sub error
	 {print "Not a valid $version-letter word.\n\nGuess a word: ";
	  new_guess ();
	  }


sub checkmate
	 {if ($guess eq $secret)
	           {print "\nHistory\n\n@history\t$secret\n\n";
		    print "You win!!\nIt took you $count valid guesses.\n\n\n";
		    exit;
		    }
	  else
	           {count ();}
	  }
#checks if guess matches the secret word.



sub count
	 {if ($version eq "three") 
	          {my @guess_split = split //, $guess; #splits guess from a string into a list of individual letters

		   $score = 0;

		   if ($secret =~ /$guess_split[0]/) #looks for first letter in secret word.
		            {$score ++;} #increases total of matching letters if found.

     	           if ($secret =~ /$guess_split[1]/)
		            {$score ++;}

       	           if ($secret =~ /$guess_split[2]/)
		            {$score ++;}

       	           if ($score <= 1)
		            {print "$score of your letters is in my word.\n";}
	           else
			    {print "$score of your letters are in my word.\n";}

       	           if ($score == 3)
		            {print "You still haven't guessed my word, though.\n";}
	           push @history, "$count\t$guess\t$score\n";
		   }


	  elsif ($version eq "four")
	         {my @guess_split = split //, $guess; #splits guess from a string into a list of individual letters

		  $score = 0;

		  if ($secret =~ /$guess_split[0]/) #looks for first letter in secret word.
		           {$score ++;} #increases total of matching letters if found.

		  if ($secret =~ /$guess_split[1]/)
		           {$score ++;}

		  if ($secret =~ /$guess_split[2]/)
		           {$score ++;}

		  if ($secret =~ /$guess_split[3]/)
		           {$score ++;}

		  if ($score <= 1)
		           {print "$score of your letters is in my word.\n";}
		  else
		           {print "$score of your letters are in my word.\n";}

		  if ($score == 4)
		           {print "You still haven't guessed my word, though.\n";}
		  push @history, "$count\t$guess\t$score\n";
		  }

      prompt ();
	}




sub prompt
      {print "\n(You can type \"history\" to see your history.)\nGuess another word: ";
       new_guess ();
       }




######## side menus ##########


sub quit
	{print "\nGoodbye.\n";
	 exit;
         }



sub history
	{$location = "history";
	 print "\n++++++++++++++++++++++++++++++++++++++++++++\n\n";
	 print "\nHistory\n\n@history\n\nType \"back\" to go back.\n";
	 chomp (my $action = <STDIN>);
	 $action = lc($action);
	 if ($action eq "back")
	          {print "\n\nGuess another word: ";
		   new_guess ();
		   }
	 elsif ($action eq "helpme")
	          {help ();}
	 elsif ($action eq "quitgame")
	          {quit ();}
	 else {history ();}
         }
#prints list of previous guesses and number of matching letters.



sub help
        {print "\n\n++++++++++++++++++++++++++++++++++++++++++++\n\n\nTry to guess the secret $version-letter word!\n";
	 if ($version eq "three")
	          {print "The secret word has no repeated letters, unlike \"eve\" and \"all\".
Guess $version-letter words only. No proper names, slang, or abbreviations allowed.\n
I will tell you how many letters from your word appear in the secret word.\n
Example:
You guessed \"too\". [Let's say the secret word is \"dog\".]
2 of your letters are in my word.\n\n
Type \"back\" to go back.\n";
		  }

	 if ($version eq "four")
	          {print "The secret word has no repeated letters, unlike \"ever\" and \"ally\".
Guess $version-letter words only. No proper names, slang, or abbreviations allowed.\n
I will tell you how many letters from your word appear in the secret word.\n
Example:
You guessed \"noon\". [Let's say the secret word is \"boat\".]
2 of your letters are in my word.\n\n
Type \"back\" to go back.\n";
		   }

	 chomp (my $action = <STDIN>);
	 $action = lc($action);

	 if ($action eq "back") #decides where to go back.
	          {if ($location eq "welcome_screen")
		            {print "\n\n\n\n";
			     welcome_screen ();
			     }
		   elsif ($location eq "new_guess")
		            {print "\n++++++++++++++++++++++++++++++++++++++++++++\n\n\n\n
Guess a word: ";
			     new_guess ();
			     }
		   elsif ($location eq "history")
		            {history ();}
		   }

	 if ($action eq "quitgame")
	          {quit ();}

	 else {help ();}
        }
#help screen returns user to previous page, via location variable.


#####################################


# To do:
# play in race against computer, which tries to guess your word
# before you guess secret word.

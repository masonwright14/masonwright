#!/usr/bin/perl -w

use strict;

my @word_list = ('abase', 'abate', 'abdicate', 'abduct', 'aberration', 'abet', 'abhor', 'abide', 'abject', 'abjure', 'abnegation', 'abort', 'abridge', 'abrogate', 'abscond', 'absolution', 'abstain', 'abstruse', 'accede', 'accentuate', 'accessible', 'acclaim', 'accolade', 'accommodating', 'accord', 'accost', 'accretion', 'acerbic', 'acquiescence', 'acrimony', 'acumen', 'acute', 'adamant', 'adept', 'adhere', 'admonish', 'adorn', 'adroit', 'adulation', 'adumbrate', 'adverse', 'advocate', 'aerial', 'aesthetic', 'affable', 'affinity', 'affluent', 'affront', 'aggrandize', 'aggregate', 'aggrieved', 'agile', 'agnostic', 'agriculture', 'aisle', 'alacrity', 'alias', 'allay', 'allege', 'alleviate', 'allocate', 'aloof', 'altercation', 'amalgamate', 'ambiguous', 'ambivalent', 'ameliorate', 'amenable', 'amenity', 'amiable', 'amicable', 'amorous', 'amorphous', 'anachronistic', 'analgesic', 'analogous', 'anarchist', 'anathema', 'anecdote', 'anesthesia', 'anguish', 'animated', 'annex', 'annul', 'anomaly', 'anonymous', 'antagonism', 'antecedent', 'antediluvian', 'anthology', 'antipathy', 'antiquated', 'antiseptic', 'antithesis', 'anxiety', 'apathetic', 'apocryphal', 'appalling', 'appease', 'appraise', 'apprehend', 'approbation', 'appropriate', 'aquatic', 'arable', 'arbiter', 'arbitrary', 'arbitration', 'arboreal', 'arcane', 'archaic', 'archetypal', 'ardor', 'arid', 'arrogate', 'artifact', 'artisan', 'ascertain', 'ascetic', 'ascribe', 'aspersion', 'aspire', 'assail', 'assess', 'assiduous', 'assuage', 'astute', 'asylum', 'atone', 'atrophy', 'attain', 'attribute', 'atypical', 'audacious', 'audible', 'augment', 'auspicious', 'austere', 'avarice', 'avenge', 'aversion', 'balk', 'ballad', 'banal', 'bane', 'bard', 'bashful', 'battery', 'beguile', 'behemoth', 'benevolent', 'benign', 'bequeath', 'berate', 'bereft', 'beseech', 'bias', 'bilk', 'blandish', 'blemish', 'blight', 'boisterous', 'bombastic', 'boon', 'bourgeois', 'brazen', 'brusque', 'buffet', 'burnish', 'buttress', 'cacophony', 'cadence', 'cajole', 'calamity', 'calibrate', 'calumny', 'camaraderie', 'candor', 'canny', 'canvas', 'capacious', 'capitulate', 'capricious', 'captivate', 'carouse', 'carp', 'catalog', 'catalyze', 'caucus', 'caustic', 'cavort', 'censure', 'cerebral', 'chaos', 'chastise', 'cherish', 'chide', 'choreography', 'chronicle', 'chronological', 'circuitous', 'circumlocution', 'circumscribed', 'circumspect', 'circumvent', 'clairvoyant', 'clamor', 'clandestine', 'cleave', 'clemency', 'clergy', 'cloying', 'coagulate', 'coalesce', 'cobbler', 'coerce', 'cogent', 'cognizant', 'coherent', 'collateral', 'colloquial', 'collusion', 'colossus', 'combustion', 'commendation', 'commensurate', 'commodious', 'compelling', 'compensate', 'complacency', 'complement', 'complaint', 'complicit', 'compliment', 'compound', 'comprehensive', 'compress', 'compunction', 'concede', 'conciliatory', 'concise', 'concoct', 'concomitant', 'concord', 'condolence', 'condone', 'conduit', 'confection', 'confidant', 'conflagration', 'confluence', 'conformist', 'confound', 'congeal', 'congenial', 'congregation', 'congruity', 'connive', 'consecrate', 'consensus', 'consign', 'consolation', 'consonant', 'constituent', 'constrain', 'construe', 'consummate', 'consumption', 'contemporaneous', 'contentious', 'contravene', 'contrite', 'confusion', 'conundrum', 'convene', 'convention', 'convivial', 'convoluted', 'copious', 'cordial', 'coronation', 'corpulence', 'corroborate', 'corrosive', 'cosmopolitan', 'counteract', 'coup', 'covet', 'covert', 'credulity', 'crescendo', 'criteria', 'culmination', 'culpable', 'cultivate', 'cumulative', 'cunning', 'cupidity', 'cursory', 'curtail', 'daunting', 'dearth', 'debacle', 'debase', 'debauch', 'debunk', 'decorous', 'decry', 'deface', 'defamatory', 'defer', 'deferential', 'defile', 'deft', 'defunct', 'delegate', 'deleterious', 'deliberate', 'delineate', 'demagogue', 'demarcation', 'demean', 'demure', 'denigrate', 'denounce', 'deplore', 'depravity', 'deprecate', 'derelict', 'deride', 'derivative', 'desecrate', 'desiccated', 'desolate', 'despondent', 'despot', 'destitute', 'deter', 'devious', 'dialect', 'diaphanous', 'didactic', 'diffident', 'diffuse', 'dilatory', 'diligent', 'diminutive', 'dirge', 'disaffected', 'disavow', 'discern', 'disclose', 'discordant', 'discrepancy', 'discretion', 'discursive', 'disdain', 'disgruntled', 'disheartened', 'disparage', 'disparate', 'dispatch', 'dispel', 'disperse', 'disrepute', 'dissemble', 'disseminate', 'dissent', 'dissipate', 'dissonance', 'dissuade', 'distend', 'dither', 'divine', 'divisive', 'docile', 'dogmatic', 'dormant', 'dour', 'dubious', 'duplicity', 'duress', 'dynamic', 'ebullient', 'eclectic', 'ecstatic', 'edict', 'efface', 'effervescent', 'efficacious', 'effrontery', 'effulgent', 'egregious', 'elaborate', 'elated', 'elegy', 'elicit', 'eloquent', 'elucidate', 'elude', 'emaciated', 'embellish', 'embezzle', 'emend', 'eminent', 'emollient', 'emote', 'empathy', 'empirical', 'emulate', 'enamor', 'encore', 'encumber', 'enervate', 'enfranchise', 'engender', 'enigmatic', 'enmity', 'ennui', 'entail', 'enthrall', 'ephemeral', 'epistolary', 'epitome', 'equanimity', 'equivocal', 'erudite', 'eschew', 'esoteric', 'espouse', 'ethereal', 'etymology', 'euphoric', 'evanescent', 'evince', 'exacerbate', 'exalt', 'exasperate', 'excavate', 'exculpate', 'excursion', 'execrable', 'exhort', 'exigent', 'exonerate', 'exorbitant', 'expedient', 'expiate', 'expunge', 'expurgate', 'extant', 'extol', 'extraneous', 'extricate', 'exult', 'fabricate', 'façade', 'facile', 'fallacious', 'fastidious', 'fathom', 'fatuous', 'fecund', 'felicitous', 'feral', 'fervent', 'fetid', 'fetter', 'fickle', 'fidelity', 'figurative', 'flabbergasted', 'flaccid', 'flagrant', 'florid', 'flout', 'foil', 'forage', 'forbearance', 'forestall', 'forlorn', 'forsake', 'fortitude', 'fortuitous', 'forum', 'foster', 'fractious', 'fraught', 'frenetic', 'frivolous', 'frugal', 'furtive', 'garish', 'garrulous', 'genial', 'gluttony', 'goad', 'gourmand', 'grandiloquence', 'grandiose', 'gratuitous', 'gregarious', 'grievous', 'guile', 'hackneyed', 'hallowed', 'hapless', 'harangue', 'hardy', 'harrowing', 'haughty', 'hedonist', 'hegemony', 'heinous', 'heterogeneous', 'hiatus', 'hierarchy', 'hypocrisy', 'hypothetical', 'iconoclast', 'idiosyncratic', 'idolatrous', 'ignominious', 'illicit', 'immerse', 'immutable', 'impassive', 'impeccable', 'impecunious', 'imperative', 'imperious', 'impertinent', 'impervious', 'impetuous', 'impinge', 'implacable', 'implement', 'implicate', 'implicit', 'impregnable', 'impudent', 'impute', 'inane', 'inarticulate', 'incarnate', 'incendiary', 'incessant', 'inchoate', 'incisive', 'inclination', 'incontrovertible', 'incorrigible', 'increment', 'incumbent', 'indefatigable', 'indigenous', 'indigent', 'indignation', 'indolent', 'indomitable', 'induce', 'ineffable', 'inept', 'inexorable', 'inextricable', 'infamy', 'infusion', 'ingenious', 'ingenuous', 'inhibit', 'inimical', 'iniquity', 'injunction', 'innate', 'innocuous', 'innovate', 'innuendo', 'inoculate', 'inquisitor', 'insatiable', 'insidious', 'insinuate', 'insipid', 'insolent', 'instigate', 'insular', 'insurgent', 'integral', 'interject', 'interlocutor', 'interminable', 'intimation', 'intractable', 'intransigent', 'intrepid', 'inundate', 'inure', 'invective', 'inviolable', 'irascible', 'iridescent', 'irreverence', 'irrevocable', 'jubilant', 'judicious', 'juxtaposition', 'knell', 'kudos', 'laceration', 'laconic', 'languid', 'larceny', 'largess', 'latent', 'laudatory', 'lavish', 'legerdemain', 'lenient', 'lethargic', 'liability', 'libertarian', 'licentious', 'limpid', 'linchpin', 'lithe', 'litigant', 'lucid', 'luminous', 'lurid', 'maelstrom', 'magnanimous', 'malediction', 'malevolent', 'malleable', 'mandate', 'manifest', 'manifold', 'maudlin', 'maverick', 'mawkish', 'maxim', 'meager', 'medley', 'mendacious', 'mercurial', 'meritorious', 'metamorphosis', 'meticulous', 'mitigate', 'moderate', 'modulate', 'mollify', 'morass', 'mores', 'morose', 'multifarious', 'mundane', 'munificence', 'mutable', 'myriad', 'nadir', 'nascent', 'nebulous', 'nefarious', 'negligent', 'neophyte', 'nocturnal', 'noisome', 'nomadic', 'nominal', 'nonchalant', 'nondescript', 'notorious', 'novice', 'noxious', 'nuance', 'nurture', 'obdurate', 'obfuscate', 'oblique', 'oblivious', 'obscure', 'obsequious', 'obsolete', 'obstinate', 'obstreperous', 'obtuse', 'odious', 'officious', 'ominous', 'onerous', 'opulent', 'oration', 'ornate', 'orthodox', 'oscillate', 'ostensible', 'ostentatious', 'ostracism', 'pacific', 'palatable', 'palette', 'pallid', 'panacea', 'paradigm', 'paradox', 'paragon', 'paramount', 'pariah', 'parody', 'parsimony', 'partisan', 'patent', 'pathology', 'pathos', 'pernicious', 'perplex', 'perspicacity', 'pert', 'pertinacious', 'perusal', 'pervasive', 'petulance', 'philanthropic', 'phlegmatic', 'pillage', 'pinnacle', 'pithy', 'pittance', 'placate', 'placid', 'platitude', 'plaudits', 'plausible', 'plenitude', 'pliable', 'poignant', 'polemic', 'portent', 'potable', 'potentate', 'pragmatic', 'precipice', 'preclude', 'precocious', 'predilection', 'preponderance', 'prepossessing', 'presage', 'prescient', 'prescribe', 'presumptuous', 'pretense', 'primeval', 'privation', 'probity', 'proclivity', 'procure', 'profane', 'profligate', 'profuse', 'promulgate', 'propagate', 'propensity', 'propitious', 'propriety', 'prosaic', 'protean', 'prowess', 'prudence', 'prurient', 'puerile', 'pugnacious', 'pulchritude', 'punctilious', 'pungent', 'punitive', 'putrid', 'quagmire', 'quaint', 'quandary', 'quell', 'querulous', 'quixotic', 'quotidian', 'rail', 'rancid', 'rancor', 'rapport', 'rash', 'raucous', 'raze', 'rebuke', 'recalcitrant', 'recapitulate', 'reciprocate', 'reclusive', 'reconcile', 'rectitude', 'redoubtable', 'refract', 'refurbish', 'refute', 'regurgitate', 'relegate', 'relish', 'remedial', 'remiss', 'renovate', 'renown', 'renunciation', 'repentant', 'replete', 'repose', 'reprehensible', 'reprieve', 'reproach', 'reprobate', 'reprove', 'repudiate', 'repulse', 'reputable', 'requisition', 'rescind', 'reservoir', 'resilient', 'resolute', 'resolve', 'respite', 'resplendent', 'restitution', 'restive', 'retract', 'revel', 'revere', 'revoke', 'rhapsodize', 'ribald', 'rife', 'ruminate', 'ruse', 'saccharine', 'sacrosanct', 'sagacity', 'salient', 'salutation', 'salve', 'sanctimonious', 'sanguine', 'satiate', 'scathing', 'scintillating', 'scrupulous', 'scurrilous', 'sedentary', 'semaphore', 'seminal', 'sensual', 'sensuous', 'serendipity', 'serendipity', 'serene', 'servile', 'sinuous', 'sobriety', 'solicitous', 'solipsistic', 'soluble', 'solvent', 'somnolent', 'sophomoric', 'sovereign', 'speculative', 'spurious', 'stagnate', 'staid', 'stingy', 'stoic', 'stolid', 'strenuous', 'strident', 'stupefy', 'subjugate', 'sublime', 'submissive', 'succinct', 'superfluous', 'surfeit', 'surmise', 'surreptitious', 'surrogate', 'swarthy', 'sycophant', 'tacit', 'taciturn', 'tangential', 'tantamount', 'tedious', 'temerity', 'tenable', 'tenuous', 'terrestrial', 'timorous', 'tirade', 'toady', 'toady', 'tome', 'torpid', 'torrid', 'tortuous', 'tractable', 'transgress', 'transient', 'transmute', 'travesty', 'tremulous', 'trenchant', 'trepidation', 'trite', 'truculent', 'truncate', 'turgid', 'ubiquitous', 'umbrage', 'uncanny', 'unctuous', 'undulate', 'upbraid', 'usurp', 'utilitarian', 'utopia', 'vacillate', 'vacuous', 'validate', 'vapid', 'variegated', 'vehemently', 'veneer', 'venerable', 'venerate', 'veracity', 'verbose', 'verdant', 'vestige', 'vex', 'vicarious', 'vicissitude', 'vigilant', 'vilify', 'vindicate', 'vindictive', 'virtuoso', 'viscous', 'vitriolic', 'vituperate', 'vivacious', 'vocation', 'vociferous', 'wallow', 'wane', 'wanton', 'whimsical', 'wily', 'winsome', 'wistful', 'wizened', 'wrath', 'yoke', 'zealous', 'zenith', 'zephyr');

my $secret_word = $word_list[int(rand($#word_list))];
my $guess;
my $counter = 0;
my @to_print;
my $length = length($secret_word);
my $current_index;
my @index_list;
my @errors;
my $already_guessed = " ";
my $action;


foreach (1..$length)      #sets up underscore-space for each character of the secret word
       {push @to_print, "_ ";}


welcome_screen ();

sub welcome_screen
        {print "\n\n\n++++++++++++++++++++++++++++++++++++++++++++\n\n++++++++++++++++++++++++++++++++++++++++++++\n\n\n\nHangman\n\n
Welcome to SAT word hangman!\nHit enter to play.\n
(Anytime, type \"quit\" to exit.)\n";
         $action = <STDIN>;
         # action: player's input for help or enter.
         if ($action eq "\n")
                 {print "\n++++++++++++++++++++++++++++++++++++++++++++\n\n\n\n";
                  show_gallows ();
                  }
         elsif (lc($action) eq "quit\n")
                 {quit ();}
         }


show_gallows ();


sub show_gallows
	{print "\n++++++++++++++++++++++++++++++++++++++++++++\n\n";

	 if ($counter == 0)
	         {print "\n\n____________\n|          |\n|\n|\n|\n|\n|\n";}
	 if ($counter == 1)
	         {print "____________\n|          |\n|          O\n|\n|\n|\n|\n";}
	 if ($counter == 2)
	         {print "____________\n|          |\n|          O\n|          |\n|\n|\n|\n";}
	 if ($counter == 3)
	         {print "____________\n|          |\n|          O\n|        --|\n|\n|\n|\n";}
	 if ($counter == 4)
	         {print "____________\n|          |\n|          O\n|        --|--\n|\n|\n|\n";}
	 if ($counter == 5)
	         {print "____________\n|          |\n|          O\n|        --|--\n|         /\n|\n|\n";}
	 if ($counter ==6)
	         {print "____________\n|          |\n|          O\n|        --|--\n|         / \\\n|\n|\n";
		  sleep 1;
		  print "#########################\n";
		  print "____________\n|          |\n|          |\n|          @\n|         /|\\\n|          |\n|\n
YOU ARE DEAD.\nWord was: $secret_word\n";
		  exit;
		  }
          print "\n",@to_print,"\n";
	  print "@errors\n";
	  guess ();
         }


sub check_win
	{my $dash_counter = 0;
	 foreach my $element (@to_print) 
	         {if ($element =~ /_/)
		           {$dash_counter = 1;}
	         }
	 
	 if ($dash_counter == 1)
		 {show_gallows;}
	 elsif ($dash_counter == 0)
	         {victory ();}
          }


sub victory
	 {print "\n\n____________\n|          |\n|\n|\n|\tYOU       O\n|\t        --|--\n|\tWIN!     / \\\n";
	  print "_______________________________";
	  print "\n",@to_print,"\n";
	  print "@errors\n\n";
	  exit;
	  }


sub guess
	 {print "\nGuess a letter: ";
	  chomp ($guess = <STDIN>);
	  $guess = lc($guess);
	  
	  if ($guess eq "quit")
	          {quit ();}
	  
	  unless ($guess =~ /^[a-z]$/)
	          {print "Invalid letter. Try again: ";
		   guess ();
		  }
	  
	  if ($guess =~ /[$already_guessed]/)
	          {print "Already guessed. Try again: ";
		   guess ();
		  }
	  
	  $already_guessed = "$already_guessed"."$guess";
	  evaluate ();
	  }



sub evaluate
	 {if ($guess =~ /[$secret_word]/)
	         {update ();}
	  else 
	         {countdown ();}
	 }


####################

sub update
	 {$current_index = index ("$secret_word", "$guess", 0);
	  make_list ();
	  }


sub make_list
         {if ($current_index >= 0)     #if the letter was found . . .
	        {push @index_list, $current_index;  # add the index to a list of indexes for that letter
	  	 $current_index = index("$secret_word", "$guess", ($current_index + 1)); # then look for it farther along in the word
	 	 make_list ();
 	         } # cycle repeats as long as the letter is found.
	  else {print_out ();}
	 } # when letter no longer found, move on.


sub print_out
	 {foreach my $element (@index_list)
	        {$to_print[$element] = $guess;} # changes some underscores to the guessed letter 
	  @index_list = (); # resets the list of indexes to empty set, so you don't change the letters you've already entered, to whatever is successfully guessed next

	  check_win (); # let user guess again.
	 }


######################

sub countdown
        {push @errors, $guess;
	 $counter ++;
	 show_gallows ();
        }

sub quit
        {print "\nGoodbye.\n";
	 exit;
        }

#########################################
#
# TO DO:
#
# -comment, clean up.

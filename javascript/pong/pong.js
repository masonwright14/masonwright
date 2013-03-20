var canvas, context, hitSound, wallSound, missSound; // DOM references
var playGame; // boolean -- tells if game has started to adjust animation output
var arrowUp, arrowDown, enter, upS, downZ; // keycode numbers
var player, opponent, ball, chooser; // object references
var playTo; // highest score to play up to (11 currently)
var pressedAlready, enterPressedAlready; // boolean for whether an arrow / enter key was already held down
var mode; // String that tells which screen or game mode is running
          // "firstWelcome", "onePlayerWelcome", "onePlayerIntro", "onePlayer", "twoPlayerIntro", 
          // "twoPlayer", "demo"
var blinkCounter, demoCounter; // counts up during animation loops to make letters blink on the screen, to call up demo
var demoCounterMax; // cutoff for calling up the demo. 5000 msec at first, but 10,000 after first page load
var timer; // the animation loop timer, used to end the animation loop with clearTimeout
var level; // 0 for "novice" option, 1 for "amateur", 2 for "professional"

// to do:
// make new ball, chooser each game instead of hanging on to old ones (more logical, simpler)
// figure out how to join playTo var to a string when making instruction screens
// consider re-coding welcome screens with WelcomeScreen as a class, animateWelcome as a prototype method


///// SETUP FUNCTIONS /////

window.onload = function()
{
    initialize(); // this runs just once
    welcomeScreen();
}

function initialize()
{
    canvas = document.getElementById("theCanvas"); // convenience vars
    context = canvas.getContext("2d");

    hitSound = document.getElementById("hitSound");
    wallSound = document.getElementById("wallSound");
    missSound = document.getElementById("missSound");

    chooser = new Chooser(); // circle that indicates selection in menus
    player = new Paddle(100, 210, 5);
    opponent = new Paddle(540, 210, 20);
    ball = new Ball();

    arrowUp = 38; // keycodes
    arrowDown = 40;
    enter = 13;
    upS = 83;
    downZ = 90;

    playTo = 11; // max score in the game
    blinkCounter = 0; // for blinking text
    demoCounterMax = 5000 / 30; // 5 seconds until demo mode is called on inactivity (at first page load)
    enterPressedAlready = false; // var to prevent double-pressing of enter in menus
}

function demoWiring()
{
    $(document).unbind();

    $(document).bind("keydown", function(e)
	{
	    if(e.keyCode == enter)
	    {
		enterPressedAlready = true; // prevent fall-through

		clearTimeout(timer);
		welcomeScreen();
	    }
	});

    $(document).bind("keyup", function(e)
	{
	    if(e.keyCode == enter)
	    {enterPressedAlready = false;}
	});
}


///// WELCOME SCREENS /////

function welcomeScreen()
{
    mode = "firstWelcome";
    chooser.max = 2; // max index of menu
    chooser.index = 0; // start at the first menu item
    pressedAlready = false;
    demoCounter = 0; // reset the counter to call up demo mode on inactivity

    player.score = opponent.score = 0; // reset scores

    welcomeWiring();
    canvas.focus(); // sets the focus to the canvas, so you don't have to click it before detecting key events
                    // must be done after the wiring to work
    animateWelcome();
}

function welcomeWiring() // handles input for welcome, decision, demo screens
{
    $(document).unbind();

    $(document).bind("keydown", function(e)
	{
	    if(mode == "firstWelcome")
		demoCounter = 0; // reset counter to call demo mode any time a key is pressed
	    if(e.keyCode == arrowUp)
		chooser.moveUp = true;
	    else if(e.keyCode == arrowDown)
		chooser.moveDown = true;
	    else if(e.keyCode == enter && !enterPressedAlready)
	    {
		if(mode == "firstWelcome")
		{
		    enterPressedAlready = true; // prevent fall-through to other modes

		    if(chooser.index == 0) // if selection is one-player
		    {
			mode = "onePlayerWelcome";
		    }
		    else if(chooser.index == 1) // if selection is two-player
		    {
			mode = "twoPlayerIntro";
		    }
		    else if(chooser.index == 2) // if selection is demo
		    {
			blinkCounter = 0;
			clearTimeout(timer);
			mode = "demo";
			demoWindow();
		    }
		}
		else if(mode == "onePlayerWelcome" && !enterPressedAlready) // if to start one-player intro
		{
		    enterPressedAlready = true; // prevent fall-through
		    
		    level = chooser.index; // select level of difficulty
		    mode = "onePlayerIntro";
		}
		else if(mode =="onePlayerIntro" && !enterPressedAlready) // if to start one-player game
		{
		    enterPressedAlready = true;

		    clearTimeout(timer);
		    mode = "onePlayer";
		    gameWindow();
		}
		else if(mode =="twoPlayerIntro" && !enterPressedAlready) // if to start two-player game
		{
		    enterPressedAlready = true;

		    clearTimeout(timer);
		    mode = "twoPlayer";
		    gameWindow();
		}
		else if(mode == "demo" && !enterPressedAlready) // if to end demo mode
		{
		    enterPressedAlready = true; // prevent fall-through

		    clearTimeout(timer);
		    welcomeScreen();
		}
	    }
	});

    $(document).bind("keyup", function(e)
	{
	    if(e.keyCode == arrowUp)
	    {
		pressedAlready = false;
		chooser.moveUp = false;
	    }
	    else if(e.keyCode == arrowDown)
	    {
		pressedAlready = false;
		chooser.moveDown = false;
	    }
	    else if(e.keyCode == enter)
	    {
		enterPressedAlready = false;
	    }
	});
}

function animateWelcome()
{
    context.clearRect(0, 0, canvas.width, canvas.height);
    context.fillStyle = "rgb(255, 255, 255)";

    context.font = "40px sans-serif";

    if(mode == "firstWelcome")
    {
	var text = "Pong";
	context.fillText(text, 280, 100);
	text = "One player";
	context.fillText(text, 200, 180);
	text = "Two players";
	context.fillText(text, 200, 230);
	text = "Demo";
	context.fillText(text, 200, 280);
    }
    else if(mode == "onePlayerWelcome")
    {
	var text = "Pong";
	context.fillText(text, 280, 100);
	text = "Novice";
	context.fillText(text, 200, 180);
	text = "Amateur";
	context.fillText(text, 200, 230);
	text = "Professional";
	context.fillText(text, 200, 280);
    }
    else if(mode == "onePlayerIntro")
    {
	context.font = "30px sans-serif";

	text = "You are left player";
	context.fillText(text, 100, 150);
	text = "Move with up and down arrow keys";
	context.fillText(text, 100, 200);
	text = ("Play first to 11");
	context.fillText(text, 100, 250);
    }
    else if(mode == "twoPlayerIntro")
    {
	context.font = "30px sans-serif";

	text = "Left player: Use 's' and 'z'";
	context.fillText(text, 100, 150);
	text = "Right player: Use 'up' and 'down'";
	context.fillText(text, 100, 200);
	text = ("Play first to 11");
	context.fillText(text, 100, 250);
    }

    blinker();

    if(chooser.moveUp == true && pressedAlready == false && chooser.index != 0)
    {
	chooser.index--;
	pressedAlready = true;
    }
    else if(chooser.moveDown == true && pressedAlready == false && chooser.index != chooser.max)
    {
	chooser.index++;
	pressedAlready = true;
    }

    var y = 180 - chooser.radius + chooser.index * 50;

    if(mode == "firstWelcome" || mode == "onePlayerWelcome")
    {
	context.beginPath();
	context.arc(160, y, chooser.radius, 0, Math.PI * 2, false);
	context.closePath();
	context.fill();    
    }

    timer = setTimeout("animateWelcome()", 33);

    if(mode == "firstWelcome")
    {
	demoCounter++;
	checkDemoCounter();  // test if enough inactive time has passed to run the demo
    }
}

function checkDemoCounter()
{
    if(demoCounter >= demoCounterMax)
    {
	demoCounterMax = 10000 / 30; // after first page load, increase time to load demo to 10s
	blinkCounter = 0;
	clearTimeout(timer);
	mode = "demo";
	demoWindow();
    }
}

///// GAME SETUP /////

function gameWindow() // controls setup for playing Pong
{
    if(mode == "onePlayer")
	onePlayerWiring();
    else if(mode == "twoPlayer")
	twoPlayerWiring();
    setUp();
    animateGame();
}

function demoWindow() // controls setup for demo mode 
{
    // demo mode event handling is still done within welcomeWiring
    level = 2;
    setUp();
    animateGame();
}

function setUp() // general setup for game or demo
{
    player.y = 210; // return paddles to center
    opponent.y = 210;

    ball.x = canvas.width / 2; // start ball in center
    ball.y = Math.floor (Math.random() * canvas.height); // at a random height

    if(mode == "onePlayer") // ball always starts toward single player
	ball.vX = -6;
    else // ball starts toward randomly chosen player otherwise
    {
	if(Math.random() < 0.5)
	    ball.vX = -6;
	else
	    ball.vX = 6;
    }
    ball.vY = Math.floor(Math.random() * 9) - 4;

    playGame = true; // animation will continue on setTimeout once called
}

function onePlayerWiring() // handles input for one-player game
{
    $(document).unbind();

    player.moveUp = false; // prevent paddles from moving on their own at start
    player.moveDown = false;

    $(document).bind("keydown", function(e)
	{
	    if(e.keyCode == arrowUp)
		player.moveUp = true;
	    else if(e.keyCode == arrowDown)
		player.moveDown = true;
	});

    $(document).bind("keyup", function(e)
	{
	    if(e.keyCode == arrowUp)
		player.moveUp = false;
	    else if(e.keyCode == arrowDown)
		player.moveDown = false;
	});
}

function twoPlayerWiring() // for two-player game
{
    $(document).unbind();

    opponent.moveUp = false; // prevent paddles from moving on their own at start
    opponent.moveDown = false;
    player.moveUp = false;
    player.moveDown = false;

    $(document).bind("keydown", function(e)
	{
	    if(e.keyCode == arrowUp)
		opponent.moveUp = true;
	    if(e.keyCode == arrowDown)
		opponent.moveDown = true;
	    if(e.keyCode == upS)
		player.moveUp = true;
	    if(e.keyCode == downZ)
		player.moveDown = true;
	});

    $(document).bind("keyup", function(e)
	{
	    if(e.keyCode == arrowUp)
		opponent.moveUp = false;
	    if(e.keyCode == arrowDown)
		opponent.moveDown = false;
	    if(e.keyCode == upS)
		player.moveUp = false;
	    if(e.keyCode == downZ)
		player.moveDown = false;
	});
}

function gameOver()
{
    $(document).unbind();

    playGame = false;
    animateGame(); // show final score

    context.font = "30px sans-serif";

    text = "PRESS";
    context.fillText(text, 190, 400);
    text = "ENTER";
    context.fillText(text, 350, 400);

    $(document).bind("keydown", function(e)
	{
	   if(e.keyCode == enter)
	    {
		enterPressedAlready = true; // prevent fall-through
		welcomeScreen();
	    }
	});
}

function animateGame()
{
    // redraw background
    context.clearRect(0, 0, canvas.width, canvas.height);
    dottedLine();

    context.fillStyle = "rgb(255, 255, 255)";
    showScore();

    if(mode == "demo")
    {
	blinker();
	player.planMove();
    }

    // adjust player's paddle ycor
    player.movePaddle();

    // opponent decides where to move
    if(mode != "twoPlayer")
	opponent.planMove();

    // adjust opponent's paddle ycor
    opponent.movePaddle();

    //adjust ball coordinates
    ball.x = Math.round(ball.x + ball.vX);
    ball.y += ball.vY;

    // check if ball is offscreen (someone missed it)
    if(ball.x + ball.radius < 0 || ball.x - ball.radius > canvas.width)
    {
	if(ball.x + ball.radius < 0) // if player missed the ball
	{
	    opponent.score += 1;
	    ball.vX = -6; // ball is returned to player
	}
	else // opponent missed the ball
	{
	    player.score += 1;
	    ball.vX = 6;
	}

	player.futureY = -1;
	opponent.futureY = -1;

	missSound.currentTime = 0;
	missSound.play();

	if(opponent.score == playTo || player.score == playTo)
	    gameOver();

	ball.x = canvas.width / 2;  // if game not over, start ball at center of screen, toward player who missed
	ball.y = Math.floor (Math.random() * canvas.height);
	ball.vY = Math.floor(Math.random() * 9) - 4;
	ball.pastPaddle = false;
    }

    // check ball for collision with a paddle
    if( (!ball.pastPaddle) && 
	(ball.x - ball.radius <= player.x + player.width || ball.x + ball.radius >= opponent.x)) // if ball just passed paddle
    {
	ball.pastPaddle = true;
	if(ball.x - ball.radius <= player.x + player.width) // if ball is past player's paddle
	{
	    var paddleArea // a number from 0-7; index of equal-sized region of paddle from top to bottom

	    if(ball.y + ball.radius >= player.y && ball.y - ball.radius <= player.y + player.height) // if player hit ball
	    {
		ball.vX *= -1;
		ball.x = player.x + player.width + ball.radius;

		if(Math.abs(ball.vX * 1.1) < 30) // if ball speed is not maxed out
		    ball.vX *= 1.1;

		paddleArea = Math.floor((ball.y - (player.y - ball.radius)) / ((player.height + ball.radius * 2) / 8));
		if(paddleArea <= 0)
		    ball.vY = -2 * Math.abs(ball.vX);
		else if(paddleArea == 1)
		    ball.vY = -1 * Math.abs(ball.vX);
		else if(paddleArea == 2)
		    ball.vY = -0.5 * Math.abs(ball.vX);
		else if(paddleArea == 3 || paddleArea == 4)
		    ball.vY = 0;
		else if(paddleArea == 5)
		    ball.vY = 0.5 * Math.abs(ball.vX);
		else if(paddleArea == 6)
		    ball.vY = 1 * Math.abs(ball.vX);
		else if(paddleArea >= 7)
		    ball.vY = 2 * Math.abs(ball.vX);

		hitSound.currentTime = 0;
		hitSound.play();

		opponent.futureY = -1;

		ball.pastPaddle = false;
	    }
	}
	else // else if ball is past opponent's paddle, not player's
	{
	    if(ball.y + ball.radius >= opponent.y && ball.y - ball.radius <= opponent.y + opponent.height) // if opponent hit ball
	    {
		ball.vX *= -1;
		ball.x = opponent.x - ball.radius;

		if(Math.abs(ball.vX * 1.1) < 30) // if ball speed is not maxed out
		    ball.vX *= 1.1;

		paddleArea = Math.floor((ball.y - (opponent.y - ball.radius)) / ((opponent.height + ball.radius * 2) / 8));
		if(paddleArea <= 0)
		    ball.vY = -2 * Math.abs(ball.vX);
		else if(paddleArea == 1)
		    ball.vY = -1 * Math.abs(ball.vX);
		else if(paddleArea == 2)
		    ball.vY = -0.5 * Math.abs(ball.vX);
		else if(paddleArea == 3 || paddleArea == 4)
		    ball.vY = 0;
		else if(paddleArea == 5)
		    ball.vY = 0.5 * Math.abs(ball.vX);
		else if(paddleArea == 6)
		    ball.vY = 1 * Math.abs(ball.vX);
		else if(paddleArea >= 7)
		    ball.vY = 2 * Math.abs(ball.vX);

		hitSound.currentTime = 0;
		hitSound.play();

		player.futureY = -1;

		ball.pastPaddle = false;
	    }  
	}
    }

    // check ball for collision with canvas borders
    if(ball.y - ball.radius < 0)
    {
	ball.vY *= -1;
	ball.y = ball.radius;

	wallSound.currentTime = 0;
	wallSound.play();
    }
    else if(ball.y + ball.radius > canvas.height)
    {
	ball.vY *= -1;
	ball.y = canvas.height - ball.radius;

	wallSound.currentTime = 0;
	wallSound.play();
    }

    // draw the ball if game has started
    if(playGame)
    {
	context.beginPath();
	context.arc(ball.x, ball.y, ball.radius, 0, Math.PI * 2, false);
	context.closePath();
	context.fill();
    }

    // draw the paddles
    context.fillRect(player.x, player.y, player.width, player.height);
    context.fillRect(opponent.x, opponent.y, player.width, player.height);

    // if game continues, animate again
    if(playGame)
	timer = setTimeout("animateGame()", 33);
}

///// GAME OBJECTS /////

function Paddle(x, y, speed)
{
    this.x = x;
    this.y = y;

    this.speed = speed;
    this.lastDirection = "none"; // used to check if you've changed directions and should reset speed

    this.moveUp = false;
    this.moveDown = false;

    this.futureY = -1;

    this.score = 0;
}

Paddle.prototype.height = 60;
Paddle.prototype.width = 10;
Paddle.prototype.initialSpeed = 5;
Paddle.prototype.a = 0.5;

Paddle.prototype.movePaddle = function() // adjust paddle's y coordinate
{
 if(this.moveUp)
    {
	this.y -= this.speed; // paddle accelerates as arrow key is held down

	if(this.lastDirection == "up") // but speed resets if a DIFFERENT arrow key is pressed now
	    this.speed += this.a;
	else
	    this.speed = this.initialSpeed;
	this.lastDirection = "up";

	if(this.y < 0)
	    this.y = 0;
    }
    else if(this.moveDown)
    {
	this.y += this.speed;

	if(this.lastDirection == "down")
	    this.speed += this.a;
	else
	    this.speed = this.initialSpeed;
	this.lastDirection = "down";

	if(this.y + this.height > canvas.height)
	    this.y = canvas.height - this.height;
    }
    else
    {
	this.speed = this.initialSpeed;
	this.lastDirection = "none";
    }
}

Paddle.prototype.planMove = function() // computer-controlled paddle decides which direction to travel (if any)
{
    if((ball.vX < 0 && this.x > 320) || (ball.vX > 0 && this.x < 320)) // if ball is moving toward the other player
    {
	if(level > 0) // only recover if not a novice
	{
	    if(this.y > canvas.height / 2 - this.height / 2 + 5) // recover toward the center
	    {
		this.moveUp = true;
		this.moveDown = false;
	    }
	    else if(this.y < canvas.height / 2 - this.height / 2 - 5)
	    {
		this.moveDown = true;
		this.moveUp = false;
	    }
	    else
	    {
		this.moveUp = false;
		this.moveDown = false;
	    }
	}
	else // if a novice
	{
	    this.moveUp = false;
	    this.moveDown = false;
	}
    }
    else // if the ball is moving toward own paddle
    { // follow the ball
	if(level < 2)
	{
	    if(ball.y > this.y + 0.3 * this.height && ball.y < this.y + 0.7 * this.height)
	    {
		this.moveUp = false;
		this.moveDown = false;
	    }
	    else if(ball.y < this.y + this.height / 2)
	    {
		this.moveUp = true;
		this.moveDown = false;
	    }
	    else
	    {
		this.moveDown = true;
		this.moveUp = false;
	    }
	}
	else // most difficult level tracking on ball
	{
	    if(this.x > 320) // if the right (computer) paddle
	    {
		if(this.futureY == -1 || Math.abs(ball.x - 340) <= ball.vX ||
		   Math.abs(ball.x - 500) <= ball.vX) // re-check and shift again
		    {
			this.findFutureY();
		    }
	    }
	    else if(this.x < 320) // if the left paddle
	    {
		if(this.futureY == -1 || Math.abs(ball.x - 340) <= ball.vX * -1 ||
		   Math.abs(ball.x - 140) <= ball.vX * -1) // re-check and shift again
		    {
			this.findFutureY();
		    }
	    }

	    if(Math.abs(this.futureY - (this.y + this.height / 2)) <= 2)
	    {
		this.moveUp = false;
		this.moveDown = false;
	    }
	    else if(this.futureY < this.y + this.height / 2)
	    {
		this.moveUp = true;
		this.moveDown = false;
	    }
	    else
	    {
		this.moveDown = true;
		this.moveUp = false;
	    }
	}
    }
}

Paddle.prototype.findFutureY = function() // helper method for planMove in "professional"/demo mode
{
    var dx; // dx will hold the x distance the ball has yet to travel before it could hit own paddle (self)
    if(this.x > 320) // if this is the right paddle
	dx = Math.abs(this.x - (ball.x + ball.radius));
    else // if this is the left paddle
	dx = Math.abs((ball.x - ball.radius) - this.x);
    var dy = Math.abs(dx * (ball.vY / Math.abs(ball.vX))); // dy is how far the ball will travel in the y direction
                                                           // while traveling dx in the x direction, based on slope
    var tempY = ball.y; // we don't want to change the ball's values during the computation
    var tempVY = ball.vY;
    var space; // how far the ball can travel in the y direction before hitting the top or bottom barrier of the world
    
    // this loop sets tempY to the y coordinate the ball will have when it reaches own paddle (self).
    // during each run through the loop, the ball either reaches the paddle (loop ends) or hits the floor/ceiling.
    // the loop works by subtracting the "space" value from dy until dy is all gone, resetting "space" on each collision.
    // each time dy is reduced, tempY is adjusted by the same amount either up or down, depending on
    // whether the ball is imagined to be traveling upwards or downwards.
    while(dy != 0) // if the ball hasn't reached the other paddle yet
    {
	if(tempVY < 0) // if moving upward
	    space = tempY - ball.radius; // distance to the ceiling
	else // if moving downward
	    space = canvas.height - (tempY + ball.radius); // distance to the floor
	
	if (dy <= space) // if the ball doesn't need to bounce again
	{
	    if(tempVY < 0) // if moving upward
		tempY -= dy;
	    else // if moving downward
		tempY += dy;
	    dy = 0; // end the while loop
	}
	else // if ball must bounce again
	{
	    if(tempVY < 0) // if moving upward
		tempY -= space;
	    else // if moving downward
		tempY += space;
	    dy -= space;
	    tempVY *= -1; // switch directions
	}
    }
    
    this.futureY = Math.floor(tempY);
    var shift = Math.floor(Math.random() * (this.height * 0.8) - this.height * 0.4); // shift paddle a fraction of its height
    this.futureY += shift; // strike off-center at random
}

function Ball()
{
    this.x = canvas.width / 2;
    this.y = Math.floor (Math.random() * canvas.height);

    this.radius = 4;
    
    this.vX = -6;
    this.vY = Math.floor(Math.random() * 9) - 4;

    this.pastPaddle = false;
}

function Chooser()
{
    this.index = 0;
    this.max;
    this.radius = 10;
    this.moveUp = false;
    this.moveDown = false;
}

///// ANIMATION HELPERS /////

function dottedLine()
{
    context.lineWidth = 5;
    context.strokeStyle = "rgb(255, 255, 255)";

    context.beginPath();
    context.moveTo(320, 0);

    var y = 0;
    while(y <= 480)
    {
	y += 15;
	context.lineTo(320, y);
	y += 15;
	context.moveTo(320, y);
    }
    context.closePath();
    context.stroke();
}

function showScore()
{
    context.font = "40px sans-serif";

    var text = player.score;
    context.fillText(text, 200, 80);

    text = opponent.score;
    context.fillText(text, 410, 80);
}

function blinker()
{
    if(blinkCounter % 60 < 30)
    {
	if(mode == "demo")
	{
	    context.font = "24px sans-serif";
	    
	    text = "DEMO MODE";
	    context.fillText(text, 140, 400);
	    text = "HIT ENTER";
	    context.fillText(text, 360, 400);
	}
	else if(mode == "firstWelcome" || mode == "onePlayerWelcome" || mode == "onePlayerIntro" || mode == "twoPlayerIntro")
	{
	    context.font = "40px sans-serif";
	    text = "Hit enter to begin"
	    context.fillText(text, 160, 400);
	}
    }
    
    blinkCounter++;
    if(blinkCounter >= 960)
	blinkCounter = 0;
}
<!DOCTYPE html>
<html>
    <head>
        <title> Vinyl Generator</title>
        <link rel="stylesheet" href="index.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <script src="index.js"></script>
    </head>
    <body>
        <video playsinline autoplay muted id="bgvid">
        	<source src='record3.mp4' type='video/mp4'>
        </video>
        <div id="tint">	</div>
        
		<div class="center welcome" id="welcome_text_container">
			<h1 style="width: 100%; text-align: center; margin-top: 0px; height: 100%;"> Welcome </h1>
		</div>
		<div class="center welcome" id="button_container"> 
			<button class="welcome_buttons" id="view"> <h2> View All Vinyls </h2> </button>
			<button class="welcome_buttons" id="random"> <h2> Select Random Vinyl </h2> </button>
			<button class="welcome_buttons" id="add"> <h2> Add New Vinyl </h2> </button>
		</div>
        
        <div class="center" id="vinyl">
			<p id="random_vinyl">
				Listen to: 
				<span style="display: none">
				 </span>
			</p>
        </div>
        
        <div class = "center" id = "table">
        	
        </div>   
        
        <div class="center" id="add_vinyl">
        	<div id="add_vinyl_container">
				<form id="form">
					<h2 class="form_labels"> Artist </h2>
					<input type="text" name="artist">
					<h2 class="form_labels"> Album </h2> 
					<input type="text" name="album">
					<input type="submit" name="submit" value="Add">
				</form>
			</div>
        </div>
        <script>	
    		var iterations = 0; 
    		document.getElementById("bgvid").addEventListener('ended', function () {    

				if (iterations < 3) {       

					this.currentTime = 0;
					this.play();
					iterations ++;
		
				}   
			}, false);
    	</script>
    </body>
</html>

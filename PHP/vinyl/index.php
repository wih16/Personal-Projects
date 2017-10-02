<!DOCTYPE html>
<html>
    <head>
        <title> Vinyl Generator</title>
        <style>
        	body{
        		margin: 0px;
        	}
        	#bgvid{
        		position: fixed;
				top: 50%;
				left: 50%;
				min-width: 100%;
				min-height: 100%;
				width: auto;
				height: auto;
				z-index: -100;
				-ms-transform: translateX(-50%) translateY(-50%);
				-moz-transform: translateX(-50%) translateY(-50%);
				-webkit-transform: translateX(-50%) translateY(-50%);
				transform: translateX(-50%) translateY(-50%);
				background-size: cover; 
        	}
        	div{
        		position: fixed; 
        		width:100%; 
        		height:100%; 
        		background-color: white; 
        		z-index: -99;
        		opacity: .3;
        	}
        	#p1{
        		position: fixed;
        		width: 100%; 
        		text-align:center; 
        		font-family: 'Century Gothic', CenturyGothic, AppleGothic, sans-serif; 
        		color: #000000; 
        		font-size: 20px;
        	}
        </style>
    </head>
    <body>
        <video playsinline autoplay muted loop id="bgvid">
        	<source src='record.mp4' type='video/mp4'>
        </video>
        <div>	
        </div>
        <p id="p1">
			  <?php	 
			 $link = mysqli_connect("127.0.0.1", "root", "hoyne", "vinyl");
			 if(!$link)	
			 {
				echo "Unable to connect to the database server.";
				exit();
			 }
			 if(!mysqli_set_charset($link, 'utf8')){
				echo "Unable to set database connection encoding.";
				exit(); 
			 }
			 if(!mysqli_query($link, "TRUNCATE TABLE vinyl")){
				echo "Delete Error";
			 }  
		 
			 $file = @fopen("records.csv", "r"); 
			 while(!feof($file)){
				$buffer = fgets($file, 4096); 
				list($artist, $album, $other) = explode(',', $buffer); 
				if($album == ''){
					continue; 
				}
				if(ord($other) != 13){  
					continue; 
				}
				$sql = "INSERT INTO vinyl (Artist, Album) VALUES ('" . $artist . "','" . $album . "')";
				mysqli_query($link, $sql); 
			 }
			 $query = "SELECT Artist, Album FROM vinyl ORDER BY RAND() LIMIT 1";
			 $record = mysqli_query($link, $query); 
				 
			 while($row = mysqli_fetch_array($record)){
				echo $row["Artist"] . " - " . $row["Album"];
			 }
			 ?>
			</p>
        
        <script>	
    		document.getElementById("p1").style.marginTop = (window.innerHeight/2) - 100 + "px";
    	</script>
    </body>
</html>

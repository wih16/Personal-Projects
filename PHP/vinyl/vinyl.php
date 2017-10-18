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
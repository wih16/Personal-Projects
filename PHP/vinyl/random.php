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

	 $query = "SELECT Artist, Album FROM vinyl ORDER BY RAND() LIMIT 1";
	 $record = mysqli_query($link, $query); 

	 while($row = mysqli_fetch_array($record)){
	 	if(substr($row["Artist"], -5) == ", The"){
	 		$row["Artist"] = substr($row["Artist"], -3) . " " . substr($row["Artist"], 0, -5);
	 	}
		echo json_encode($row["Artist"] . " - " . $row["Album"]);
	 }
?>
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
	 
	$style_left = "";/*"background: -webkit-linear-gradient(left, rgba(107, 177, 255, 0), rgba(107, 177, 255, .1)); background: -o-linear-gradient(left, rgba(107, 177, 255, 0), rgba(107, 177, 255, .1)); background: -moz-linear-gradient(left, rgba(107, 177, 255, 0), rgba(107, 177, 255, .1)); background: linear-gradient(left, rgba(107, 177, 255, 0), rgba(107, 177, 255, .1));";*/
	$style_right = "";/*"background: -webkit-linear-gradient(right, rgba(107, 177, 255, 0), rgba(107, 177, 255, .1)); background: -o-linear-gradient(right, rgba(107, 177, 255, 0), rgba(107, 177, 255, .1)); background: -moz-linear-gradient(right, rgba(107, 177, 255, 0), rgba(107, 177, 255, .1)); background: linear-gradient(right, rgba(107, 177, 255, 0), rgba(107, 177, 255, .1));";*/
	$return = "<table style='width: 100%; border-collapse: collapse; '><tr><th class='left' style='width: 50%; text-align: right; font-size: 20px; padding: 10px;" . $style_left ."'>Artist</th><th class='right' style='width: 50%; text-align: left; font-size: 20px; padding-left: 10px;" . $style_right . "'>Album</th></tr>";

	 $query = "SELECT Artist, Album FROM vinyl ORDER BY Artist ASC";
	 $record = mysqli_query($link, $query); 
	while($row = mysqli_fetch_array($record)){
	 	if(substr($row["Artist"], -5) == ", The"){
	 		$row["Artist"] = substr($row["Artist"], -3) . " " . substr($row["Artist"], 0, -5);
	 	}
		$return = $return . "<tr><td class='left' style='text-align: right; padding: 10px;" . $style_left ."'>" . $row["Artist"] . "</td><td class='right' style='padding: 10px;" . $style_right ."'>" . $row["Album"] . "</td></tr>";
	 }
	 $return = $return . "</table>";
	 echo json_encode($return); 

?>
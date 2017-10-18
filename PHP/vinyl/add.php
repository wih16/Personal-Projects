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
	 $artist = $_POST["artist"];
	 $album = $_POST["album"];
	 
	 $query = "INSERT INTO vinyl (Artist, Album) VALUES (\"" . $artist . "\", \"" . $album . "\")";	 
	 if(!mysqli_query($link, $query)){
	 	echo "Unable to add element"; 
	 	exit();
	 }	 
	 else{
	 	echo "New vinyl added"; 
	 }
?>
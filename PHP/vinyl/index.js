var check = 0; 
$(document).ready(function(){
	setTimeout(function(){
		$("#welcome_text_container").fadeIn(2000, function(){
			$("#welcome_text_container").animate({top : "20%"}, 1000, function(){
				$('#button_container').fadeIn(1000); 
			});
		});	
	}, 3750);
})
$(document).ready(function(){
	setTimeout(function(){
		$("#tint").fadeIn(2000);
	}, 1750);
})

/* Menu Button Calls */
/*random vinyl*/
$(document).ready(function(){
	$("#random").click(function(){
		if(check === 1){
			if($("#table").is(":visible")){
				$("#table").fadeOut("slow", function(){
					$('#vinyl').fadeIn(2000, function(){
						$("span").fadeIn();
					});
				});
			}
			else if($("#add_vinyl").is(":visible")){
				$("#add_vinyl").fadeOut("slow", function(){
					$('#add_vinyl').fadeOut(2000, function(){
						$('#vinyl').fadeIn(2000, function(){
							$("span").fadeIn();
						});
					});
				});
			}
		}
		else{
			check = 1; 
			$("#welcome_text_container").slideUp(500, function(){
				$("#button_container").height("10%");
			});
			$("#button_container").animate({top: "90%"}, {duration: 999, queue: false});
			$("#button_container").animate({left: "0%"}, {duration: 1000, queue: false});
			$("#button_container").animate({width: "100%"}, {duration: 1000, queue: false});
			$('#vinyl').fadeIn(2000, function(){
				$("span").fadeIn();
			});
		}
	});
})

/*view vinyls*/
$(document).ready(function(){
	$("#view").click(function(){
		if(check === 1){
			if($("#vinyl").is(":visible")){
				$("#vinyl").fadeOut("slow", function(){
					$('#table').fadeIn(2000);
				});
			}
			else if($("#add_vinyl").is(":visible")){
				$("#add_vinyl").fadeOut("slow", function(){
					$('#table').fadeIn(2000);
				});
			}
		}
		else{
			check = 1; 
			$("#welcome_text_container").slideUp(500, function(){
				$("#button_container").height("10%");
			});
			$("#button_container").animate({top: "90%"}, {duration: 999, queue: false});
			$("#button_container").animate({left: "0%"}, {duration: 1000, queue: false});
			$("#button_container").animate({width: "100%"}, {duration: 1000, queue: false});
			$('#table').fadeIn(2000);
		}
	});
})

/*add vinyl*/
$(document).ready(function(){
	$("#add").click(function(){
		if(check === 1){
			if($("#table").is(":visible")){
				$("#table").fadeOut("slow", function(){
					$('#add_vinyl').fadeIn(2000);
				});
			}
			else if($("#vinyl").is(":visible")){
				$("#vinyl").fadeOut("slow", function(){
					$('#add_vinyl').fadeIn(2000);
				});
			}
		}
		else{
			check = 1; 
			$("#welcome_text_container").slideUp(500, function(){
				$("#button_container").height("10%");
			});
			$("#button_container").animate({top: "90%"}, {duration: 999, queue: false});
			$("#button_container").animate({left: "0%"}, {duration: 1000, queue: false});
			$("#button_container").animate({width: "100%"}, {duration: 1000, queue: false});
			$('#add_vinyl').fadeIn(2000);
		}
	});
})


$(document).ready(function(){
	$("#random").on("click", random);
})
$(document).ready(function(){
	$("#view").on("click", function(){
		if(isEmpty($("#table"))){
			view();
		}	
	});
})
$(document).ready(function(){
	$(form).submit(function(e){
		$.ajax({
			type: "POST", 
			url: "add.php", 
			data: $(form).serialize(), 
			success: function(data){
				alert(data); 
			}
		});
		e.preventDefault(); 
		document.getElementById("form").reset(); 
	});
})

/* PHP Calls */
function random(){
	$.ajax({
			type: "POST",
			url: "random.php",
			success : function(json_data){
				var string = $.parseJSON(json_data);
				if(string.length > 7){
					$("span").html(string); 
				}
				else{
					random();
				}	
			}
		});
}

function view(){
	$.ajax({
			type: "POST",
			url: "view.php",
			success : function(json_data){
				var string = $.parseJSON(json_data);
				$("#table").html(string); 
			}
		});
		
	$(".css").css("border", "red");
}

/* Other */
function isEmpty( el ){
  return !$.trim(el.html());
}
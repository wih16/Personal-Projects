/*$(document).ready(function(){ 
    $('#enter').click(function(){
        var people = JSON.parse(localStorage.getItem('people'));
        var name = $('#usr').val();
        var email = $('#email').val();
        var pass = $('#pwd').val();

        var new_array = [name, email, pass];
        people.push(new_array);

        localStorage.people = JSON.stringify(people);
        
        delete localStorage.people;               
        console.log(localStorage.people);
        window.location.href = "index.html";   
    });
});*/
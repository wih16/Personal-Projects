$(document).ready(function(){
    $('#intro').hide();
    $('#googleMap_outer').css('opacity', '0');
    $('#click_outer').hide();
    $('#intro').fadeIn(2000, function(){
        $('#googleMap_outer').fadeTo(500, 1);
        $('#click_outer').fadeIn(500)
    });
});
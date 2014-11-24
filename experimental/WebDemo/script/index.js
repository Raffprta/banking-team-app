// Simple script for slider transitions.

// The images directory.
var directory = "images/";
// Image array.
var slider_images = ["slide_1.png", "slide_2.png", "slide_3.png", "slide_6.png", "slide_4.png", "slide_5.png", "slide_7.png"];
// The position it is at currently in the slider.
var transition_at = 0;

var slider_increment = function(offset){
    // Get the next slider image.
    transition_at = (transition_at + offset) % slider_images.length;
    // If the number falls below 0 (index out of bounds) set to the last array index.
    if(transition_at < 0){
        transition_at = slider_images.length - 1;
    }
    // JQuery animation!
    $("#slider").slideDown("slow", function() {
        $(this).fadeOut( "slow", function() {
            $(this).attr("src", directory + slider_images[transition_at]);
            $(this).fadeIn( "slow", function() {
                // The slider image is faded out, and then faded in.
            });
        });

    });
}

// When the left button is clicked then go to the previous image.
$("#goLeft").click(function() {
    slider_increment(-1);
});

// When the right button is clicked then go to the next image.
$("#goRight").click(function() {
    slider_increment(1);
});

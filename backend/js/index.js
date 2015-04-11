// Simple script for slider transitions.

// The position it is at currently in the slider.
var transition_at = 0;

/** Bug fix for slow internet connection or slow page serving:
 * such as the Uni webspace - preload all of the images using Javascript
 * and cache them to the browser.
 *
 * Images loaded from 1 -> n+1
 *
 * @param no_images The number of images to be loaded
 * @param img_name the prefix name of the image such as slide_
 * @param img_ext the file extension such as .png
 * @param directory where the images are loaded from like images/
 * @returns An Image array
 */
var preload = function(no_images, img_name, img_ext, directory){
    var images = [];
    for(var i = 0; i < no_images; i++) {
        images[i] = new Image();
        images[i].src = directory + img_name + (i+1) + img_ext;
    }
    return images;
}

// Image array.
var slider_images = preload(7, "slide_", ".png", "images/");

var slider_increment = function(offset){
    // Get the next slider image.
    transition_at = (transition_at + offset) % slider_images.length;
    // If the number falls below 0 (index out of bounds) set to the last array index.
    if(transition_at < 0){
        transition_at = slider_images.length - 1;
    }
    // JQuery animation!
    $("#slider").fadeOut("slow", function() {

        $(this).attr("src", slider_images[transition_at].src);

        $(this).fadeIn("slow", function() {

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

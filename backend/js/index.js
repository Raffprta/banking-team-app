// Simple script for slider transitions.

// The position it is at currently in the slider.
var transition_at = 0;

/** Bug fix for slow internet connection or slow page serving:
 * such as the Uni webspace - preload all of the images using Javascript
 * and cache them to the browser.
 *
 * Images loaded from 1 -> n+1
 *
 * -- Apologies for quick ugly code, it was 11pm when I realised this --
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
var slider_images = preload(5, "slide_", ".png", "images/");


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


slider_images_2 = preload(3, "sslide_", ".png", "images/");

var slider_increment_2 = function(offset){
    // Get the next slider image.
    transition_at = (transition_at + offset) % slider_images_2.length;
    // If the number falls below 0 (index out of bounds) set to the last array index.
    if(transition_at < 0){
        transition_at = slider_images_2.length - 1;
    }
    // JQuery animation!
    $("#slider2").fadeOut("slow", function() {

        $(this).attr("src", slider_images_2[transition_at].src);

        $(this).fadeIn("slow", function() {

        });
    });

}

// When the left button is clicked then go to the previous image.
$("#goLeft2").click(function() {
    slider_increment_2(-1);
});

// When the right button is clicked then go to the next image.
$("#goRight2").click(function() {
    slider_increment_2(1);
});

slider_images_g = preload(4, "gslide_", ".png", "images/");

var slider_increment_g = function(offset){
    // Get the next slider image.
    transition_at = (transition_at + offset) % slider_images_g.length;
    // If the number falls below 0 (index out of bounds) set to the last array index.
    if(transition_at < 0){
        transition_at = slider_images_g.length - 1;
    }
    // JQuery animation!
    $("#gslider").fadeOut("slow", function() {

        $(this).attr("src", slider_images_g[transition_at].src);

        $(this).fadeIn("slow", function() {

        });
    });

}

// When the left button is clicked then go to the previous image.
$("#ggoLeft").click(function() {
    slider_increment_g(-1);
});

// When the right button is clicked then go to the next image.
$("#ggoRight").click(function() {
    slider_increment_g(1);
});

slider_images_e = preload(4, "eslide_", ".png", "images/");

var slider_increment_e = function(offset){
    // Get the next slider image.
    transition_at = (transition_at + offset) % slider_images_e.length;
    // If the number falls below 0 (index out of bounds) set to the last array index.
    if(transition_at < 0){
        transition_at = slider_images_e.length - 1;
    }
    // JQuery animation!
    $("#eslider").fadeOut("slow", function() {

        $(this).attr("src", slider_images_e[transition_at].src);

        $(this).fadeIn("slow", function() {

        });
    });

}

// When the left button is clicked then go to the previous image.
$("#egoLeft").click(function() {
    slider_increment_e(-1);
});

// When the right button is clicked then go to the next image.
$("#egoRight").click(function() {
    slider_increment_e(1);
});



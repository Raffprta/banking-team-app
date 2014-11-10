<?php

require_once __DIR__.'/config.php';
require_once __DIR__.'/vendor/autoload.php';
require_once __DIR__.'/rb.php';

// Update request when app is installed in a subdirectory
$base = dirname($_SERVER['PHP_SELF']);
$trimmedBase = ltrim($base, '/');

// If we're not at the root of the server (Windows and Un*x), alter request
if ($trimmedBase !== '\\' && $trimmedBase !== '') {
    $_SERVER['REQUEST_URI'] = substr($_SERVER['REQUEST_URI'], strlen($base));
}

// Set the templates folder as our twig environment and make a Twig instance
$loader = new Twig_Loader_Filesystem('templates');
$twig = new Twig_Environment($loader);

// Add the global base, which will be used by the templates.
$twig->addGlobal('baseURL', BASE_URL);

// Init Klein router -- so far unused, but will be used in the final application.
$klein = new \Klein\Klein();

// Render the child page which extends base.twig with some dummy variables
// In the live example these would be pulled from the database
// If the user is logged in then we load in the logged in page, rather than the landing page


// Very simple Klein routing, this sets up the demo for the user to click Login/Logout.

$klein->respond('GET', '/index.php', function () {
    global $twig;
    return $twig->render('index.twig', array('loggedInUser' => false));
});

$klein->respond('GET', '/login', function () {
    global $twig;
    return $twig->render('logged_in_index.twig', array('loggedInUser' => true));
});

$klein->respond('GET', '/logout', function () {
    global $twig;
    return $twig->render('index.twig', array('loggedInUser' => false));
});

$klein->dispatch();




?>


<?php

// Gets protocol for the base -- this below code *should* hopefully automate getting the base.
$protocol = stripos($_SERVER['SERVER_PROTOCOL'],'https') === true ? 'https://' : 'http://';
define('BASE_URL', $protocol . $_SERVER['SERVER_NAME'] . ':' . $_SERVER['SERVER_PORT'] . $_SERVER['PHP_SELF']);

/* Temporarily comment these out, as they are not needed for the demo.
define('DATABASE_HOST', 'localhost');             // Database location
define('DATABASE_NAME', 'test');                  // Database name
define('DATABASE_USERNAME', 'team19');            // Database username
define('DATABASE_PASSWORD', '');                  // Database password
*/
?>


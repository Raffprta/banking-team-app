<?php

//================================================================================
//
// CSC2022 Team Project - Team 19 - Lloyds App Backend
//
// config.php - configuration variables and constant definitions
//
//================================================================================

define('BASE_URL', 'http://localhost:8080/');   // Root URL of this app (include https:// and trailing slash)
define('DATABASE_HOST', 'localhost');           // Database location
define('DATABASE_NAME', 'lloydsapp');           // Database name
define('DATABASE_USERNAME', 'root');            // Database username
define('DATABASE_PASSWORD', '');                // Database password

define('MINIMUM_PASSWORD_LENGTH', 6);           // Minimum password length
define('WEB_SESSION_EXPIRY_TIME', 3600);        // Time in seconds before a website user is automatically logged-out (60 * 60 = 1 hour)
define('DEVICE_TOKEN_EXPIRY_TIME', 2592000);    // Time in seconds before a device token expires (60 * 60 * 24 * 30 = 30 days)

define('REDBEAN_FREEZE_ENABLED', false);        // Freeze RedBean for production use (performance increase)
define('REDBEAN_DEBUG_ENABLED', false);         // RedBean debug mode

// Server API key from Google Developer console
define('GOOGLE_SERVER_API_KEY', 'AIzaSyAnOqe2hy_znuRnk6pfijuHXOFBH_Z6r6M');

//================================================================================
// DO NOT MODIFY BELOW THIS LINE
//================================================================================

// API keys for mobile apps
$apiKeys = array(
    'Android'  => '93d2e2620252446abbf63a82d3c3fb81'
);

// Codes for access rights
define('ACCESS_LEVEL_USER',             0);     // Mobile app user
define('ACCESS_LEVEL_ADMINISTRATOR',    10);    // Administrator

// Bank account types
define('ACCOUNT_TYPE_CURRENT',          0);     // Current account
define('ACCOUNT_TYPE_SAVINGS',          1);     // Savings account
define('ACCOUNT_TYPE_STUDENT',          2);     // Student account

// TODO: Tags for transaction types

// Klein flash message types
define('FLASH_SUCCESS',         'success');     // Success message
define('FLASH_ERROR',           'error');       // Error message
define('FLASH_INFO',            'info');        // Information message
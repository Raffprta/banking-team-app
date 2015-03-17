<?php

//================================================================================
//
// CSC2022 Team Project - Team 19 - Lloyds App Backend
//
// config.php - configuration variables and constant definitions
//
//================================================================================

define('BASE_URL', 'http://team19.ngrok.com/');        // Root URL of this app (include https:// and trailing slash)
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

// Tags for transaction types
define('TAG_FOODDRINK',                 0);     // Food and Drinks
define('TAG_CLOTHES',                   1);     // Clothes
define('TAG_WITHDRAWAL',                2);     // Withdrawals
define('TAG_ENTERTAINMENT',             3);     // Entertainment
define('TAG_OTHER',                     4);     // Other
define('TAG_UTILITY',                   5);     // Utilities
define('TAG_TRANSPORT',                 6);     // Transport
define('TAG_DONATION',                  7);     // Donation

// Klein flash message types
define('FLASH_SUCCESS',         'success');     // Success message
define('FLASH_ERROR',           'error');       // Error message
define('FLASH_INFO',            'info');        // Information message

// API status codes (for use in JSON status variable)
define('API_SUCCESS',           'success');     // Successful API call
define('API_ERROR',             'error');       // Error condition

// Push notification type tags
define('PUSH_TYPE_HEARTBEAT',        'heartbeat');  // Notifications informing the app a transaction has occurred etc.
define('PUSH_TYPE_OFFERS',           'offer');      // Notifications about special offers
define('PUSH_TYPE_INFO',             'info');       // General information notifications
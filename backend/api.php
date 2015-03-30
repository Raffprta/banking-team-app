<?php

//================================================================================
//
// CSC2022 Team Project - Team 19 - Lloyds App Backend
//
// api.php - public API calls for mobile apps and other external clients
//
//================================================================================

// To use the API, a client must:
//
// 1). Always present a valid API key header, eg. 'API-Key: << API KEY >>' (see config.php)
// 2). Authenticate by POSTing username/password to /api/authenticate to obtain a device token.
//     This should be stored on the client.
// 3). Present a 'Device-Token' header with the token along with the 'API-Key' header to use further API calls.
//
// All API calls return a JSON-encoded response with a 'status' field.
// If the request was successful, 'status' will contain 'success'.
// Otherwise 'status' will contain 'error', and another field, 'errorMessage' will contain the reason.

// TODO: Retrieve game state for a user
// TODO: Store/update game state for a user

// Log file tag
define('TAG', 'API: ');

//================================================================================
// Initialisation
//================================================================================
$this->respond(function($request, $response, $service) {
    // Replace error handlers so that we always return JSON in this namespace
    set_error_handler("apiErrorHandler");
    set_exception_handler("apiExceptionHandler");

    $this->onHTTPError(function ($code, $router) {
        if ($code === 404) {
            sendJSONError('The URL \'' . $router->request()->uri() . '\' does not exist.');
        } else {
            $error = 'Internal server error. Code: ' . $code;
            error_log(TAG . $error);
            sendJSONError($error);
        }
    });

    // Init database
    initRedBean();
});

//================================================================================
// API: Get bank accounts
//================================================================================
$this->respond('GET', '/accountdetails', function ($request, $response, $service) {
    error_log(TAG . 'Account details request from ' . $_SERVER['REMOTE_ADDR']);

    try {
        $user = checkAPIAuthentication($response);
        sendJSONResponse(array('accounts' => R::exportAll($user->xownAccountList)));
    } catch (Exception $e) {
        sendJSONError($e->getMessage());
    }
});

//================================================================================
// API: Get transactions
//================================================================================
$this->respond('GET', '/transactions/[i:accId]', function ($request, $response, $service) {
    error_log(TAG . 'Transactions request from ' . $_SERVER['REMOTE_ADDR']);
    try {
        $user = checkAPIAuthentication($response);

        // Make sure the account ID is valid and the user owns the account we're interested in
        $account = R::load('account', $request->$accId);
        if (is_null($account) || $account->userId !== $user->id) {
            sendJSONError('The account ID of the account to show transactions to/from was invalid.');
        }

        $transactions = null;

        // If a time period is specified, use it
        if (isset($_GET['periodFrom']) && isset($_GET['periodTo'])) {
            $transactions = R::find('transaction', '(from_account_id = ? OR to_account_id = ?) AND time >= ? AND time <= ?',
                array(
                    $_GET['accId'],
                    $_GET['accId'],
                    $_GET['periodFrom'],
                    $_GET['periodTo']
                )
            );
        }

        // Otherwise just return all of them
        else {
            $transactions = R::find('transaction', 'from_account_id = ? OR to_account_id = ?',
                array(
                    $_GET['accId'],
                    $_GET['accId']
                )
            );
        }

        sendJSONResponse(array('transactions' => R::exportAll($transactions)));
    } catch (Exception $e) {
        sendJSONError($e->getMessage());
    }
});

//================================================================================
// API: Transfer money
//================================================================================
$this->respond('POST', '/transfer', function ($request, $response, $service) {
    error_log(TAG . 'Transfer money request from ' . $_SERVER['REMOTE_ADDR']);
    try {
        $user = checkAPIAuthentication($response);
        $jsonRequest = json_decode($request->body(), true);

        // Validation
        if (is_null($jsonRequest)) {
            sendJSONError('Malformed request.');
        }

        if (!isset($jsonRequest['fromAccId'])) {
            sendJSONError('Your device did not send the account ID of the account to transfer from.');
        }

        if (!isset($jsonRequest['toAccNo'])) {
            sendJSONError('Your device did not send the account number of the account to transfer to.');
        }

        if (!isset($jsonRequest['toSortCode'])) {
            sendJSONError('Your device did not send the sort code of the account to transfer to.');
        }

        if (!isset($jsonRequest['amount'])) {
            sendJSONError('Your device did not send the amount of money to transfer.');
        }

        // Verification
        $fromAccount = R::load('account', $jsonRequest['fromAccId']);
        if (is_null($fromAccount) || $fromAccount->userId !== $user->id) {
            sendJSONError('The ID of the account to transfer from was invalid.');
        }

        $toAccount = R::findOne('account', 'sort_code = ? AND account_number = ?', array($jsonRequest['toSortCode'], $jsonRequest['toAccNo']));
        if (is_null($toAccount)) {
            sendJSONError('The account or sort code of the account to transfer to was invalid.');
        }

        $amount = intval($jsonRequest['amount']);
        if ($amount < 1) {
            sendJSONError('The amount to transfer was invalid.');
        }

        if (($fromAccount->balance + $fromAccount->overdraft) < $amount) {
            sendJSONError('There are insufficient funds available to complete this transaction.');
        }

        $reference = isset($jsonRequest['reference']) ? $jsonRequest['reference'] : "";
        $tag = isset($jsonRequest['tag']) ? $jsonRequest['tag'] : TAG_UNTAGGED;

        // Good to go...
        $fromAccount->balance -= $amount;
        $toAccount->balance += $amount;
        createTransaction($fromAccount, $toAccount, $amount, $reference, $tag);

        // Save all beans
        R::store($fromAccount);
        R::store($toAccount);

        sendJSONResponse(array());
    } catch (Exception $e) {
        sendJSONError($e->getMessage());
    }
});

//================================================================================
// API: Update Google Cloud Messaging ID
//      Input JSON fields: 'gcm_id'
//================================================================================
$this->respond('POST', '/updategcmid', function ($request, $response, $service) {
    error_log(TAG . 'GCM ID update request from ' . $_SERVER['REMOTE_ADDR']);
    try {
        $user = checkAPIAuthentication($response);
        $jsonRequest = json_decode($request->body(), true);

        if (is_null($jsonRequest) || !isset($jsonRequest['gcmId'])) {
            sendJSONError('Your device did not supply the new GCM ID.');
        }

        // Update the GCM ID
        $user->gcm_id = $jsonRequest['gcmId'];
        R::store($user);

        // Just send success status by passing an empty array
        sendJSONResponse(array());
    } catch (Exception $e) {
        sendJSONError($e->getMessage());
    }
});

//===============================================================================================================
// API: Update User Settings
//      Input JSON fields: 'email_notifications', 'push_notifications' value of 1 for true, value of 0 for false
//===============================================================================================================
$this->respond('POST', '/updatesettings', function ($request, $response, $service) {
    error_log(TAG . 'Settings update request from ' . $_SERVER['REMOTE_ADDR']);
    try {
        $user = checkAPIAuthentication($response);
        $jsonRequest = json_decode($request->body(), true);

        if (is_null($jsonRequest) || !isset($jsonRequest['emailNotifications']) || !isset($jsonRequest['pushNotifications'])) {
            sendJSONError('Your device did not supply the email and push settings.');
        }

        // Update the settings
        $user->emailNotifications = $jsonRequest['emailNotifications'];
        $user->pushNotifications = $jsonRequest['pushNotifications'];
        R::store($user);

        // Just send success status by passing an empty array
        sendJSONResponse(array());
    } catch (Exception $e) {
        sendJSONError($e->getMessage());
    }
});

//===============================================================================================================
// API: Update User's Linked Google Play Account
//      Input JSON fields: 'playId' which is the player id.
//===============================================================================================================
$this->respond('POST', '/updateplayid', function ($request, $response, $service) {
    error_log(TAG . 'Google Player ID update request from ' . $_SERVER['REMOTE_ADDR']);
    try {
        $user = checkAPIAuthentication($response);
        $jsonRequest = json_decode($request->body(), true);

        if (is_null($jsonRequest) || !isset($jsonRequest['playId'])) {
            sendJSONError('Your device did not supply the player id.');
        }

        // Update the settings
        $user->playerId = $jsonRequest['playId'];
        R::store($user);

        // Just send success status by passing an empty array
        sendJSONResponse(array());
    } catch (Exception $e) {
        sendJSONError($e->getMessage());
    }
});

//================================================================================
// API: Authenticate (get device token with valid username/password/secret chars)
//      Input JSON fields: 'username', 'password', 'security'
//      Output JSON fields: 'deviceToken'
//================================================================================
$this->respond('POST', '/authenticate', function ($request, $response, $service) {
    error_log(TAG . 'Login request from ' . $_SERVER['REMOTE_ADDR']);

    // Make sure a username and password have been supplied
    $loginData = json_decode($request->body(), true);

    if (!$loginData || !isset($loginData['username']) || !isset($loginData['password'])) {
        error_log(TAG . 'Access denied to ' . $_SERVER['REMOTE_ADDR'] . ': Username/password not supplied');
        sendJSONError('Your device did not supply a username and/or password.');
    }
    
    // Ensure a security code was also provided.
    if (!isset($loginData['secureChars'])) {
        error_log(TAG . 'Access denied to ' . $_SERVER['REMOTE_ADDR'] . ': Security code not supplied');
        sendJSONError('Your device did not supply a security code.');
    }

    // Attempt login
    $user = R::findOne('user', 'email = ?', array($loginData['username']));
    
    // Check username/password
    if (is_null($user) || !password_verify($loginData['password'], $user->password)) {
        error_log(TAG . 'Access denied to ' . $_SERVER['REMOTE_ADDR'] . ': Incorrect username or password.');
        sendJSONError('Incorrect username or password.');
        return;
    }
    
    // Get array within security field of the original JSON body
    $secureChars = $loginData['secureChars'];
    
    // Boolean to represent whether a security code was successfully validated
    $validated = false;
    
    // Get the set security code
    $securityCode = $user->security;

    // For every security digit, validate them
    foreach($secureChars as $secureChar) {
        $index = intval($secureChar['index']) - 1;

        // Check if the characters are the same
        if($securityCode[$index] ===  $secureChar['character']){
            $validated = true;
            continue;
        } else {
            // If at any point the validation fails, then exit the loop and mark the validation as failed.
            $validated = false;
            break;
        }
    }
    
    if (!$validated) {
        error_log(TAG . 'Access denied to ' . $_SERVER['REMOTE_ADDR'] . ': Incorrect security code digits.');
        sendJSONError('Incorrect security code digits.');
    } else {
        // Login successful; create and store device token information
        $deviceToken = R::dispense('devicetoken');
        $deviceToken->token = generateDeviceTokenHash($loginData);
        updateDeviceToken($deviceToken);

        // Associate token with user
        $user->xownDevicetokenList[] = $deviceToken;
        R::store($user);

        error_log(TAG . 'Access granted to ' . $_SERVER['REMOTE_ADDR'] . ': \'' . $loginData['username'] . '\' got device token ' . $deviceToken->token);
        sendJSONResponse(array('deviceToken' => $deviceToken->token));
    }
});

//================================================================================
// Error handlers
//================================================================================
function apiErrorHandler($errno, $errstr, $file, $line) {
    $error = 'Internal server error. Code ' . $errno . ': ' . $errstr;
    error_log($error);
    sendJSONError($error);
}

function apiExceptionHandler($exception) {
    $error = 'Internal server error. ' . $exception->getMessage();
    error_log($error);
    sendJSONError($error);
}

//================================================================================
// Utility functions
//================================================================================
function checkAPIKey() {
    global $apiKeys;

    if (!isset($_SERVER['HTTP_API_KEY'])) {
        error_log(TAG . 'No API key supplied by ' . $_SERVER['REMOTE_ADDR']);
        return false;
    }

    $clientName = array_search($_SERVER['HTTP_API_KEY'], $apiKeys);

    // See if the API key exists in the collection
    if ($clientName) {
        error_log(TAG . 'Valid API key (' . $clientName . ') supplied by ' . $_SERVER['REMOTE_ADDR']);
        return true;
    }

    // Access denied
    error_log(TAG . 'Invalid API key supplied by ' . $_SERVER['REMOTE_ADDR']);
    return false;
}

function checkDeviceToken() {
    // Expire old tokens
    $deviceTokens = R::findAll('deviceToken');
    foreach ($deviceTokens as $token) {
        if ($token->lastActivity < time() - DEVICE_TOKEN_EXPIRY_TIME) {
            R::trash($token);
        }
    }

    // Check to see if a token was supplied
    if (!isset($_SERVER['HTTP_DEVICE_TOKEN'])) {
        error_log(TAG . 'Access denied to ' . $_SERVER['REMOTE_ADDR'] . ': Device token was invalid');
        return null;
    }

    // Attempt to find the token
    $deviceToken = R::findOne('devicetoken', 'token = ?', array($_SERVER['HTTP_DEVICE_TOKEN']));
    if (is_null($deviceToken)) {
        error_log(TAG . 'Access denied to ' . $_SERVER['REMOTE_ADDR'] . ': Device token was invalid');
        return null;
    }

    // Try to find associated user
    $user = R::load('user', $deviceToken->user_id);

    // If user associated with the token no longer exists, destroy the token
    if (is_null($user)) {
        error_log(TAG . 'Access denied to ' . $_SERVER['REMOTE_ADDR'] . ': User no longer exists');
        R::trash($deviceToken);
        return null;
    }

    // Update access time, and IP address
    updateDeviceToken($deviceToken);

    // Return the associated user
    return $user;
}

function checkAPIAuthentication($response) {
    error_log(TAG . 'Token authentication request from ' . $_SERVER['REMOTE_ADDR']);

    if (!checkAPIKey()) {
        sendJSONError('The API key supplied by your device was invalid. Please update your app, or contact support.');
    }

    $user = checkDeviceToken();
    if (is_null($user)) {
        sendJSONError('Your device did not supply a valid authentication token.');
    }

    error_log(TAG . 'Successful token authentication from ' . $_SERVER['REMOTE_ADDR']);
    return $user;
}

function generateDeviceTokenHash($loginData) {
    return hash('sha256', $_SERVER['HTTP_USER_AGENT'] . $loginData['password'] . time());
}

function updateDeviceToken($deviceToken) {
    $deviceToken->lastActivity = time();
    $deviceToken->lastIPAddress = $_SERVER['REMOTE_ADDR'];
    R::store($deviceToken);
}

function sendJSONResponse($data) {
    $body = array_merge(
        $data,
        array('status' => API_SUCCESS)
    );

    echo json_encode($body);
    exit;
}

function sendJSONError($reason) {
    $body = array(
        'errorMessage' => $reason,
        'status' => API_ERROR
    );

    echo json_encode($body);
    exit;
}

// Send a push notification to one or more registered devices using Google Cloud Messaging
function sendGoogleCloudMessage($message, $ids) {
    // URL to GCM API
    $url = 'https://android.googleapis.com/gcm/send';

    // POST variables
    $post = array(
        'registration_ids'  => $ids,
        'data' => array('message' => $message)
    );

    // HTTP request headers
    $headers = array('Authorization: key=' . GOOGLE_SERVER_API_KEY, 'Content-Type: application/json');

    // Initialize curl
    $ch = curl_init();

    // Set URL to GCM API URL
    curl_setopt($ch, CURLOPT_URL, $url);

    // Set request method to POST
    curl_setopt($ch, CURLOPT_POST, true);

    // Set custom headers
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);

    // Get the response back as string instead of printing it
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

    // Set post data as JSON
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($post));

    // Send the push!
    $result = curl_exec($ch);

    // Get any curl errors
    $errno = curl_errno($ch);
    $errmsg = curl_error($ch);

    // Close curl handle
    curl_close($ch);

    // If there was an error, return it, otherwise return Google response
    return $errno ? $errmsg : $result;
}